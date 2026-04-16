const BASE_URL = "http://localhost:8081/agents";

export async function getAllAgents() {
  const response = await fetch(BASE_URL);

  if (!response.ok) {
    throw new Error("Failed to fetch agents");
  }

  return response.json();
}