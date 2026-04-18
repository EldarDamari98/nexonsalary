const BASE_URL = "http://localhost:8081/commissions";

export async function calculateCommissions(month) {
  const response = await fetch(`${BASE_URL}/calculate?month=${month}`, {
    method: "POST",
  });

  const data = await response.json();

  if (!response.ok) {
    const err = new Error(data.message || "Calculation failed");
    err.alreadyCalculated = data.message?.includes("already calculated");
    err.noData = data.message?.includes("No balance data");
    throw err;
  }

  return data;
}

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

export async function getCommissionSummary(month) {
  const response = await fetch(`${BASE_URL}/summary?month=${month}`);

  if (!response.ok) {
    throw new Error("Failed to fetch commission summary");
  }

  return response.json();
}

export async function getCommissionTransactions(month, agentId) {
  const params = new URLSearchParams({ month });
  if (agentId) params.append("agentId", agentId);

  const response = await fetch(`${BASE_URL}/transactions?${params}`);

  if (!response.ok) {
    throw new Error("Failed to fetch commission transactions");
  }

  return response.json();
}

export async function getCommissionExplorerSummary({
  month = "",
  period = "MONTH",
  search = "",
  agentId = "",
  reason = "",
  direction = "",
} = {}) {
  const params = new URLSearchParams({
    period,
    search,
    reason,
    direction,
  });

  if (month) params.append("month", month);
  if (agentId) params.append("agentId", String(agentId));

  const response = await fetch(`${BASE_URL}/explorer-summary?${params.toString()}`);

  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || "Failed to fetch explorer summary");
  }

  return response.json();
}

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
    period,
    search,
    reason,
    direction,
    page: String(page),
    size: String(size),
    sortBy,
    sortDirection,
  });

  if (month) params.append("month", month);
  if (agentId) params.append("agentId", String(agentId));

  const response = await fetch(
    `${BASE_URL}/explorer-transactions?${params.toString()}`
  );

  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || "Failed to fetch explorer transactions");
  }

  return response.json();
}