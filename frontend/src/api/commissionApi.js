// Base URL for all commission-related API calls
const BASE_URL = "http://localhost:8081/commissions";

/**
 * Triggers the commission calculation engine for a specific month.
 *
 * Sends all balance records for the given month through the commission rules:
 * perimeter fees for new clients, trail commissions for existing ones, and
 * clawback penalties for clients who left too soon.
 *
 * Will throw an error with alreadyCalculated=true if this month was already processed.
 * In that case, use recalculateCommissions() instead.
 *
 * @param {string} month - the month to calculate in YYYY-MM format (e.g. "2025-01")
 * @returns {Promise<Object>} summary of the calculation result including totals and counts
 * @throws {Error} with alreadyCalculated or noData flags set for known error cases
 */
export async function calculateCommissions(month) {
  const response = await fetch(`${BASE_URL}/calculate?month=${month}`, {
    method: "POST",
  });

  const data = await response.json();

  if (!response.ok) {
    const err = new Error(data.message || "Calculation failed");
    // Flag specific error types so the UI can show the right message
    err.alreadyCalculated = data.message?.includes("already calculated");
    err.noData = data.message?.includes("No balance data");
    throw err;
  }

  return data;
}

/**
 * Deletes existing commissions for a month and recalculates from scratch.
 *
 * Use this when the underlying balance data was corrected after a previous calculation.
 * Unlike calculateCommissions(), this will not fail if commissions already exist.
 *
 * @param {string} month - the month to recalculate in YYYY-MM format (e.g. "2025-01")
 * @returns {Promise<Object>} fresh calculation result summary
 * @throws {Error} if the recalculation fails
 */
export async function recalculateCommissions(month) {
  const response = await fetch(`${BASE_URL}/recalculate?month=${month}`, {
    method: "POST",
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || "Recalculation failed");
  }

  return data;
}

/**
 * Fetches a commission summary for a specific month.
 *
 * The returned object contains totals broken down by commission type:
 *   - totalPerimeterFeeNew, totalPerimeterFeeDelta, totalTrailCommission, totalClawbacks
 *   - netCommission: the final amount after subtracting clawbacks
 *   - newClients, existingClients, transferredClients, leftClients, transactionCount
 *
 * @param {string} month - the month to fetch in YYYY-MM format
 * @returns {Promise<Object>} commission summary object
 * @throws {Error} if the request fails
 */
export async function getCommissionSummary(month) {
  const response = await fetch(`${BASE_URL}/summary?month=${month}`);

  if (!response.ok) {
    throw new Error("Failed to fetch commission summary");
  }

  return response.json();
}

/**
 * Fetches all commission transactions for a given month, optionally filtered by agent.
 *
 * Each transaction includes member name, account number, balance data,
 * commission amount, direction (CREDIT/DEBIT), and the reason it was generated.
 *
 * @param {string} month            - the month to fetch in YYYY-MM format
 * @param {number|null} [agentId]   - optional agent ID to filter results to a single agent
 * @returns {Promise<Array>} array of commission transaction objects
 * @throws {Error} if the request fails
 */
export async function getCommissionTransactions(month, agentId) {
  const params = new URLSearchParams({ month });
  if (agentId) params.append("agentId", agentId);

  const response = await fetch(`${BASE_URL}/transactions?${params}`);

  if (!response.ok) {
    throw new Error("Failed to fetch commission transactions");
  }

  return response.json();
}

/**
 * Fetches aggregated commission totals for the explorer view with flexible filtering.
 *
 * @param {Object} options - filter options
 * @param {string} [options.month=""]       - month filter in YYYY-MM format (empty = all months)
 * @param {string} [options.period="MONTH"] - grouping period: MONTH, YEAR, or ALL
 * @param {string} [options.search=""]      - text search on member name or account number
 * @param {string} [options.agentId=""]     - filter by agent ID
 * @param {string} [options.reason=""]      - filter by commission reason (e.g. "TRAIL_COMMISSION")
 * @param {string} [options.direction=""]   - filter by direction: CREDIT or DEBIT
 * @returns {Promise<Object>} aggregated commission summary
 * @throws {Error} if the request fails
 */
export async function getCommissionExplorerSummary({
  month = "",
  period = "MONTH",
  search = "",
  agentId = "",
  reason = "",
  direction = "",
} = {}) {
  const params = new URLSearchParams({ period, search, reason, direction });
  if (month) params.append("month", month);
  if (agentId) params.append("agentId", String(agentId));

  const response = await fetch(`${BASE_URL}/explorer-summary?${params.toString()}`);

  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || "Failed to fetch explorer summary");
  }

  return response.json();
}

/**
 * Fetches a paginated list of individual commission transactions for the explorer view.
 *
 * @param {Object} options - filter and pagination options
 * @param {string} [options.month=""]              - month filter in YYYY-MM format
 * @param {string} [options.period="MONTH"]        - grouping period: MONTH, YEAR, or ALL
 * @param {string} [options.search=""]             - text search filter
 * @param {string} [options.agentId=""]            - filter by agent ID
 * @param {string} [options.reason=""]             - filter by commission reason
 * @param {string} [options.direction=""]          - filter by direction: CREDIT or DEBIT
 * @param {number} [options.page=1]                - page number (starts at 1)
 * @param {number} [options.size=10]               - records per page
 * @param {string} [options.sortBy="balanceDate"]  - field to sort by
 * @param {string} [options.sortDirection="desc"]  - sort order: "asc" or "desc"
 * @returns {Promise<Object>} paginated response with items array and total count
 * @throws {Error} if the request fails
 */
export async function getCommissionExplorerTransactions({
  month = "",
  period = "MONTH",
  search = "",
  agentId = "",
  reason = "",
  direction = "",
  page = 1,
  size = 10,
  sortBy = "balanceDate",
  sortDirection = "desc",
} = {}) {
  const params = new URLSearchParams({
    period, search, reason, direction,
    page: String(page), size: String(size), sortBy, sortDirection,
  });
  if (month) params.append("month", month);
  if (agentId) params.append("agentId", String(agentId));

  const response = await fetch(`${BASE_URL}/explorer-transactions?${params.toString()}`);

  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || "Failed to fetch explorer transactions");
  }

  return response.json();
}
