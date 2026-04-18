package com.nexonsalary.service;

import com.nexonsalary.dto.BalanceUploadListItemDto;
import com.nexonsalary.dto.ImportSummaryDto;
import com.nexonsalary.dto.MonthlyMemberBalanceDto;
import com.nexonsalary.model.Agent;
import com.nexonsalary.model.BalanceUpload;
import com.nexonsalary.model.Member;
import com.nexonsalary.model.MemberAccount;
import com.nexonsalary.model.MonthlyMemberBalance;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class MonthlyBalanceService {

    public ImportSummaryDto saveMonthlyBalances(List<MonthlyMemberBalanceDto> balances, String sourceFileName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        ImportSummaryDto summary = new ImportSummaryDto();

        try {
            summary.setImportedRows(balances.size());

            BalanceUpload upload = new BalanceUpload(
                    sourceFileName,
                    balances.size(),
                    0,
                    0,
                    0,
                    0,
                    0
            );
            session.persist(upload);

            for (MonthlyMemberBalanceDto dto : balances) {
                FindOrCreateAgentResult agentResult =
                        findOrCreateAgent(session, dto.getAgentCode(), dto.getAgentName());

                FindOrCreateMemberResult memberResult =
                        findOrCreateMember(session, dto.getNationalId(), dto.getMemberName());

                FindOrCreateAccountResult accountResult =
                        findOrCreateAccount(session, dto.getAccountNumber(), memberResult.member(), agentResult.agent());

                MonthlyMemberBalance existing = findExistingBalance(
                        session,
                        accountResult.account().getId(),
                        dto.getBalanceDate()
                );

                if (agentResult.created()) {
                    summary.setCreatedAgents(summary.getCreatedAgents() + 1);
                }

                if (memberResult.created()) {
                    summary.setCreatedMembers(summary.getCreatedMembers() + 1);
                }

                if (accountResult.created()) {
                    summary.setCreatedAccounts(summary.getCreatedAccounts() + 1);
                }

                if (existing == null) {
                    MonthlyMemberBalance balance = new MonthlyMemberBalance(
                            memberResult.member(),
                            agentResult.agent(),
                            accountResult.account(),
                            upload,
                            dto.getBalanceDate(),
                            dto.getTotalBalance(),
                            sourceFileName
                    );

                    session.persist(balance);
                    summary.setCreatedBalances(summary.getCreatedBalances() + 1);
                } else {
                    existing.setMember(memberResult.member());
                    existing.setAgent(agentResult.agent());
                    existing.setAccount(accountResult.account());
                    existing.setUpload(upload);
                    existing.setTotalBalance(dto.getTotalBalance());
                    existing.setSourceFileName(sourceFileName);
                    session.merge(existing);
                    summary.setUpdatedBalances(summary.getUpdatedBalances() + 1);
                }
            }

            upload.setCreatedAgents(summary.getCreatedAgents());
            upload.setCreatedMembers(summary.getCreatedMembers());
            upload.setCreatedAccounts(summary.getCreatedAccounts());
            upload.setCreatedBalances(summary.getCreatedBalances());
            upload.setUpdatedBalances(summary.getUpdatedBalances());
            session.merge(upload);

            summary.setUploadId(upload.getId());

            transaction.commit();
            return summary;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public List<BalanceUploadListItemDto> getAllUploads() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<BalanceUpload> uploads = session.createQuery("""
                    from BalanceUpload u
                    order by u.uploadedAt desc, u.id desc
                    """, BalanceUpload.class).getResultList();

            return uploads.stream()
                    .map(upload -> new BalanceUploadListItemDto(
                            upload.getId(),
                            upload.getFileName(),
                            upload.getUploadedAt() != null ? upload.getUploadedAt().toString() : "",
                            upload.getImportedRows(),
                            upload.getCreatedAgents(),
                            upload.getCreatedMembers(),
                            upload.getCreatedAccounts(),
                            upload.getCreatedBalances(),
                            upload.getUpdatedBalances()
                    ))
                    .toList();
        }
    }

    public void deleteUpload(Long uploadId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            BalanceUpload upload = session.find(BalanceUpload.class, uploadId);
            if (upload == null) {
                throw new IllegalArgumentException("Upload not found");
            }

            session.createMutationQuery("""
                    delete from MonthlyMemberBalance
                    where upload.id = :uploadId
                    """)
                    .setParameter("uploadId", uploadId)
                    .executeUpdate();

            session.remove(upload);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private FindOrCreateAgentResult findOrCreateAgent(Session session, String agentCode, String agentName) {
        Query<Agent> query = session.createQuery(
                "from Agent where agentCode = :agentCode", Agent.class
        );
        query.setParameter("agentCode", agentCode);

        Agent agent = query.uniqueResult();

        if (agent == null) {
            agent = new Agent(agentCode, agentName);
            session.persist(agent);
            return new FindOrCreateAgentResult(agent, true);
        } else {
            if (agentName != null && !agentName.isBlank()) {
                agent.setAgentName(agentName);
                session.merge(agent);
            }
            return new FindOrCreateAgentResult(agent, false);
        }
    }

    private FindOrCreateMemberResult findOrCreateMember(Session session, String nationalId, String memberName) {
        Query<Member> query = session.createQuery(
                "from Member where nationalId = :nationalId", Member.class
        );
        query.setParameter("nationalId", nationalId);

        Member member = query.uniqueResult();

        if (member == null) {
            member = new Member(nationalId, memberName);
            session.persist(member);
            return new FindOrCreateMemberResult(member, true);
        } else {
            if (memberName != null && !memberName.isBlank()) {
                member.setFullName(memberName);
                session.merge(member);
            }
            return new FindOrCreateMemberResult(member, false);
        }
    }

    private FindOrCreateAccountResult findOrCreateAccount(Session session,
                                                          String accountNumber,
                                                          Member member,
                                                          Agent agent) {
        Query<MemberAccount> query = session.createQuery(
                "from MemberAccount where accountNumber = :accountNumber",
                MemberAccount.class
        );
        query.setParameter("accountNumber", accountNumber);

        MemberAccount account = query.uniqueResult();

        if (account == null) {
            account = new MemberAccount(accountNumber, member, agent);
            session.persist(account);
            return new FindOrCreateAccountResult(account, true);
        } else {
            account.setMember(member);
            account.setAgent(agent);
            session.merge(account);
            return new FindOrCreateAccountResult(account, false);
        }
    }

    private MonthlyMemberBalance findExistingBalance(Session session,
                                                     Long accountId,
                                                     LocalDate balanceDate) {
        Query<MonthlyMemberBalance> query = session.createQuery("""
                from MonthlyMemberBalance
                where account.id = :accountId
                  and balanceDate = :balanceDate
                """, MonthlyMemberBalance.class);

        query.setParameter("accountId", accountId);
        query.setParameter("balanceDate", balanceDate);

        return query.uniqueResult();
    }

    private record FindOrCreateAgentResult(Agent agent, boolean created) {
    }

    private record FindOrCreateMemberResult(Member member, boolean created) {
    }

    private record FindOrCreateAccountResult(MemberAccount account, boolean created) {
    }
}