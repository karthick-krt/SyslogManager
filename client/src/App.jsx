import { useEffect, useState } from "react";
import api from "./services/api";
import Login from "./Login";

function App() {
  const [message, setMessage] = useState("");

  useEffect(() => {
    api.get("/api/home")
      .then(res => setMessage(res.data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div style={{ padding: "20px" }}>
      <h1>My React + Spring Boot App</h1>
      <p>{message}</p>
      <hr />
      <Login />
    </div>
  );
}

export default App;