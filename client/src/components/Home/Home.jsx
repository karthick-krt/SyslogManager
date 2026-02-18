import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import Logo from "../Common/Logo";
import "./Home.css";
import api from "../../services/api";

export default function Home() {
  const navigate = useNavigate();

  const logout = async () => {
    try {
      await api.post("/api/auth/logout");
    } catch (err) {
      // ignore errors (token may already be invalid)
    } finally {
      localStorage.removeItem("auth");
      navigate("/login");
    }
  };

  const [loading, setLoading] = useState(true);
  useEffect(() => {
    let mounted = true;
    api
      .get("/api/auth/validate")
      .then(() => {
        if (mounted) setLoading(false);
      })
      .catch(() => {
        // token invalid or session invalid — redirect to login
        localStorage.removeItem("auth");
        navigate("/login");
      });
    return () => {
      mounted = false;
    };
  }, [navigate]);

  if (loading) {
    return (
      <div className="dashboard-loading">
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div>
      <div className="topbar">
        <Logo />
        <button className="logout-btn" onClick={logout}>
          Logout
        </button>
      </div>

      <div className="dashboard-content">
        <h1>Welcome to SYSLOG Manager</h1>
        <p>Monitor logs, analyze events, manage security insights.</p>
      </div>
    </div>
  );
}
