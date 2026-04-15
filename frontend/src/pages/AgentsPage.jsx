import { useEffect, useMemo, useState } from "react";
import { getAllAgents } from "../api/agentApi";

function AgentsPage() {
  const [agents, setAgents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const loadAgents = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getAllAgents();
      setAgents(data);
    } catch (err) {
      setError(err.message || "Failed to load agents");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAgents();
  }, []);

  const filteredAgents = useMemo(() => {
    const value = search.trim().toLowerCase();

    return agents.filter((agent) => {
      return (
        !value ||
        agent.agentName?.toLowerCase().includes(value) ||
        agent.agentCode?.toLowerCase().includes(value)
      );
    });
  }, [agents, search]);

  useEffect(() => {
    setCurrentPage(1);
  }, [search, pageSize]);

  const totalPages = Math.max(1, Math.ceil(filteredAgents.length / pageSize));
  const safeCurrentPage = Math.min(currentPage, totalPages);

  const paginatedAgents = useMemo(() => {
    const startIndex = (safeCurrentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return filteredAgents.slice(startIndex, endIndex);
  }, [filteredAgents, safeCurrentPage, pageSize]);

  const startRow =
    filteredAgents.length === 0 ? 0 : (safeCurrentPage - 1) * pageSize + 1;
  const endRow = Math.min(safeCurrentPage * pageSize, filteredAgents.length);

  const clearSearch = () => {
    setSearch("");
    setCurrentPage(1);
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Agents Explorer</h1>
          <p>View agents, linked members, accounts and managed assets</p>
        </div>

        <div className="page-actions">
          <button className="primary-btn" onClick={loadAgents} disabled={loading}>
            {loading ? "Refreshing..." : "Refresh"}
          </button>
        </div>
      </div>

      <div className="card">
        <div className="filters-bar">
          <input
            className="input"
            type="text"
            placeholder="Search by agent name or code"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <select
            className="input"
            value={pageSize}
            onChange={(e) => setPageSize(Number(e.target.value))}
          >
            <option value={5}>5 per page</option>
            <option value={10}>10 per page</option>
            <option value={20}>20 per page</option>
            <option value={50}>50 per page</option>
          </select>

          <button
            className="secondary-btn filter-action-btn"
            onClick={clearSearch}
          >
            Clear Filters
          </button>
        </div>

        {error && (
          <p style={{ marginBottom: "16px", color: "#dc2626" }}>
            {error}
          </p>
        )}

        {!loading && !error && (
          <div className="balances-toolbar">
            <p className="balances-results-text">
              Showing {startRow}-{endRow} of {filteredAgents.length} agents
            </p>

            <div className="pagination-controls">
              <button
                className="secondary-btn"
                onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                disabled={safeCurrentPage === 1}
              >
                Previous
              </button>

              <span className="pagination-page-info">
                Page {safeCurrentPage} of {totalPages}
              </span>

              <button
                className="secondary-btn"
                onClick={() =>
                  setCurrentPage((prev) => Math.min(prev + 1, totalPages))
                }
                disabled={safeCurrentPage === totalPages}
              >
                Next
              </button>
            </div>
          </div>
        )}

        {loading ? (
          <p>Loading agents...</p>
        ) : (
          <div style={{ overflowX: "auto" }}>
            <table className="employees-table">
              <thead>
                <tr>
                  <th>Agent Code</th>
                  <th>Agent Name</th>
                  <th>Members</th>
                  <th>Accounts</th>
                  <th>Total Assets</th>
                  <th>Latest Balance Date</th>
                </tr>
              </thead>
              <tbody>
                {paginatedAgents.length === 0 ? (
                  <tr>
                    <td colSpan="6" style={{ textAlign: "center", padding: "24px" }}>
                      No agents found
                    </td>
                  </tr>
                ) : (
                  paginatedAgents.map((agent) => (
                    <tr key={agent.agentId}>
                      <td>{agent.agentCode}</td>
                      <td>{agent.agentName}</td>
                      <td>{agent.membersCount}</td>
                      <td>{agent.accountsCount}</td>
                      <td>{formatCurrency(agent.totalAssets)}</td>
                      <td>{formatDate(agent.latestBalanceDate)}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
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

function formatDate(value) {
  if (!value) return "";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleDateString("en-GB");
}

export default AgentsPage;