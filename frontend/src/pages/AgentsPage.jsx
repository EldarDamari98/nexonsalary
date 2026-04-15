import { useEffect, useMemo, useState } from "react";
import { getAllMembers } from "../api/memberApi";

function EmployeesPage() {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const loadMembers = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getAllMembers();
      setMembers(data);
    } catch (err) {
      setError(err.message || "Failed to load members");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMembers();
  }, []);

  const filteredMembers = useMemo(() => {
    const value = search.trim().toLowerCase();

    return members.filter((member) => {
      return (
        !value ||
        member.memberName?.toLowerCase().includes(value) ||
        member.nationalId?.toLowerCase().includes(value) ||
        member.agentName?.toLowerCase().includes(value)
      );
    });
  }, [members, search]);

  useEffect(() => {
    setCurrentPage(1);
  }, [search, pageSize]);

  const totalPages = Math.max(1, Math.ceil(filteredMembers.length / pageSize));
  const safeCurrentPage = Math.min(currentPage, totalPages);

  const paginatedMembers = useMemo(() => {
    const startIndex = (safeCurrentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return filteredMembers.slice(startIndex, endIndex);
  }, [filteredMembers, safeCurrentPage, pageSize]);

  const startRow =
    filteredMembers.length === 0 ? 0 : (safeCurrentPage - 1) * pageSize + 1;
  const endRow = Math.min(safeCurrentPage * pageSize, filteredMembers.length);

  const clearSearch = () => {
    setSearch("");
    setCurrentPage(1);
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Members Explorer</h1>
          <p>View imported members, latest balances and linked accounts</p>
        </div>

        <div className="page-actions">
          <button className="primary-btn" onClick={loadMembers} disabled={loading}>
            {loading ? "Refreshing..." : "Refresh"}
          </button>
        </div>
      </div>

      <div className="card">
        <div className="filters-bar">
          <input
            className="input"
            type="text"
            placeholder="Search by member, ID or agent"
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
              Showing {startRow}-{endRow} of {filteredMembers.length} members
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
          <p>Loading members...</p>
        ) : (
          <div style={{ overflowX: "auto" }}>
            <table className="employees-table">
              <thead>
                <tr>
                  <th>Member Name</th>
                  <th>National ID</th>
                  <th>Accounts</th>
                  <th>Latest Balance</th>
                  <th>Agent</th>
                  <th>Last Update</th>
                </tr>
              </thead>
              <tbody>
                {paginatedMembers.length === 0 ? (
                  <tr>
                    <td colSpan="6" style={{ textAlign: "center", padding: "24px" }}>
                      No members found
                    </td>
                  </tr>
                ) : (
                  paginatedMembers.map((member) => (
                    <tr key={member.memberId}>
                      <td>{member.memberName}</td>
                      <td>{member.nationalId}</td>
                      <td>{member.accountsCount}</td>
                      <td>{formatCurrency(member.latestBalance)}</td>
                      <td>{member.agentName}</td>
                      <td>{formatDate(member.lastBalanceDate)}</td>
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
  if (!value) {
    return "";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleDateString("en-GB");
}

export default EmployeesPage;