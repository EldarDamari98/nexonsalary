const BASE_URL = "http://localhost:8081/balances";

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

export async function getBalanceUploads() {
  const response = await fetch(`${BASE_URL}/uploads`);

  if (!response.ok) {
    throw new Error("Failed to fetch balance uploads");
  }

  return response.json();
}

export async function deleteBalanceUpload(uploadId) {
  const response = await fetch(`${BASE_URL}/uploads/${uploadId}`, {
    method: "DELETE",
  });

  let data = {};
  try {
    data = await response.json();
  } catch {
    data = {};
  }

  if (!response.ok) {
    throw new Error(data.message || "Failed to delete upload");
  }

  return data;
}