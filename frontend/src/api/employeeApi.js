const BASE_URL = "http://localhost:8080/employees";

export async function getAllEmployees() {
  const response = await fetch(BASE_URL);

  if (!response.ok) {
    throw new Error("Failed to fetch employees");
  }

  return response.json();
}