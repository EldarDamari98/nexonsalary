// Base URL for all balance-related API calls
const BASE_URL = "http://localhost:8081/balances";

/**
 * Uploads an Excel (.xlsx) file containing monthly balance data.
 *
 * The file is sent as multipart/form-data. The backend parses the Hebrew-column
 * Excel file, creates or updates agents/members/accounts, and saves the balance records.
 *
 * The returned object includes:
 *   - success: whether the import succeeded
 *   - uploadId: the ID of the new BalanceUpload audit record
 *   - importedRows, createdAgents, createdMembers, createdAccounts, createdBalances, updatedBalances
 *   - message: a human-readable status message
 *
 * @param {File} file - the Excel file selected by the user
 * @returns {Promise<Object>} import result summary
 * @throws {Error} if the upload or parsing fails
 */
export async function uploadBalanceFile(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}/upload`, {
    method: "POST",
    body: formData,
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || "Failed to upload balance file");
  }

  return data;
}

/**
 * Fetches a paginated, filtered list of monthly balance records.
 *
 * @param {Object} options - filter and pagination options
 * @param {number} [options.page=1]               - page number (starts at 1)
 * @param {number} [options.size=10]              - records per page
 * @param {string} [options.search=""]            - text search on member name or account number
 * @param {string} [options.agent=""]             - filter by agent name or code
 * @param {string} [options.date=""]              - filter by balance date (YYYY-MM-DD)
 * @param {string} [options.sortBy="balanceDate"] - field to sort by
 * @param {string} [options.sortDirection="desc"] - sort order: "asc" or "desc"
 * @returns {Promise<Object>} paginated response with items array and total count
 * @throws {Error} if the request fails
 */
export async function getBalances({
  page = 1,
  size = 10,
  search = "",
  agent = "",
  date = "",
  sortBy = "balanceDate",
  sortDirection = "desc",
} = {}) {
  const queryParams = new URLSearchParams({
    page: String(page),
    size: String(size),
    search,
    agent,
    date,
    sortBy,
    sortDirection,
  });

  const response = await fetch(`${BASE_URL}?${queryParams.toString()}`);

  if (!response.ok) {
    throw new Error("Failed to fetch balances");
  }

  return response.json();
}

/**
 * Fetches the history of all Excel files that have been imported.
 *
 * Each upload record includes the file name, date of import, and counts
 * of records created or updated during that import.
 *
 * @returns {Promise<Array>} array of upload history objects
 * @throws {Error} if the request fails
 */
export async function getBalanceUploads() {
  const response = await fetch(`${BASE_URL}/uploads`);

  if (!response.ok) {
    throw new Error("Failed to fetch balance uploads");
  }

  return response.json();
}

/**
 * Deletes a specific import upload and all balance records that came from it.
 *
 * This is used to undo a bad import. The deletion cascades to all
 * MonthlyMemberBalance records linked to this upload.
 *
 * @param {number} uploadId - the ID of the upload to delete
 * @returns {Promise<Object>} success message from the server
 * @throws {Error} if the upload is not found or deletion fails
 */
export async function deleteBalanceUpload(uploadId) {
  const response = await fetch(`${BASE_URL}/uploads/${uploadId}`, {
    method: "DELETE",
  });

  let data = {};
  try {
    data = await response.json();
  } catch {
    data = {};
  }

  if (!response.ok) {
    throw new Error(data.message || "Failed to delete upload");
  }

  return data;
}
