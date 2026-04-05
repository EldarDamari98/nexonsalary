const BASE_URL = "http://localhost:8080/balances";

export async function uploadBalanceFile(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}/upload`, {
    method: "POST",
    body: formData,
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || "Failed to upload balance file");
  }

  return data;
}

export async function getBalances({
  page = 1,
  size = 10,
  search = "",
  agent = "",
  date = "",
  sortBy = "balanceDate",
  sortDirection = "desc",
} = {}) {
  const queryParams = new URLSearchParams({
    page: String(page),
    size: String(size),
    search,
    agent,
    date,
    sortBy,
    sortDirection,
  });

  const response = await fetch(`${BASE_URL}?${queryParams.toString()}`);

  if (!response.ok) {
    throw new Error("Failed to fetch balances");
  }

  return response.json();
}