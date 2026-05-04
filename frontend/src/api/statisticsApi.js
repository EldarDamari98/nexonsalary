// Base URL for all statistics-related API calls
const BASE_URL = "http://localhost:8081/statistics";

/**
 * Shared helper that fetches a URL and throws a readable error if it fails.
 *
 * @param {string} url          - the full URL to fetch
 * @param {string} errorMessage - the message to include in the thrown Error
 * @returns {Promise<any>} parsed JSON response
 * @throws {Error} with the given error message if the response is not OK
 */
async function fetchJson(url, errorMessage) {
  const response = await fetch(url);

  if (!response.ok) {
    throw new Error(errorMessage);
  }

  return response.json();
}

/**
 * Fetches high-level system-wide statistics for the overview cards.
 *
 * Returns: totalAgents, totalMembers, totalAccounts, totalAssets, currentMonthlySalary.
 *
 * @returns {Promise<Object>} statistics overview object
 */
export function getStatisticsOverview() {
  return fetchJson(`${BASE_URL}/overview`, "Failed to fetch statistics overview");
}

/**
 * Fetches total assets under management grouped by month — used for the trend line chart.
 *
 * Each item in the returned array has a date and a totalBalance value,
 * representing the sum of all account balances for that month.
 *
 * @returns {Promise<Array>} array of { date, totalBalance } data points
 */
export function getAssetsTrend() {
  return fetchJson(`${BASE_URL}/assets-trend`, "Failed to fetch assets trend");
}

/**
 * Fetches total commissions earned grouped by month — used for the commission trend chart.
 *
 * Each item has a date and a netCommission value (credits minus debits/clawbacks).
 *
 * @returns {Promise<Array>} array of { date, netCommission } data points
 */
export function getCommissionTrend() {
  return fetchJson(`${BASE_URL}/commission-trend`, "Failed to fetch commission trend");
}

/**
 * Fetches the top-performing agents ranked by total commission earned.
 *
 * Used for the leaderboard table. Each item includes agentName and totalCommission.
 *
 * @returns {Promise<Array>} array of agents sorted by commission descending
 */
export function getTopAgents() {
  return fetchJson(`${BASE_URL}/top-agents`, "Failed to fetch top agents");
}

/**
 * Fetches commission totals broken down by reason type — used for the pie chart.
 *
 * Each item has a reason label (e.g. "TRAIL_COMMISSION") and a total amount.
 *
 * @returns {Promise<Array>} array of { reason, total } breakdown items
 */
export function getReasonBreakdown() {
  return fetchJson(`${BASE_URL}/reason-breakdown`, "Failed to fetch reason breakdown");
}

/**
 * Fetches new vs. lost clients per month — used for the client movement bar chart.
 *
 * Each item has a month, newClients count, and leftClients count.
 *
 * @returns {Promise<Array>} array of monthly client movement data points
 */
export function getClientMovement() {
  return fetchJson(`${BASE_URL}/client-movement`, "Failed to fetch client movement");
}
