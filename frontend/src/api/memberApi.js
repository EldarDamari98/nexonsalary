const BASE_URL = "http://localhost:8080/members";

export async function getAllMembers() {
  const response = await fetch(BASE_URL);

  if (!response.ok) {
    throw new Error("Failed to fetch members");
  }

  return response.json();
}
