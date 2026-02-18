import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import logo from "../../assets/logo.svg";
import "./Login.css";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await api.post("/api/auth/login", {
        username,
        password,
      });
      console.log("login response", res.data);
      if (res.status === 200 && res.data?.token) {
        localStorage.setItem(
          "auth",
          JSON.stringify({ token: res.data.token, user: res.data.user })
        );
        console.log('token saved:', res.data.token);
        // prefer router navigation but fall back to full reload if it doesn't work
        try {
          navigate("/home", { replace: true });
        } catch (e) {
          window.location.href = "/home";
        }
        return;
      }
      setMessage("Login failed: invalid server response");
    } catch (err) {
      console.error(err);
      const server = err?.response?.data;
      setMessage(server || "Invalid username or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="card-inner">
          <div className="brand-side">
            <img src={logo} alt="SM logo" className="brand-logo-side" />
            <div className="brand-text-side">
              <h2 className="product-name-side">SYSLOGMANAGER</h2>
              <p className="product-desc-side">Monitor logs, analyze events, manage security insights.</p>
            </div>
          </div>

          <div className="form-side">
            <form onSubmit={submit} className="login-form">
              <div className="form-group">
                <label>Username</label>
                <input
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Password</label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>

              <button className="login-btn" disabled={loading}>{loading ? 'Logging in…' : 'Login'}</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
