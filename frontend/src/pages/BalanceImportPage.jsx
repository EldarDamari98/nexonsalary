import { useState } from "react";
import { uploadBalanceFile } from "../api/balanceApi";

function BalanceImportPage() {
  const [file, setFile] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleUpload = async () => {
    if (!file) {
      setError("Please select a file");
      setResult(null);
      return;
    }

    try {
      setLoading(true);
      setError("");
      setResult(null);

      const data = await uploadBalanceFile(file);
      setResult(data);
    } catch (err) {
      setError(err.message || "Upload failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Balance Import</h1>
          <p>Upload monthly balance Excel files and import agents, members and accounts</p>
        </div>
      </div>

      <div className="card">
        <h3>Upload Excel File</h3>

        <input
          className="input"
          type="file"
          accept=".xlsx,.xls"
          onChange={(e) => setFile(e.target.files?.[0] || null)}
        />

        <div style={{ marginTop: "16px" }}>
          <button className="primary-btn" onClick={handleUpload} disabled={loading}>
            {loading ? "Uploading..." : "Upload File"}
          </button>
        </div>

        {error && (
          <p style={{ marginTop: "16px", color: "#dc2626" }}>
            {error}
          </p>
        )}

        {result && (
          <div style={{ marginTop: "24px" }}>
            <p style={{ marginBottom: "16px", color: "#16a34a", fontWeight: 600 }}>
              {result.message}
            </p>

            <div className="stats-grid">
              <div className="stat-card">
                <p className="stat-card-title">Imported Rows</p>
                <h3 className="stat-card-value">{result.importedRows}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Created Agents</p>
                <h3 className="stat-card-value">{result.createdAgents}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Created Members</p>
                <h3 className="stat-card-value">{result.createdMembers}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Created Accounts</p>
                <h3 className="stat-card-value">{result.createdAccounts}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Created Balances</p>
                <h3 className="stat-card-value">{result.createdBalances}</h3>
              </div>

              <div className="stat-card">
                <p className="stat-card-title">Updated Balances</p>
                <h3 className="stat-card-value">{result.updatedBalances}</h3>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default BalanceImportPage;