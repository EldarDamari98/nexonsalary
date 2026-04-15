import { useEffect, useState } from "react";
import { getDashboardSummary } from "../api/dashboardApi";

function DashboardPage() {
  const [summary, setSummary] = useState({
    totalAgents: 0,
    totalMembers: 0,
    totalAccounts: 0,
    totalAssets: 0,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadSummary = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getDashboardSummary();
      setSummary(data);
    } catch (err) {
      setError(err.message || "Failed to load dashboard");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSummary();
  }, []);

  if (loading) return <h2>Loading dashboard...</h2>;
  if (error) return <h2>Error: {error}</h2>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>Overview of agents, members, accounts and assets</p>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <p className="stat-card-title">Total Agents</p>
          <h3 className="stat-card-value">{summary.totalAgents}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Total Members</p>
          <h3 className="stat-card-value">{summary.totalMembers}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Total Accounts</p>
          <h3 className="stat-card-value">{summary.totalAccounts}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Total Assets</p>
          <h3 className="stat-card-value">{formatCurrency(summary.totalAssets)}</h3>
        </div>
      </div>

      <div className="card">
        <h3>System Summary</h3>
        <p>
          This dashboard shows the current totals across imported agents, members,
          accounts and balances
        </p>
      </div>
    </div>
  );
}

function formatCurrency(value) {
  return Number(value || 0).toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

export default DashboardPage;