// Base URL for all dashboard-related API calls
const BASE_URL = "http://localhost:8081/dashboard";

/**
 * Fetches the high-level summary numbers shown on the main dashboard.
 *
 * The returned object includes:
 *   - totalAgents: total number of agents in the system
 *   - totalMembers: total number of unique clients
 *   - totalAccounts: total number of managed accounts
 *   - totalAssets: total ILS balance across all accounts
 *   - currentMonthlySalary: total commissions for the most recent calculated month
 *   - latestMonth: the month these salary figures refer to (e.g. "2025-01-01")
 *
 * @returns {Promise<Object>} dashboard summary object
 * @throws {Error} if the request fails
 */
export async function getDashboardSummary() {
  const response = await fetch(`${BASE_URL}/summary`);

  if (!response.ok) {
    throw new Error("Failed to fetch dashboard summary");
  }

  return response.json();
}
