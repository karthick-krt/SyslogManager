import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json"
  }
});

// Attach token automatically if present
api.interceptors.request.use(config => {
  const stored = localStorage.getItem('auth');
  if (stored) {
    const obj = JSON.parse(stored);
    if (obj?.token) {
      config.headers.Authorization = `Bearer ${obj.token}`;
    }
  }
  return config;
});

export default api;