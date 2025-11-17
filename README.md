A cross-platform security agent designed to protect elders from scam calls, malicious USB activity, suspicious network behavior, and unauthorized processes.
ElderGuardianAgent is a lightweight security tool.
The agent runs quietly in the background, monitors risky activity, and reports suspicious behavior to a central dashboard or family member.

The project emphasizes simplicity, low system overhead, and real-world security automation
Main Goals



Detect malicious USB storage devices

Track suspicious network behavior

Monitor unusual processes and scripts

Send alerts & logs to a family-controlled watcher/server

Provide a clean, beautiful Liquid Glass UI with light/dark themes

Work safely inside home and organizational environments
How It Works
Agent Modules

✔ USB monitoring
✔ Network monitoring
✔ Process scanning
✔ Event logging
✔ Real-time alerts
✔ Local history panel
✔ Start/Stop agent controls

Architecture

The agent is built in Java 17+ (Gradle) and includes:

Lightweight UI (Liquid Glass style)

WebSocket server/client (ws-server folder)

Modular scanning system

Event-driven logger

Centralized settings file (build.gradle.kts, settings.gradle.kts)

🚀 Current Features Completed

Full UI redesign (light/dark theme + smooth switch)

Liquid-glass effect panels & modern styling

Start/Stop Agent functionality

History log panel

USB detection logic

Network listener

Gradle build (Windows-ready: .exe with Launch4j)

GitHub repository structure

Cross-platform ZIP build

🏢 Using the Agent in Organizational Computers

ElderGuardianAgent is designed to support enterprise usage:

Silent background mode

Auto-start on login

Minimal system footprint

No admin rights required for most modules

Supports centralized logging & alert routing

Compatible with Windows 10/11 corporate environments

Suitable for SOC teams as a light endpoint monitor

🔧 Tech Stack

Java 17+

Gradle (Kotlin DSL)

WebSocket server (Jetty/Java)

JavaFX / Swing UI components

Liquid Glass custom UI

Launch4j Windows packaging
