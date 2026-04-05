import { useEffect, useMemo, useState } from "react";
import { getBalances } from "../api/balanceApi";

function BalancesPage() {
  const [balances, setBalances] = useState([]);
  const [summary, setSummary] = useState({
    totalRecords: 0,
    uniqueMembers: 0,
    uniqueAccounts: 0,
    totalAssets: 0,
  });

  const [totalItems, setTotalItems] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [selectedAgent, setSelectedAgent] = useState("");
  const [selectedDate, setSelectedDate] = useState("");

  const [sortBy, setSortBy] = useState("balanceDate");
  const [sortDirection, setSortDirection] = useState("desc");

  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const loadBalances = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getBalances({
        page: currentPage,
        size: pageSize,
        search,
        agent: selectedAgent,
        date: selectedDate,
        sortBy,
        sortDirection,
      });

      setBalances(data.items || []);
      setSummary(
        data.summary || {
          totalRecords: 0,
          uniqueMembers: 0,
          uniqueAccounts: 0,
          totalAssets: 0,
        }
      );
      setTotalItems(data.totalItems || 0);
      setTotalPages(data.totalPages || 1);
    } catch (err) {
      setError(err.message || "Failed to load balances");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBalances();
  }, [currentPage, pageSize, search, selectedAgent, selectedDate, sortBy, sortDirection]);

  const clearFilters = () => {
    setSearch("");
    setSelectedAgent("");
    setSelectedDate("");
    setSortBy("balanceDate");
    setSortDirection("desc");
    setCurrentPage(1);
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setSortDirection((prev) => (prev === "asc" ? "desc" : "asc"));
      setCurrentPage(1);
      return;
    }

    setSortBy(field);
    setSortDirection(field === "memberName" ? "asc" : "desc");
    setCurrentPage(1);
  };

  const getSortIndicator = (field) => {
    if (sortBy !== field) {
      return "↕";
    }

    return sortDirection === "asc" ? "↑" : "↓";
  };

  const startRow = totalItems === 0 ? 0 : (currentPage - 1) * pageSize + 1;
  const endRow = Math.min(currentPage * pageSize, totalItems);

  const exportToCsv = () => {
    const headers = [
      "Member Name",
      "National ID",
      "Account Number",
      "Agent",
      "Balance Date",
      "Total Balance",
    ];

    const rows = balances.map((balance) => [
      balance.memberName || "",
      balance.nationalId || "",
      balance.accountNumber || "",
      balance.agentName || "",
      balance.balanceDate || "",
      balance.totalBalance ?? "",
    ]);

    const csvContent = [headers, ...rows]
      .map((row) =>
        row.map((cell) => `"${String(cell).replace(/"/g, '""')}"`).join(",")
      )
      .join("\n");

    const blob = new Blob([csvContent], {
      type: "text/csv;charset=utf-8;",
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    const today = new Date().toISOString().slice(0, 10);

    link.href = url;
    link.setAttribute("download", `balances-page-${today}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    URL.revokeObjectURL(url);
  };

  const agentOptions = useMemo(() => {
    return [...new Set(balances.map((balance) => balance.agentName).filter(Boolean))].sort((a, b) =>
      a.localeCompare(b)
    );
  }, [balances]);

  const dateOptions = useMemo(() => {
    return [...new Set(balances.map((balance) => balance.balanceDate).filter(Boolean))].sort((a, b) =>
      b.localeCompare(a)
    );
  }, [balances]);

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Balances Explorer</h1>
          <p>View imported balances with server-side search, filters, sorting and pagination</p>
        </div>

        <div className="page-actions">
          <button
            className="secondary-btn"
            onClick={exportToCsv}
            disabled={loading || balances.length === 0}
          >
            Export CSV
          </button>

          <button className="primary-btn" onClick={loadBalances} disabled={loading}>
            {loading ? "Refreshing..." : "Refresh"}
          </button>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <p className="stat-card-title">Total Records</p>
          <h3 className="stat-card-value">{summary.totalRecords}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Unique Members</p>
          <h3 className="stat-card-value">{summary.uniqueMembers}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Unique Accounts</p>
          <h3 className="stat-card-value">{summary.uniqueAccounts}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Total Assets</p>
          <h3 className="stat-card-value">{formatCurrency(summary.totalAssets)}</h3>
        </div>
      </div>

      <div className="card">
        <div className="balances-filters">
          <input
            className="input"
            type="text"
            placeholder="Search by member, ID, account or agent"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setCurrentPage(1);
            }}
          />

          <input
            className="input"
            type="text"
            placeholder="Filter by exact agent name"
            value={selectedAgent}
            onChange={(e) => {
              setSelectedAgent(e.target.value);
              setCurrentPage(1);
            }}
          />

          <input
            className="input"
            type="date"
            value={selectedDate}
            onChange={(e) => {
              setSelectedDate(e.target.value);
              setCurrentPage(1);
            }}
          />

          <select
            className="input"
            value={pageSize}
            onChange={(e) => {
              setPageSize(Number(e.target.value));
              setCurrentPage(1);
            }}
          >
            <option value={5}>5 per page</option>
            <option value={10}>10 per page</option>
            <option value={20}>20 per page</option>
            <option value={50}>50 per page</option>
          </select>

          <button
            className="secondary-btn filter-action-btn"
            onClick={clearFilters}
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
              Showing {startRow}-{endRow} of {totalItems} balance records
            </p>

            <div className="pagination-controls">
              <button
                className="secondary-btn"
                onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                disabled={currentPage === 1}
              >
                Previous
              </button>

              <span className="pagination-page-info">
                Page {currentPage} of {Math.max(totalPages, 1)}
              </span>

              <button
                className="secondary-btn"
                onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                disabled={currentPage >= totalPages}
              >
                Next
              </button>
            </div>
          </div>
        )}

        {loading ? (
          <p>Loading balances...</p>
        ) : (
          <div style={{ overflowX: "auto" }}>
            <table className="simple-table">
              <thead>
                <tr>
                  <th
                    className="sortable-header"
                    onClick={() => handleSort("memberName")}
                  >
                    Member Name {getSortIndicator("memberName")}
                  </th>
                  <th>National ID</th>
                  <th>Account Number</th>
                  <th>Agent</th>
                  <th
                    className="sortable-header"
                    onClick={() => handleSort("balanceDate")}
                  >
                    Balance Date {getSortIndicator("balanceDate")}
                  </th>
                  <th
                    className="sortable-header balances-number-header"
                    onClick={() => handleSort("totalBalance")}
                  >
                    Total Balance {getSortIndicator("totalBalance")}
                  </th>
                </tr>
              </thead>
              <tbody>
                {balances.length === 0 ? (
                  <tr>
                    <td colSpan="6" style={{ textAlign: "center", padding: "24px" }}>
                      No balances found
                    </td>
                  </tr>
                ) : (
                  balances.map((balance) => (
                    <tr key={balance.balanceId}>
                      <td>{balance.memberName}</td>
                      <td>{balance.nationalId}</td>
                      <td>{balance.accountNumber}</td>
                      <td>{balance.agentName}</td>
                      <td>{formatDate(balance.balanceDate)}</td>
                      <td className="balances-number-cell">
                        {formatCurrency(balance.totalBalance)}
                      </td>
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
  if (value === null || value === undefined || value === "") {
    return "";
  }

  return Number(value).toLocaleString("en-US", {
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

export default BalancesPage;