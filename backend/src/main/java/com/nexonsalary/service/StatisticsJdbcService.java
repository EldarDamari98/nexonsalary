package com.nexonsalary.service;

import com.nexonsalary.dto.*;
import com.nexonsalary.util.DbConnectionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StatisticsJdbcService {

    public StatisticsOverviewDto getOverview() {
        String sql = """
                select
                    (select coalesce(sum(total_balance), 0) from monthly_member_balances) as total_assets,
                    (select coalesce(sum(
                        case
                            when direction = 'DEBIT' then -commission_amount
                            else commission_amount
                        end
                    ), 0) from commission_transactions) as total_commission_paid,
                    (select count(*) from commission_transactions) as total_transactions,
                    (select count(*) from agents where active = true) as active_agents
                """;

        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                return new StatisticsOverviewDto(
                        rs.getBigDecimal("total_assets"),
                        rs.getBigDecimal("total_commission_paid"),
                        rs.getLong("total_transactions"),
                        rs.getLong("active_agents")
                );
            }

            return new StatisticsOverviewDto(BigDecimal.ZERO, BigDecimal.ZERO, 0, 0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load statistics overview", e);
        }
    }

    public List<MonthlyTrendPointDto> getAssetsTrend() {
        String sql = """
                select
                    date_format(balance_date, '%Y-%m') as month,
                    sum(total_balance) as value
                from monthly_member_balances
                group by date_format(balance_date, '%Y-%m')
                order by month
                """;

        List<MonthlyTrendPointDto> result = new ArrayList<>();

        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                result.add(new MonthlyTrendPointDto(
                        rs.getString("month"),
                        rs.getBigDecimal("value")
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load assets trend", e);
        }
    }

    public List<MonthlyTrendPointDto> getCommissionTrend() {
        String sql = """
                select
                    date_format(balance_date, '%Y-%m') as month,
                    sum(
                        case
                            when direction = 'DEBIT' then -commission_amount
                            else commission_amount
                        end
                    ) as value
                from commission_transactions
                group by date_format(balance_date, '%Y-%m')
                order by month
                """;

        List<MonthlyTrendPointDto> result = new ArrayList<>();

        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                result.add(new MonthlyTrendPointDto(
                        rs.getString("month"),
                        rs.getBigDecimal("value")
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load commission trend", e);
        }
    }

    public List<AgentPerformanceDto> getTopAgents() {
        String sql = """
                select
                    a.id as agent_id,
                    a.agent_code,
                    a.agent_name,
                    coalesce(sum(
                        case
                            when ct.direction = 'DEBIT' then -ct.commission_amount
                            else ct.commission_amount
                        end
                    ), 0) as net_commission
                from agents a
                join commission_transactions ct on ct.agent_id = a.id
                group by a.id, a.agent_code, a.agent_name
                order by net_commission desc
                limit 10
                """;

        List<AgentPerformanceDto> result = new ArrayList<>();

        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                result.add(new AgentPerformanceDto(
                        rs.getLong("agent_id"),
                        rs.getString("agent_code"),
                        rs.getString("agent_name"),
                        rs.getBigDecimal("net_commission")
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load top agents", e);
        }
    }

    public List<ReasonBreakdownDto> getReasonBreakdown() {
        String sql = """
                select
                    reason,
                    count(*) as count
                from commission_transactions
                group by reason
                order by count desc
                """;

        List<ReasonBreakdownDto> result = new ArrayList<>();

        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                result.add(new ReasonBreakdownDto(
                        rs.getString("reason"),
                        rs.getLong("count")
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load reason breakdown", e);
        }
    }

    public List<ClientMovementDto> getClientMovement() {
        String sql = """
                select
                    month,
                    sum(new_clients) as new_clients,
                    sum(left_clients) as left_clients,
                    sum(transferred_clients) as transferred_clients
                from (
                    select
                        date_format(balance_date, '%Y-%m') as month,
                        count(case when reason = 'PERIMETER_FEE_NEW' then 1 end) as new_clients,
                        0 as left_clients,
                        0 as transferred_clients
                    from commission_transactions
                    group by date_format(balance_date, '%Y-%m')

                    union all

                    select
                        date_format(leave_date, '%Y-%m') as month,
                        0 as new_clients,
                        count(*) as left_clients,
                        0 as transferred_clients
                    from client_agent_history
                    where leave_date is not null
                    group by date_format(leave_date, '%Y-%m')
                ) movement
                group by month
                order by month
                """;

        List<ClientMovementDto> result = new ArrayList<>();

        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                result.add(new ClientMovementDto(
                        rs.getString("month"),
                        rs.getLong("new_clients"),
                        rs.getLong("left_clients"),
                        rs.getLong("transferred_clients")
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load client movement", e);
        }
    }
}