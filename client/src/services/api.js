import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // your Spring Boot URL
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const auth = JSON.parse(localStorage.getItem("auth"));
  if (auth?.token) {
    config.headers.Authorization = `Bearer ${auth.token}`;
  }
  return config;
});

// Handle auth failure globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem("auth");
      window.location.href = "/login"; // 🔥 auto redirect
    }
    return Promise.reject(error);
  }
);

export default api;
