import { useState } from "react";
import {
  calculateCommissions,
  recalculateCommissions,
  getCommissionSummary,
  getCommissionTransactions,
} from "../api/commissionApi";

function CommissionsPage() {
  const [month, setMonth] = useState("");
  const [calculating, setCalculating] = useState(false);
  const [recalculating, setRecalculating] = useState(false);
  const [alreadyCalculated, setAlreadyCalculated] = useState(false);
  const [calcResult, setCalcResult] = useState(null);
  const [calcError, setCalcError] = useState("");

  const [summary, setSummary] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loadingResults, setLoadingResults] = useState(false);
  const [resultsError, setResultsError] = useState("");

  const handleCalculate = async () => {
    if (!month) return;
    setCalculating(true);
    setCalcError("");
    setCalcResult(null);
    setAlreadyCalculated(false);

    try {
      const result = await calculateCommissions(month);
      setCalcResult(result);
      await loadResults(month);
    } catch (err) {
      setCalcError(err.message || "Calculation failed");
      if (err.alreadyCalculated) {
        setAlreadyCalculated(true);
        await loadResults(month);
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
      await loadResults(month);
    } catch (err) {
      setCalcError(err.message || "Recalculation failed");
    } finally {
      setRecalculating(false);
    }
  };

  const handleViewResults = async () => {
    if (!month) return;
    await loadResults(month);
  };

  const loadResults = async (m) => {
    setLoadingResults(true);
    setResultsError("");
    try {
      const [summaryData, txData] = await Promise.all([
        getCommissionSummary(m),
        getCommissionTransactions(m),
      ]);
      setSummary(summaryData);
      setTransactions(txData);
    } catch (err) {
      setResultsError(err.message || "Failed to load results");
    } finally {
      setLoadingResults(false);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Commissions</h1>
          <p>Calculate and view monthly agent commissions</p>
        </div>
      </div>

      {/* Calculate panel */}
      <div className="card" style={{ marginBottom: "24px" }}>
        <h2 style={{ margin: "0 0 16px" }}>Calculate Monthly Commissions</h2>
        <p style={{ color: "#6b7280", marginBottom: "20px" }}>
          Select the month to calculate. The system compares it against the
          previous month to detect new clients, departures, and transfers.
        </p>

        <div style={{ display: "flex", gap: "12px", alignItems: "center", flexWrap: "wrap" }}>
          <input
            className="input"
            type="date"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
            style={{ width: "200px" }}
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
            <p style={{ color: "#16a34a", fontWeight: 600, marginBottom: "12px" }}>
              {calcResult.message}
            </p>
            <div className="stats-grid">
              <div className="stat-card">
                <p className="stat-card-title">New Clients</p>
                <h3 className="stat-card-value">{calcResult.newClients}</h3>
              </div>
              <div className="stat-card">
                <p className="stat-card-title">Existing Clients</p>
                <h3 className="stat-card-value">{calcResult.existingClients}</h3>
              </div>
              <div className="stat-card">
                <p className="stat-card-title">Left</p>
                <h3 className="stat-card-value">{calcResult.leftClients}</h3>
              </div>
              <div className="stat-card">
                <p className="stat-card-title">Transferred</p>
                <h3 className="stat-card-value">{calcResult.transferredClients}</h3>
              </div>
              <div className="stat-card">
                <p className="stat-card-title">Scope (New)</p>
                <h3 className="stat-card-value">{fmt(calcResult.totalScopeNew)}</h3>
              </div>
              <div className="stat-card">
                <p className="stat-card-title">Scope (Delta)</p>
                <h3 className="stat-card-value">{fmt(calcResult.totalScopeDelta)}</h3>
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
                <h3 className="stat-card-value">{fmt(calcResult.netCommission)}</h3>
              </div>
            </div>
          </div>
        )}
      </div>

      {resultsError && (
        <p style={{ color: "#dc2626", marginBottom: "16px" }}>{resultsError}</p>
      )}

      {/* Summary per agent */}
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
                    <td>{row.agentName} <span style={{ color: "#6b7280", fontSize: "12px" }}>({row.agentCode})</span></td>
                    <td style={{ textAlign: "right" }}>{fmt(row.scopeNew)}</td>
                    <td style={{ textAlign: "right" }}>{fmt(row.scopeDelta)}</td>
                    <td style={{ textAlign: "right", color: "#dc2626" }}>-{fmt(row.clawbacks)}</td>
                    <td style={{ textAlign: "right" }}>{fmt(row.nifra)}</td>
                    <td style={{ textAlign: "right", fontWeight: 600 }}>{fmt(row.netCommission)}</td>
                    <td style={{ textAlign: "right" }}>{row.transactionCount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Transaction detail */}
      {transactions.length > 0 && (
        <div className="card">
          <h2 style={{ margin: "0 0 16px" }}>Transaction Detail</h2>
          <div style={{ overflowX: "auto" }}>
            <table className="simple-table">
              <thead>
                <tr>
                  <th>Agent</th>
                  <th>Member</th>
                  <th>Account</th>
                  <th>Reason</th>
                  <th style={{ textAlign: "right" }}>Prev Balance</th>
                  <th style={{ textAlign: "right" }}>Curr Balance</th>
                  <th style={{ textAlign: "right" }}>Amount</th>
                  <th>Direction</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((tx) => (
                  <tr key={tx.id}>
                    <td>{tx.agentName}</td>
                    <td>{tx.memberName}</td>
                    <td>{tx.accountNumber}</td>
                    <td>
                      <span className={`badge badge-${tx.reason.toLowerCase().replace("_", "-")}`}>
                        {formatReason(tx.reason)}
                      </span>
                    </td>
                    <td style={{ textAlign: "right" }}>{fmt(tx.previousBalance)}</td>
                    <td style={{ textAlign: "right" }}>{fmt(tx.currentBalance)}</td>
                    <td
                      style={{
                        textAlign: "right",
                        fontWeight: 600,
                        color: tx.direction === "DEBIT" ? "#dc2626" : "#16a34a",
                      }}
                    >
                      {tx.direction === "DEBIT" ? "-" : "+"}{fmt(tx.commissionAmount)}
                    </td>
                    <td>{tx.direction}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
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

export default CommissionsPage;
