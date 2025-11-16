import { WebSocketServer } from "ws";

function startServer(port) {
  try {
    const wss = new WebSocketServer({ port });
    console.log(`✅ WebSocket server running on ws://localhost:${port}`);

    wss.on("connection", (ws) => {
      console.log("🤝 Client connected");
      ws.send("Ping from server");

      ws.on("message", (msg) => console.log("📩 Message from client:", msg));
    });

    wss.on("close", () => console.log("❌ WebSocket closed"));
  } catch (err) {
    if (err.code === "EADDRINUSE") {
      console.log(`⚠️ Порт ${port} занят, пробую ${port + 1}...`);
      startServer(port + 1);
    } else {
      console.error("Ошибка:", err);
    }
  }
}

startServer(9000);

