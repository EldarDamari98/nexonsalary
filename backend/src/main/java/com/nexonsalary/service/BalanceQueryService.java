package com.nexonsalary.service;

import com.nexonsalary.dto.BalanceListItemDto;
import com.nexonsalary.dto.BalanceSummaryDto;
import com.nexonsalary.dto.PagedBalanceResponseDto;
import com.nexonsalary.model.MonthlyMemberBalance;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class BalanceQueryService {

    public PagedBalanceResponseDto getBalances(int page,
                                               int size,
                                               String search,
                                               String agent,
                                               String date,
                                               String sortBy,
                                               String sortDirection) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String whereClause = """
                    where (:search is null or lower(m.fullName) like :search
                        or lower(m.nationalId) like :search
                        or lower(a.accountNumber) like :search
                        or lower(ag.agentName) like :search)
                      and (:agent is null or ag.agentName = :agent)
                      and (:date is null or cast(b.balanceDate as string) = :date)
                    """;

            String orderBy = buildOrderBy(sortBy, sortDirection);

            Query<MonthlyMemberBalance> query = session.createQuery("""
                    select b
                    from MonthlyMemberBalance b
                    join b.member m
                    join b.account a
                    join b.agent ag
                    """ + whereClause + orderBy,
                    MonthlyMemberBalance.class
            );

            applyParams(query, search, agent, date);

            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);

            List<BalanceListItemDto> items = query.getResultList()
                    .stream()
                    .map(balance -> new BalanceListItemDto(
                            balance.getId(),
                            balance.getMember().getFullName(),
                            balance.getMember().getNationalId(),
                            balance.getAccount().getAccountNumber(),
                            balance.getAgent().getAgentName(),
                            balance.getBalanceDate().toString(),
                            balance.getTotalBalance()
                    ))
                    .toList();

            Query<Long> countQuery = session.createQuery("""
                    select count(b.id)
                    from MonthlyMemberBalance b
                    join b.member m
                    join b.account a
                    join b.agent ag
                    """ + whereClause,
                    Long.class
            );

            applyParams(countQuery, search, agent, date);

            long totalItems = countQuery.uniqueResult();
            int totalPages = (int) Math.ceil((double) totalItems / size);

            BalanceSummaryDto summary = getSummary(session, whereClause, search, agent, date);

            return new PagedBalanceResponseDto(
                    items,
                    page,
                    size,
                    totalItems,
                    totalPages,
                    summary
            );
        }
    }

    private BalanceSummaryDto getSummary(Session session,
                                         String whereClause,
                                         String search,
                                         String agent,
                                         String date) {

        Query<Object[]> query = session.createQuery("""
                select
                    count(b.id),
                    count(distinct m.nationalId),
                    count(distinct a.accountNumber),
                    coalesce(sum(b.totalBalance), 0)
                from MonthlyMemberBalance b
                join b.member m
                join b.account a
                join b.agent ag
                """ + whereClause,
                Object[].class
        );

        applyParams(query, search, agent, date);

        Object[] row = query.uniqueResult();

        return new BalanceSummaryDto(
                ((Long) row[0]),
                ((Long) row[1]),
                ((Long) row[2]),
                (BigDecimal) row[3]
        );
    }

    private void applyParams(Query<?> query,
                             String search,
                             String agent,
                             String date) {

        query.setParameter(
                "search",
                search == null || search.isBlank()
                        ? null
                        : "%" + search.toLowerCase() + "%"
        );

        query.setParameter(
                "agent",
                agent == null || agent.isBlank() ? null : agent
        );

        query.setParameter(
                "date",
                date == null || date.isBlank() ? null : date
        );
    }

    private String buildOrderBy(String sortBy, String sortDirection) {
        String direction = "asc".equalsIgnoreCase(sortDirection) ? "asc" : "desc";

        return switch (sortBy) {
            case "memberName" -> " order by m.fullName " + direction;
            case "totalBalance" -> " order by b.totalBalance " + direction;
            default -> " order by b.balanceDate " + direction + ", b.id desc";
        };
    }
}