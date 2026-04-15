const BASE_URL = "http://localhost:8080/agents";

export async function getAllAgents() {
  const response = await fetch(BASE_URL);

  if (!response.ok) {
    throw new Error("Failed to fetch agents");
  }

  return response.json();
}