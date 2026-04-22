package com.nexonsalary.service;

import com.nexonsalary.dto.AgentCommissionSummaryDto;
import com.nexonsalary.dto.CommissionTransactionDto;
import com.nexonsalary.dto.PagedCommissionTransactionsResponseDto;
import com.nexonsalary.model.ClientStatus;
import com.nexonsalary.model.CommissionDirection;
import com.nexonsalary.model.CommissionReason;
import com.nexonsalary.model.CommissionTransaction;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class CommissionQueryService {

    public List<AgentCommissionSummaryDto> getSummaryForMonth(LocalDate firstDay) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<CommissionTransaction> transactions = session.createQuery(
                    "from CommissionTransaction ct " +
                            "join fetch ct.agent join fetch ct.member join fetch ct.account " +
                            "where ct.balanceDate = :firstDay",
                    CommissionTransaction.class
            ).setParameter("firstDay", firstDay).list();

            return aggregateSummary(transactions);
        }
    }

    public List<CommissionTransactionDto> getTransactionsForMonth(LocalDate firstDay, Long agentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from CommissionTransaction ct " +
                    "join fetch ct.agent join fetch ct.member left join fetch ct.account " +
                    "where ct.balanceDate = :firstDay";
            if (agentId != null) {
                hql += " and ct.agent.id = :agentId";
            }
            hql += " order by ct.agent.agentCode, ct.reason";

            var query = session.createQuery(hql, CommissionTransaction.class)
                    .setParameter("firstDay", firstDay);

            if (agentId != null) {
                query.setParameter("agentId", agentId);
            }

            return query.list().stream().map(this::toDto).toList();
        }
    }

    public List<AgentCommissionSummaryDto> getExplorerSummary(String month,
                                                              String period,
                                                              String search,
                                                              Long agentId,
                                                              String reason,
                                                              String direction) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            QueryParts parts = buildQueryParts(month, period, search, agentId, reason, direction);

            Query<CommissionTransaction> query = session.createQuery(
                    "from CommissionTransaction ct " +
                            "join fetch ct.agent ag " +
                            "join fetch ct.member m " +
                            "left join fetch ct.account a " +
                            parts.whereClause +
                            " order by ag.agentName asc, ct.balanceDate desc, ct.id desc",
                    CommissionTransaction.class
            );

            applyParams(query, parts.params);
            List<CommissionTransaction> transactions = query.list();

            return aggregateSummary(transactions);
        }
    }

    public PagedCommissionTransactionsResponseDto getExplorerTransactions(String month,
                                                                          String period,
                                                                          String search,
                                                                          Long agentId,
                                                                          String reason,
                                                                          String direction,
                                                                          int page,
                                                                          int size,
                                                                          String sortBy,
                                                                          String sortDirection) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            int safePage = Math.max(page, 1);
            int safeSize = Math.max(size, 1);

            QueryParts parts = buildQueryParts(month, period, search, agentId, reason, direction);
            String orderBy = buildOrderBy(sortBy, sortDirection);

            Query<CommissionTransaction> query = session.createQuery(
                    "from CommissionTransaction ct " +
                            "join fetch ct.agent ag " +
                            "join fetch ct.member m " +
                            "left join fetch ct.account a " +
                            parts.whereClause +
                            orderBy,
                    CommissionTransaction.class
            );

            applyParams(query, parts.params);
            query.setFirstResult((safePage - 1) * safeSize);
            query.setMaxResults(safeSize);

            List<CommissionTransactionDto> items = query.list()
                    .stream()
                    .map(this::toDto)
                    .toList();

            Query<Long> countQuery = session.createQuery(
                    "select count(ct.id) " +
                            "from CommissionTransaction ct " +
                            "join ct.agent ag " +
                            "join ct.member m " +
                            "left join ct.account a " +
                            parts.whereClause,
                    Long.class
            );

            applyParams(countQuery, parts.params);

            long totalItems = Optional.ofNullable(countQuery.uniqueResult()).orElse(0L);
            int totalPages = (int) Math.ceil((double) totalItems / safeSize);

            return new PagedCommissionTransactionsResponseDto(
                    items,
                    safePage,
                    safeSize,
                    totalItems,
                    totalPages
            );
        }
    }

    private List<AgentCommissionSummaryDto> aggregateSummary(List<CommissionTransaction> transactions) {
        Map<Long, AgentCommissionSummaryDto> summaryByAgent = new LinkedHashMap<>();

        for (CommissionTransaction tx : transactions) {
            Long agentId = tx.getAgent().getId();

            AgentCommissionSummaryDto summary = summaryByAgent.computeIfAbsent(agentId, id -> {
                AgentCommissionSummaryDto s = new AgentCommissionSummaryDto();
                s.setAgentId(id);
                s.setAgentCode(tx.getAgent().getAgentCode());
                s.setAgentName(tx.getAgent().getAgentName());
                return s;
            });

            BigDecimal amount = tx.getCommissionAmount();
            CommissionReason txReason = tx.getReason();

            if (txReason == CommissionReason.PERIMETER_FEE_NEW) {
                summary.setPerimeterFeeNew(summary.getPerimeterFeeNew().add(amount));
            } else if (txReason == CommissionReason.PERIMETER_FEE_DELTA) {
                summary.setPerimeterFeeDelta(summary.getPerimeterFeeDelta().add(amount));
            } else if (txReason == CommissionReason.PERIMETER_FEE_CLAWBACK) {
                summary.setClawbacks(summary.getClawbacks().add(amount));
            } else if (txReason == CommissionReason.TRAIL_COMMISSION) {
                summary.setTrailCommission(summary.getTrailCommission().add(amount));
            }

            summary.setTransactionCount(summary.getTransactionCount() + 1);
        }

        for (AgentCommissionSummaryDto summary : summaryByAgent.values()) {
            summary.setNetCommission(
                    summary.getPerimeterFeeNew()
                            .add(summary.getPerimeterFeeDelta())
                            .add(summary.getTrailCommission())
                            .subtract(summary.getClawbacks())
            );
        }

        return new ArrayList<>(summaryByAgent.values());
    }

    private QueryParts buildQueryParts(String month,
                                       String period,
                                       String search,
                                       Long agentId,
                                       String reason,
                                       String direction) {
        String normalizedPeriod = normalizePeriod(period);

        StringBuilder where = new StringBuilder(" where 1=1 ");
        Map<String, Object> params = new HashMap<>();

        DateRange range = resolveDateRange(month, normalizedPeriod);
        if (range.startDate != null && range.endDateExclusive != null) {
            where.append(" and ct.balanceDate >= :startDate and ct.balanceDate < :endDate ");
            params.put("startDate", range.startDate);
            params.put("endDate", range.endDateExclusive);
        }

        if (search != null && !search.isBlank()) {
            where.append("""
                     and (
                        lower(m.fullName) like :search
                        or lower(m.nationalId) like :search
                        or lower(a.accountNumber) like :search
                        or lower(ag.agentName) like :search
                        or lower(ag.agentCode) like :search
                     )
                    """);
            params.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (agentId != null) {
            where.append(" and ag.id = :agentId ");
            params.put("agentId", agentId);
        }

        if (reason != null && !reason.isBlank()) {
            try {
                params.put("reason", CommissionReason.valueOf(reason.trim().toUpperCase()));
                where.append(" and ct.reason = :reason ");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid reason filter");
            }
        }

        if (direction != null && !direction.isBlank()) {
            try {
                params.put("direction", CommissionDirection.valueOf(direction.trim().toUpperCase()));
                where.append(" and ct.direction = :direction ");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid direction filter");
            }
        }

        return new QueryParts(where.toString(), params);
    }

    private void applyParams(Query<?> query, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    private String buildOrderBy(String sortBy, String sortDirection) {
        String direction = "asc".equalsIgnoreCase(sortDirection) ? "asc" : "desc";

        return switch (sortBy) {
            case "memberName" -> " order by m.fullName " + direction + ", ct.id desc";
            case "agentName" -> " order by ag.agentName " + direction + ", ct.id desc";
            case "commissionAmount" -> " order by ct.commissionAmount " + direction + ", ct.id desc";
            case "reason" -> " order by ct.reason " + direction + ", ct.id desc";
            default -> " order by ct.balanceDate " + direction + ", ct.id desc";
        };
    }

    private String normalizePeriod(String period) {
        if (period == null || period.isBlank()) {
            return "MONTH";
        }

        return switch (period.trim().toUpperCase()) {
            case "MONTH", "LAST_3_MONTHS", "LAST_6_MONTHS", "LAST_12_MONTHS", "ALL_TIME" -> period.trim().toUpperCase();
            default -> throw new IllegalArgumentException("Invalid period filter");
        };
    }

    private DateRange resolveDateRange(String month, String period) {
        if ("ALL_TIME".equals(period)) {
            return new DateRange(null, null);
        }

        if ("MONTH".equals(period)) {
            if (month == null || month.isBlank()) {
                throw new IllegalArgumentException("month is required when period is MONTH");
            }

            try {
                YearMonth yearMonth = YearMonth.parse(month);
                return new DateRange(
                        yearMonth.atDay(1),
                        yearMonth.plusMonths(1).atDay(1)
                );
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid month format. Use YYYY-MM");
            }
        }

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = switch (period) {
            case "LAST_3_MONTHS" -> currentMonth.minusMonths(2);
            case "LAST_6_MONTHS" -> currentMonth.minusMonths(5);
            case "LAST_12_MONTHS" -> currentMonth.minusMonths(11);
            default -> currentMonth;
        };

        return new DateRange(
                startMonth.atDay(1),
                currentMonth.plusMonths(1).atDay(1)
        );
    }

    private CommissionTransactionDto toDto(CommissionTransaction tx) {
        CommissionTransactionDto dto = new CommissionTransactionDto();
        dto.setId(tx.getId());
        dto.setMemberName(tx.getMember().getFullName());
        dto.setNationalId(tx.getMember().getNationalId());
        dto.setAccountNumber(tx.getAccount() != null ? tx.getAccount().getAccountNumber() : null);
        dto.setAgentCode(tx.getAgent().getAgentCode());
        dto.setAgentName(tx.getAgent().getAgentName());
        dto.setBalanceDate(tx.getBalanceDate().toString());
        dto.setPreviousBalance(tx.getPreviousBalance());
        dto.setCurrentBalance(tx.getCurrentBalance());
        dto.setDeltaBalance(tx.getDeltaBalance());
        dto.setCommissionRate(tx.getCommissionRate());
        dto.setCommissionAmount(tx.getCommissionAmount());
        dto.setDirection(tx.getDirection().name());
        dto.setReason(tx.getReason().name());
        return dto;
    }

    private record QueryParts(String whereClause, Map<String, Object> params) {
    }

    private record DateRange(LocalDate startDate, LocalDate endDateExclusive) {
    }
}