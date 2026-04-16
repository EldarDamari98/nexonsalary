package com.nexonsalary.service;

import com.nexonsalary.dto.AgentCommissionSummaryDto;
import com.nexonsalary.dto.CommissionTransactionDto;
import com.nexonsalary.model.CommissionDirection;
import com.nexonsalary.model.CommissionReason;
import com.nexonsalary.model.CommissionTransaction;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class CommissionQueryService {

    public List<AgentCommissionSummaryDto> getSummaryForMonth(LocalDate month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<CommissionTransaction> transactions = session.createQuery(
                    "from CommissionTransaction ct " +
                    "join fetch ct.agent join fetch ct.member join fetch ct.account " +
                    "where ct.balanceDate = :month",
                    CommissionTransaction.class
            ).setParameter("month", month).list();

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
                CommissionReason reason = tx.getReason();

                if (reason == CommissionReason.SCOPE_NEW) {
                    summary.setScopeNew(summary.getScopeNew().add(amount));
                } else if (reason == CommissionReason.SCOPE_DELTA) {
                    summary.setScopeDelta(summary.getScopeDelta().add(amount));
                } else if (reason == CommissionReason.SCOPE_CLAWBACK) {
                    summary.setClawbacks(summary.getClawbacks().add(amount));
                } else if (reason == CommissionReason.NIFRA) {
                    summary.setNifra(summary.getNifra().add(amount));
                }

                summary.setTransactionCount(summary.getTransactionCount() + 1);
            }

            for (AgentCommissionSummaryDto s : summaryByAgent.values()) {
                s.setNetCommission(
                        s.getScopeNew()
                                .add(s.getScopeDelta())
                                .add(s.getNifra())
                                .subtract(s.getClawbacks())
                );
            }

            return new ArrayList<>(summaryByAgent.values());
        }
    }

    public List<CommissionTransactionDto> getTransactionsForMonth(LocalDate month, Long agentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from CommissionTransaction ct " +
                         "join fetch ct.agent join fetch ct.member left join fetch ct.account " +
                         "where ct.balanceDate = :month";
            if (agentId != null) {
                hql += " and ct.agent.id = :agentId";
            }
            hql += " order by ct.agent.agentCode, ct.reason";

            var query = session.createQuery(hql, CommissionTransaction.class)
                    .setParameter("month", month);
            if (agentId != null) {
                query.setParameter("agentId", agentId);
            }

            return query.list().stream().map(this::toDto).toList();
        }
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
}
