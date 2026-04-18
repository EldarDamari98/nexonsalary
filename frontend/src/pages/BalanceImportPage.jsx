import { useEffect, useState } from "react";
import {
  deleteBalanceUpload,
  getBalanceUploads,
  uploadBalanceFile,
} from "../api/balanceApi";

function BalanceImportPage() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [uploads, setUploads] = useState([]);
  const [loadingUploads, setLoadingUploads] = useState(true);
  const [deletingUploadId, setDeletingUploadId] = useState(null);

  useEffect(() => {
    loadUploads();
  }, []);

  const loadUploads = async () => {
    setLoadingUploads(true);
    try {
      const data = await getBalanceUploads();
      setUploads(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingUploads(false);
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setError("Please choose a file first");
      return;
    }

    setUploading(true);
    setError("");
    setResult(null);

    try {
      const data = await uploadBalanceFile(selectedFile);
      setResult(data);
      setSelectedFile(null);
      await loadUploads();
    } catch (err) {
      setError(err.message || "Upload failed");
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteUpload = async (uploadId, fileName) => {
    const confirmed = window.confirm(
      `Delete upload "${fileName}" and all balances imported by it?`
    );

    if (!confirmed) return;

    setDeletingUploadId(uploadId);
    setError("");
    setResult(null);

    try {
      await deleteBalanceUpload(uploadId);
      await loadUploads();
    } catch (err) {
      setError(err.message || "Delete failed");
    } finally {
      setDeletingUploadId(null);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Balance Import</h1>
          <p>Upload monthly member balances from Excel files</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: "24px" }}>
        <h2 style={{ margin: "0 0 16px" }}>Upload Balance File</h2>

        <div className="filters-bar">
          <input
            className="input"
            type="file"
            accept=".xlsx,.xls"
            onChange={(e) => setSelectedFile(e.target.files?.[0] || null)}
          />

          <button
            className="primary-btn"
            onClick={handleUpload}
            disabled={uploading}
          >
            {uploading ? "Uploading..." : "Upload File"}
          </button>
        </div>

        {selectedFile && (
          <p style={{ color: "#6b7280", marginTop: "8px" }}>
            Selected file: <strong>{selectedFile.name}</strong>
          </p>
        )}

        {error && (
          <p style={{ color: "#dc2626", marginTop: "16px" }}>{error}</p>
        )}

        {result && (
          <div style={{ marginTop: "20px" }}>
            <p style={{ color: "#16a34a", fontWeight: 600 }}>
              {result.message}
            </p>

            <div className="stats-grid" style={{ marginTop: "16px" }}>
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

      <div className="card">
        <div className="page-header" style={{ marginBottom: "16px" }}>
          <div>
            <h2 style={{ margin: 0 }}>Upload History</h2>
            <p style={{ margin: "8px 0 0", color: "#6b7280" }}>
              View all uploaded files and delete a specific upload
            </p>
          </div>

          <div className="page-actions">
            <button className="secondary-btn" onClick={loadUploads}>
              Refresh
            </button>
          </div>
        </div>

        {loadingUploads ? (
          <p>Loading uploads...</p>
        ) : uploads.length === 0 ? (
          <p>No uploads yet</p>
        ) : (
          <div style={{ overflowX: "auto" }}>
            <table className="simple-table">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>Uploaded At</th>
                  <th>Imported Rows</th>
                  <th>Created Agents</th>
                  <th>Created Members</th>
                  <th>Created Accounts</th>
                  <th>Created Balances</th>
                  <th>Updated Balances</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {uploads.map((upload) => (
                  <tr key={upload.uploadId}>
                    <td>{upload.fileName}</td>
                    <td>{formatDateTime(upload.uploadedAt)}</td>
                    <td>{upload.importedRows}</td>
                    <td>{upload.createdAgents}</td>
                    <td>{upload.createdMembers}</td>
                    <td>{upload.createdAccounts}</td>
                    <td>{upload.createdBalances}</td>
                    <td>{upload.updatedBalances}</td>
                    <td>
                      <button
                        className="secondary-btn"
                        onClick={() =>
                          handleDeleteUpload(upload.uploadId, upload.fileName)
                        }
                        disabled={deletingUploadId === upload.uploadId}
                        style={{ color: "#dc2626", borderColor: "#fecaca" }}
                      >
                        {deletingUploadId === upload.uploadId
                          ? "Deleting..."
                          : "Delete"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

function formatDateTime(value) {
  if (!value) return "";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString("en-GB");
}

export default BalanceImportPage;