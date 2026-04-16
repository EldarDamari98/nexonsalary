const BASE_URL = "http://localhost:8081/commissions";

export async function calculateCommissions(month) {
  const response = await fetch(`${BASE_URL}/calculate?month=${month}`, {
    method: "POST",
  });

  const data = await response.json();

  if (!response.ok) {
    const err = new Error(data.message || "Calculation failed");
    err.alreadyCalculated = data.message?.includes("already calculated");
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
