export default function Logo({ light }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
      <div
        style={{
          width: 28,
          height: 28,
          background: light ? "#fff" : "#1e40af",
          borderRadius: 6,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          color: light ? "#1e40af" : "#fff",
          fontWeight: "bold",
          fontSize: 16
        }}
      >
        S
      </div>
      <div>
        <div
          style={{
            fontWeight: 700,
            fontSize: 18,
            color: light ? "#fff" : "#111"
          }}
        >
          SYSLOG
        </div>
        <div
          style={{
            fontSize: 12,
            color: light ? "#e0e7ff" : "#666"
          }}
        >
          Manager
        </div>
      </div>
    </div>
  );
}
