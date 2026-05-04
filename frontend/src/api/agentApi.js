// Base URL for all agent-related API calls
const BASE_URL = "http://localhost:8081/agents";

/**
 * Fetches all agents from the system, including their performance stats.
 *
 * Each agent object in the returned array includes:
 *   - agentId, agentCode, agentName, active status
 *   - membersCount: how many unique clients this agent manages
 *   - accountsCount: how many accounts this agent manages
 *   - totalAssets: total ILS balance across all this agent's accounts
 *   - latestBalanceDate: the most recent month data was imported for this agent
 *   - currentSalary: total commission earned in the latest month
 *
 * @returns {Promise<Array>} array of agent objects
 * @throws {Error} if the request fails
 */
export async function getAllAgents() {
  const response = await fetch(BASE_URL);

  if (!response.ok) {
    throw new Error("Failed to fetch agents");
  }

  return response.json();
}
