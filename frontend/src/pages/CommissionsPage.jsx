import { useEffect, useMemo, useState } from "react";
import { getAllAgents } from "../api/agentApi";
import {
  calculateCommissions,
  getCommissionExplorerSummary,
  getCommissionExplorerTransactions,
  recalculateCommissions,
} from "../api/commissionApi";

function CommissionsPage() {
  const [month, setMonth] = useState("");

  const [calculating, setCalculating] = useState(false);
  const [recalculating, setRecalculating] = useState(false);
  const [alreadyCalculated, setAlreadyCalculated] = useState(false);
  const [calcResult, setCalcResult] = useState(null);
  const [calcError, setCalcError] = useState("");

  const [agents, setAgents] = useState([]);

  const [resultsVisible, setResultsVisible] = useState(false);
  const [summary, setSummary] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [transactionsTotalItems, setTransactionsTotalItems] = useState(0);
  const [transactionsTotalPages, setTransactionsTotalPages] = useState(1);
  const [loadingResults, setLoadingResults] = useState(false);
  const [resultsError, setResultsError] = useState("");

  const [search, setSearch] = useState("");
  const [selectedAgentId, setSelectedAgentId] = useState("");
  const [period, setPeriod] = useState("MONTH");
  const [selectedReason, setSelectedReason] = useState("");
  const [selectedDirection, setSelectedDirection] = useState("");
  const [pageSize, setPageSize] = useState(10);
  const [currentPage, setCurrentPage] = useState(1);
  const [sortBy, setSortBy] = useState("balanceDate");
  const [sortDirection, setSortDirection] = useState("desc");

  useEffect(() => {
    loadAgents();
  }, []);

  useEffect(() => {
    if (!resultsVisible) return;

    loadExplorerData();
  }, [
    resultsVisible,
    month,
    search,
    selectedAgentId,
    period,
    selectedReason,
    selectedDirection,
    pageSize,
    currentPage,
    sortBy,
    sortDirection,
  ]);

  useEffect(() => {
    if (period !== "MONTH") {
      setResultsError("");
    }
  }, [period]);

  const loadAgents = async () => {
    try {
      const data = await getAllAgents();
      setAgents(data);
    } catch (err) {
      console.error(err);
    }
  };

  const loadExplorerData = async () => {
    if (period === "MONTH" && !month) {
      setResultsError("Please select a month first");
      setSummary([]);
      setTransactions([]);
      setTransactionsTotalItems(0);
      setTransactionsTotalPages(1);
      return;
    }

    setLoadingResults(true);
    setResultsError("");

    try {
      const [summaryData, transactionsData] = await Promise.all([
        getCommissionExplorerSummary({
          month,
          period,
          search,
          agentId: selectedAgentId,
          reason: selectedReason,
          direction: selectedDirection,
        }),
        getCommissionExplorerTransactions({
          month,
          period,
          search,
          agentId: selectedAgentId,
          reason: selectedReason,
          direction: selectedDirection,
          page: currentPage,
          size: pageSize,
          sortBy,
          sortDirection,
        }),
      ]);

      setSummary(summaryData || []);
      setTransactions(transactionsData.items || []);
      setTransactionsTotalItems(transactionsData.totalItems || 0);
      setTransactionsTotalPages(transactionsData.totalPages || 1);
    } catch (err) {
      setResultsError(err.message || "Failed to load results");
      setSummary([]);
      setTransactions([]);
      setTransactionsTotalItems(0);
      setTransactionsTotalPages(1);
    } finally {
      setLoadingResults(false);
    }
  };

  const handleCalculate = async () => {
    if (!month) return;

    setCalculating(true);
    setCalcError("");
    setCalcResult(null);
    setAlreadyCalculated(false);

    try {
      const result = await calculateCommissions(month);
      setCalcResult(result);
      setPeriod("MONTH");
      setCurrentPage(1);
      setResultsVisible(true);
    } catch (err) {
      setCalcError(err.message || "Calculation failed");

      if (err.alreadyCalculated) {
        setAlreadyCalculated(true);
        setPeriod("MONTH");
        setCurrentPage(1);
        setResultsVisible(true);
      }
    } finally {
      setCalculating(false);
    }
  };

  const handleRecalculate = async () => {
    if (!month) return;

    setRecalculating(true);
    setCalcError("");
    setCalcResult(null);
    setAlreadyCalculated(false);

    try {
      const result = await recalculateCommissions(month);
      setCalcResult(result);
      setPeriod("MONTH");
      setCurrentPage(1);
      setResultsVisible(true);
    } catch (err) {
      setCalcError(err.message || "Recalculation failed");
    } finally {
      setRecalculating(false);
    }
  };

  const handleViewResults = () => {
    if (!month) return;
    setPeriod("MONTH");
    setCurrentPage(1);
    setResultsVisible(true);
  };

  const clearFilters = () => {
    setSearch("");
    setSelectedAgentId("");
    setSelectedReason("");
    setSelectedDirection("");
    setPeriod("MONTH");
    setPageSize(10);
    setCurrentPage(1);
    setSortBy("balanceDate");
    setSortDirection("desc");
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setSortDirection((prev) => (prev === "asc" ? "desc" : "asc"));
      setCurrentPage(1);
      return;
    }

    setSortBy(field);
    setSortDirection(
      field === "memberName" || field === "agentName" || field === "reason"
        ? "asc"
        : "desc"
    );
    setCurrentPage(1);
  };

  const getSortIndicator = (field) => {
    if (sortBy !== field) return "↕";
    return sortDirection === "asc" ? "↑" : "↓";
  };

  const explorerTotals = useMemo(() => {
    return summary.reduce(
      (acc, row) => {
        acc.scopeNew += Number(row.scopeNew || 0);
        acc.scopeDelta += Number(row.scopeDelta || 0);
        acc.clawbacks += Number(row.clawbacks || 0);
        acc.nifra += Number(row.nifra || 0);
        acc.netCommission += Number(row.netCommission || 0);
        acc.transactionCount += Number(row.transactionCount || 0);
        return acc;
      },
      {
        scopeNew: 0,
        scopeDelta: 0,
        clawbacks: 0,
        nifra: 0,
        netCommission: 0,
        transactionCount: 0,
      }
    );
  }, [summary]);

  const startRow =
    transactionsTotalItems === 0 ? 0 : (currentPage - 1) * pageSize + 1;

  const endRow =
    transactionsTotalItems === 0 ? 0 : startRow + transactions.length - 1;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Commissions</h1>
          <p>Calculate and explore monthly agent commissions</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: "24px" }}>
        <h2 style={{ margin: "0 0 16px" }}>Calculate Monthly Commissions</h2>
        <p style={{ color: "#6b7280", marginBottom: "20px" }}>
          Select the month to calculate. The system compares it against the
          previous month to detect new clients, departures, and transfers.
        </p>

        <div
          style={{
            display: "flex",
            gap: "12px",
            alignItems: "center",
            flexWrap: "wrap",
          }}
        >
          <input
            className="input"
            type="month"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
            style={{ width: "220px" }}
          />

          <button
            className="primary-btn"
            onClick={handleCalculate}
            disabled={!month || calculating}
          >
            {calculating ? "Calculating..." : "Calculate"}
          </button>

          <button
            className="secondary-btn"
            onClick={handleViewResults}
            disabled={!month || loadingResults}
          >
            {loadingResults ? "Loading..." : "View Results"}
          </button>

          {alreadyCalculated && (
            <button
              className="secondary-btn"
              onClick={handleRecalculate}
              disabled={recalculating}
              style={{ borderColor: "#f59e0b", color: "#b45309" }}
            >
              {recalculating ? "Recalculating..." : "Recalculate"}
            </button>
          )}
        </div>

        {calcError && (
          <p style={{ marginTop: "16px", color: "#dc2626" }}>{calcError}</p>
        )}

        {calcResult && (
          <div style={{ marginTop: "20px" }}>
            <p
              style={{
                color: "#16a34a",
                fontWeight: 600,
                marginBottom: "12px",
              }}
            >
              {calcResult.message}
            </p>

            <div className="stats-grid">
              <div className="stat-card">
                <p className="stat-card-title">New Clients</p>
                <h3 className="stat-card-value">{calcResult.newClients}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Existing Clients</p>
                <h3 className="stat-card-value">
                  {calcResult.existingClients}
                </h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Left</p>
                <h3 className="stat-card-value">{calcResult.leftClients}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Transferred</p>
                <h3 className="stat-card-value">
                  {calcResult.transferredClients}
                </h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Scope (New)</p>
                <h3 className="stat-card-value">
                  {fmt(calcResult.totalScopeNew)}
                </h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Scope (Delta)</p>
                <h3 className="stat-card-value">
                  {fmt(calcResult.totalScopeDelta)}
                </h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Clawbacks</p>
                <h3 className="stat-card-value" style={{ color: "#dc2626" }}>
                  -{fmt(calcResult.totalClawbacks)}
                </h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Nifra</p>
                <h3 className="stat-card-value">{fmt(calcResult.totalNifra)}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Net Commission</p>
                <h3 className="stat-card-value">
                  {fmt(calcResult.netCommission)}
                </h3>
              </div>
            </div>
          </div>
        )}
      </div>

      {resultsVisible && (
        <>
          <div className="card" style={{ marginBottom: "24px" }}>
            <div style={{ marginBottom: "20px" }}>
              <h2 style={{ margin: "0 0 8px" }}>Commission Results Explorer</h2>
              <p style={{ margin: 0, color: "#6b7280" }}>
                Search, filter and browse commission transactions
              </p>
            </div>

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

              <select
                className="input"
                value={selectedAgentId}
                onChange={(e) => {
                  setSelectedAgentId(e.target.value);
                  setCurrentPage(1);
                }}
              >
                <option value="">All agents</option>
                {agents.map((agent) => (
                  <option key={agent.agentId} value={agent.agentId}>
                    {agent.agentName} ({agent.agentCode})
                  </option>
                ))}
              </select>

              <select
                className="input"
                value={period}
                onChange={(e) => {
                  setPeriod(e.target.value);
                  setCurrentPage(1);
                }}
              >
                <option value="MONTH">Selected month</option>
                <option value="LAST_3_MONTHS">Last 3 months</option>
                <option value="LAST_6_MONTHS">Last 6 months</option>
                <option value="LAST_12_MONTHS">Last 12 months</option>
                <option value="ALL_TIME">All time</option>
              </select>

              <select
                className="input"
                value={selectedReason}
                onChange={(e) => {
                  setSelectedReason(e.target.value);
                  setCurrentPage(1);
                }}
              >
                <option value="">All reasons</option>
                <option value="SCOPE_NEW">Scope New</option>
                <option value="SCOPE_DELTA">Scope Delta</option>
                <option value="SCOPE_CLAWBACK">Clawback</option>
                <option value="NIFRA">Nifra</option>
              </select>

              <select
                className="input"
                value={selectedDirection}
                onChange={(e) => {
                  setSelectedDirection(e.target.value);
                  setCurrentPage(1);
                }}
              >
                <option value="">All directions</option>
                <option value="CREDIT">Credit</option>
                <option value="DEBIT">Debit</option>
              </select>

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

            <div style={{ marginBottom: "20px" }}>
              <input
                className="input"
                type="month"
                value={month}
                onChange={(e) => {
                  setMonth(e.target.value);
                  setCurrentPage(1);
                }}
                disabled={period !== "MONTH"}
                style={{ width: "220px" }}
              />
            </div>

            {resultsError && (
              <p style={{ marginBottom: "16px", color: "#dc2626" }}>
                {resultsError}
              </p>
            )}

            <div className="stats-grid" style={{ marginBottom: "20px" }}>
              <div className="stat-card">
                <p className="stat-card-title">Matching Agents</p>
                <h3 className="stat-card-value">{summary.length}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Matching Transactions</p>
                <h3 className="stat-card-value">{transactionsTotalItems}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Clawbacks</p>
                <h3 className="stat-card-value" style={{ color: "#dc2626" }}>
                  -{fmt(explorerTotals.clawbacks)}
                </h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Net Commission</p>
                <h3 className="stat-card-value">
                  {fmt(explorerTotals.netCommission)}
                </h3>
              </div>
            </div>
          </div>

          {summary.length > 0 && (
            <div className="card" style={{ marginBottom: "24px" }}>
              <h2 style={{ margin: "0 0 16px" }}>Summary by Agent</h2>

              <div style={{ overflowX: "auto" }}>
                <table className="simple-table">
                  <thead>
                    <tr>
                      <th>Agent</th>
                      <th style={{ textAlign: "right" }}>Scope New</th>
                      <th style={{ textAlign: "right" }}>Scope Delta</th>
                      <th style={{ textAlign: "right" }}>Clawbacks</th>
                      <th style={{ textAlign: "right" }}>Nifra</th>
                      <th style={{ textAlign: "right" }}>Net Commission</th>
                      <th style={{ textAlign: "right" }}>Transactions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {summary.map((row) => (
                      <tr key={row.agentId}>
                        <td>
                          {row.agentName}{" "}
                          <span style={{ color: "#6b7280", fontSize: "12px" }}>
                            ({row.agentCode})
                          </span>
                        </td>
                        <td style={{ textAlign: "right" }}>
                          {fmt(row.scopeNew)}
                        </td>
                        <td style={{ textAlign: "right" }}>
                          {fmt(row.scopeDelta)}
                        </td>
                        <td style={{ textAlign: "right", color: "#dc2626" }}>
                          -{fmt(row.clawbacks)}
                        </td>
                        <td style={{ textAlign: "right" }}>{fmt(row.nifra)}</td>
                        <td style={{ textAlign: "right", fontWeight: 600 }}>
                          {fmt(row.netCommission)}
                        </td>
                        <td style={{ textAlign: "right" }}>
                          {row.transactionCount}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          <div className="card">
            <h2 style={{ margin: "0 0 16px" }}>Transaction Detail</h2>

            {!loadingResults && !resultsError && (
              <div className="balances-toolbar">
                <p className="balances-results-text">
                  Showing {startRow}-{endRow} of {transactionsTotalItems} transactions
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
                    Page {currentPage} of {Math.max(transactionsTotalPages, 1)}
                  </span>

                  <button
                    className="secondary-btn"
                    onClick={() =>
                      setCurrentPage((prev) =>
                        Math.min(prev + 1, transactionsTotalPages)
                      )
                    }
                    disabled={currentPage >= transactionsTotalPages}
                  >
                    Next
                  </button>
                </div>
              </div>
            )}

            {loadingResults ? (
              <p>Loading transactions...</p>
            ) : (
              <div style={{ overflowX: "auto" }}>
                <table className="simple-table">
                  <thead>
                    <tr>
                      <th
                        className="sortable-header"
                        onClick={() => handleSort("agentName")}
                      >
                        Agent {getSortIndicator("agentName")}
                      </th>

                      <th
                        className="sortable-header"
                        onClick={() => handleSort("memberName")}
                      >
                        Member {getSortIndicator("memberName")}
                      </th>

                      <th>Account</th>

                      <th
                        className="sortable-header"
                        onClick={() => handleSort("reason")}
                      >
                        Reason {getSortIndicator("reason")}
                      </th>

                      <th
                        className="sortable-header"
                        onClick={() => handleSort("balanceDate")}
                      >
                        Month {getSortIndicator("balanceDate")}
                      </th>

                      <th style={{ textAlign: "right" }}>Prev Balance</th>
                      <th style={{ textAlign: "right" }}>Curr Balance</th>

                      <th
                        className="sortable-header balances-number-header"
                        onClick={() => handleSort("commissionAmount")}
                      >
                        Amount {getSortIndicator("commissionAmount")}
                      </th>

                      <th>Direction</th>
                    </tr>
                  </thead>

                  <tbody>
                    {transactions.length === 0 ? (
                      <tr>
                        <td
                          colSpan="9"
                          style={{ textAlign: "center", padding: "24px" }}
                        >
                          No commission transactions found
                        </td>
                      </tr>
                    ) : (
                      transactions.map((tx) => (
                        <tr key={tx.id}>
                          <td>
                            {tx.agentName}{" "}
                            <span style={{ color: "#6b7280", fontSize: "12px" }}>
                              ({tx.agentCode})
                            </span>
                          </td>
                          <td>{tx.memberName}</td>
                          <td>{tx.accountNumber}</td>
                          <td>{formatReason(tx.reason)}</td>
                          <td>{formatMonth(tx.balanceDate)}</td>
                          <td style={{ textAlign: "right" }}>
                            {fmt(tx.previousBalance)}
                          </td>
                          <td style={{ textAlign: "right" }}>
                            {fmt(tx.currentBalance)}
                          </td>
                          <td
                            className="balances-number-cell"
                            style={{
                              fontWeight: 600,
                              color:
                                tx.direction === "DEBIT" ? "#dc2626" : "#16a34a",
                            }}
                          >
                            {tx.direction === "DEBIT" ? "-" : "+"}
                            {fmt(tx.commissionAmount)}
                          </td>
                          <td>{tx.direction}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}

function fmt(value) {
  if (value === null || value === undefined) return "0.00";
  return Number(value).toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

function formatReason(reason) {
  const map = {
    SCOPE_NEW: "Scope New",
    SCOPE_DELTA: "Scope Delta",
    SCOPE_CLAWBACK: "Clawback",
    NIFRA: "Nifra",
  };

  return map[reason] || reason;
}

function formatMonth(value) {
  if (!value) return "";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleDateString("en-GB", {
    month: "2-digit",
    year: "numeric",
  });
}

export default CommissionsPage;