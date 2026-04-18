const BASE_URL = "http://localhost:8081/statistics";

async function fetchJson(url, errorMessage) {
  const response = await fetch(url);

  if (!response.ok) {
    throw new Error(errorMessage);
  }

  return response.json();
}

export function getStatisticsOverview() {
  return fetchJson(`${BASE_URL}/overview`, "Failed to fetch statistics overview");
}

export function getAssetsTrend() {
  return fetchJson(`${BASE_URL}/assets-trend`, "Failed to fetch assets trend");
}

export function getCommissionTrend() {
  return fetchJson(`${BASE_URL}/commission-trend`, "Failed to fetch commission trend");
}

export function getTopAgents() {
  return fetchJson(`${BASE_URL}/top-agents`, "Failed to fetch top agents");
}

export function getReasonBreakdown() {
  return fetchJson(`${BASE_URL}/reason-breakdown`, "Failed to fetch reason breakdown");
}

export function getClientMovement() {
  return fetchJson(`${BASE_URL}/client-movement`, "Failed to fetch client movement");
}