import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login/Login";
import Home from "./components/Home/Home";

function RequireAuth({ children }) {
  try {
    const auth = JSON.parse(localStorage.getItem("auth"));
    if (auth && auth.token) return children;
  } catch (e) {
    // ignore parse errors
  }
  return <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Router>
      <Routes>
        {/* Default route */}
        <Route path="/" element={<Navigate to="/home" replace />} />

        {/* Login */}
        <Route path="/login" element={<Login />} />

        {/* Home/Dashboard (protected) */}
        <Route path="/home" element={<RequireAuth><Home /></RequireAuth>} />
      </Routes>
    </Router>
  );
}
