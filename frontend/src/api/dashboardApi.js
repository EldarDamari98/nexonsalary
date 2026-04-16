const BASE_URL = "http://localhost:8081/dashboard";

export async function getDashboardSummary() {
  const response = await fetch(`${BASE_URL}/summary`);

  if (!response.ok) {
    throw new Error("Failed to fetch dashboard summary");
  }

  return response.json();
}