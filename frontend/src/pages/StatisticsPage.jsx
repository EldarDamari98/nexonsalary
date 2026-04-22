import { useEffect, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import {
  getAssetsTrend,
  getClientMovement,
  getCommissionTrend,
  getReasonBreakdown,
  getStatisticsOverview,
  getTopAgents,
} from "../api/statisticsApi";

function StatisticsPage() {
  const [overview, setOverview] = useState(null);
  const [assetsTrend, setAssetsTrend] = useState([]);
  const [commissionTrend, setCommissionTrend] = useState([]);
  const [topAgents, setTopAgents] = useState([]);
  const [reasonBreakdown, setReasonBreakdown] = useState([]);
  const [clientMovement, setClientMovement] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadStatistics();
  }, []);

  const loadStatistics = async () => {
    setLoading(true);
    setError("");

    try {
      const [
        overviewData,
        assetsTrendData,
        commissionTrendData,
        topAgentsData,
        reasonBreakdownData,
        clientMovementData,
      ] = await Promise.all([
        getStatisticsOverview(),
        getAssetsTrend(),
        getCommissionTrend(),
        getTopAgents(),
        getReasonBreakdown(),
        getClientMovement(),
      ]);

      setOverview(overviewData);
      setAssetsTrend(assetsTrendData);
      setCommissionTrend(commissionTrendData);
      setTopAgents(topAgentsData);
      setReasonBreakdown(reasonBreakdownData);
      setClientMovement(clientMovementData);
    } catch (err) {
      setError(err.message || "Failed to load statistics");
    } finally {
      setLoading(false);
    }
  };

  const pieData = reasonBreakdown.map((item) => ({
    name: formatReason(item.reason),
    value: item.count,
  }));

  if (loading) {
    return <p>Loading statistics...</p>;
  }

  if (error) {
    return (
      <div className="card">
        <p style={{ color: "#dc2626" }}>{error}</p>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Statistics</h1>
          <p>System analytics and charts powered by JDBC</p>
        </div>

        <div className="page-actions">
          <button className="primary-btn" onClick={loadStatistics}>
            Refresh
          </button>
        </div>
      </div>

      <div className="stats-grid" style={{ marginBottom: "24px" }}>
        <div className="stat-card">
          <p className="stat-card-title">Total Assets</p>
          <h3 className="stat-card-value">{fmt(overview?.totalAssets)}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Total Commission Paid</p>
          <h3 className="stat-card-value">{fmt(overview?.totalCommissionPaid)}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Total Transactions</p>
          <h3 className="stat-card-value">{fmtInt(overview?.totalTransactions)}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Active Agents</p>
          <h3 className="stat-card-value">{fmtInt(overview?.activeAgents)}</h3>
        </div>
      </div>

      <div className="stats-charts-grid">
        <div className="card chart-card">
          <h2>Monthly Assets Trend</h2>
          <div className="chart-wrapper">
            <ResponsiveContainer width="100%" height={320}>
              <LineChart data={assetsTrend}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => fmt(value)} />
                <Line type="monotone" dataKey="value" stroke="#2563eb" strokeWidth={3} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card chart-card">
          <h2>Monthly Net Commission Trend</h2>
          <div className="chart-wrapper">
            <ResponsiveContainer width="100%" height={320}>
              <BarChart data={commissionTrend}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => fmt(value)} />
                <Bar dataKey="value" fill="#2563eb" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card chart-card">
          <h2>Top 10 Agents by Net Commission</h2>
          <div className="chart-wrapper">
            <ResponsiveContainer width="100%" height={360}>
              <BarChart data={topAgents} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis
                  type="category"
                  dataKey="agentName"
                  width={120}
                />
                <Tooltip formatter={(value) => fmt(value)} />
                <Bar dataKey="netCommission" fill="#2563eb" radius={[0, 8, 8, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card chart-card">
          <h2>Transactions by Reason</h2>
          <div className="chart-wrapper">
            <ResponsiveContainer width="100%" height={360}>
              <PieChart>
                <Pie
                  data={pieData}
                  dataKey="value"
                  nameKey="name"
                  outerRadius={120}
                  label
                >
                  {pieData.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={PIE_COLORS[index % PIE_COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card chart-card chart-card-full">
          <h2>Client Movement by Month</h2>
          <div className="chart-wrapper">
            <ResponsiveContainer width="100%" height={360}>
              <BarChart data={clientMovement}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="newClients" fill="#2563eb" name="New Clients" radius={[6, 6, 0, 0]} />
                <Bar dataKey="leftClients" fill="#dc2626" name="Left Clients" radius={[6, 6, 0, 0]} />
                <Bar dataKey="transferredClients" fill="#f59e0b" name="Transferred Clients" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}

const PIE_COLORS = ["#2563eb", "#16a34a", "#f59e0b", "#dc2626", "#7c3aed"];

function fmt(value) {
  if (value === null || value === undefined) return "0.00";
  return Number(value).toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

function fmtInt(value) {
  if (value === null || value === undefined) return "0";
  return Number(value).toLocaleString("en-US");
}

function formatReason(reason) {
  const map = {
    PERIMETER_FEE_NEW: "Perimeter Fee New",
    PERIMETER_FEE_DELTA: "Perimeter Fee Delta",
    PERIMETER_FEE_CLAWBACK: "Clawback",
    TRAIL_COMMISSION: "Trail Commission",
  };

  return map[reason] || reason;
}

export default StatisticsPage;