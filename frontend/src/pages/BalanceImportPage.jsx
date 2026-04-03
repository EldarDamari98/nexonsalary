import { useState } from "react";

function BalanceImportPage() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleUpload = async () => {
    if (!file) {
      setMessage("Please select a file");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      setLoading(true);
      setMessage("");

      const response = await fetch("http://localhost:8080/api/balances/upload", {
        method: "POST",
        body: formData,
      });

      const data = await response.json();

      if (response.ok) {
        setMessage(`Success: ${data.importedRows} records imported`);
      } else {
        setMessage(data.message || "Upload failed");
      }
    } catch (error) {
      setMessage("Upload failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: "24px" }}>
      <h2>Upload Balance File</h2>

      <input
        type="file"
        accept=".xlsx,.xls"
        onChange={(e) => setFile(e.target.files[0])}
      />

      <div style={{ marginTop: "16px" }}>
        <button onClick={handleUpload} disabled={loading}>
          {loading ? "Uploading..." : "Upload"}
        </button>
      </div>

      {message && <p style={{ marginTop: "16px" }}>{message}</p>}
    </div>
  );
}

export default BalanceImportPage;