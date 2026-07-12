# 🏃 TrailSync – Verteiltes Outdoor-Tracking System

## Projektidee
TrailSync ist ein verteiltes Echtzeit-System zum Tracken von
Outdoor-Aktivitäten (Laufen, Wandern, Radfahren) und wurde
als Einsendeaufgabe für das Modul Softwaretechnik entwickelt.

## Warum ist es wirklich verteilt?
Das System besteht aus **3 separaten JVM-Prozessen**:

| Prozess | Modul | Aufgabe |
|---------|-------|---------|
| 1 | `client` (CLI) | Simuliert GPS, sendet Daten via TCP an Port 9001 |
| 2 | `server` | Empfängt Daten, verarbeitet sie, broadcastet an Port 9002 |
| 3 | `gui` | Empfängt vom Server, zeigt Live-Dashboard in Java Swing |

Die Prozesse teilen **keinen gemeinsamen Speicher**.
Kommunikation läuft ausschließlich über **TCP-Sockets**.

## Architektur
CLI-Client  →  [TCP Port 9001]  →  Server  →  [TCP Port 9002]  →  GUI
(Prozess 1)                      (Prozess 2)                    (Prozess 3)

## Module
| Modul | Datei | Beschreibung |
|-------|-------|-------------|
| `shared` | `ActivityData.java` | Gemeinsames Datenobjekt (Serializable) |
| `shared` | `ServerMessage.java` | Nachrichten vom Server an die GUI |
| `server` | `TrailServer.java` | TCP-Server auf Port 9001 + 9002 |
| `client` | `TrailClient.java` | CLI-Tracker mit GPS-Simulation |
| `gui` | `TrailGUI.java` | Java Swing Live-Dashboard |

## Technische Konzepte

### Java Object Serialization
Daten werden als Byte-Stream über TCP übertragen:
ActivityData implementiert Serializable – der Client sendet
das Objekt mit writeObject(), der Server empfängt es mit readObject().

### Multithreading im Server
Für jeden neuen Client startet der Server einen eigenen Thread.
Die Session-Verwaltung nutzt ConcurrentHashMap für Thread-Sicherheit.

### Swing EDT-Thread-Safety
Alle UI-Updates laufen via SwingUtilities.invokeLater()
im Event Dispatch Thread – sonst würde die GUI einfrieren.

## Build & Start

### 1. Kompilieren
```bash
chmod +x build.sh
./build.sh
```

### 2. Starten (3 separate Terminals!)

Terminal 1 – Server zuerst!
```bash
cd server
java -cp out:../shared/out trailsync.server.TrailServer
```

Terminal 2 – GUI
```bash
cd gui
java -cp out:../shared/out trailsync.gui.TrailGUI
```

Terminal 3 – CLI Client
```bash
cd client
java -cp out:../shared/out trailsync.client.TrailClient
```

### 3. CLI-Befehle
- start  → Aktivität starten (Laufen/Wandern/Radfahren wählen)
- status → Aktuelle Stats anzeigen
- stop   → Aktivität beenden
- quit   → Programm beenden

## Verteilung beweisen
Starte 2 CLI-Clients gleichzeitig in verschiedenen Terminals –
beide erscheinen live im GUI-Dashboard, obwohl sie in
komplett separaten JVM-Prozessen laufen!

## Tools
- Sprache: Java 17
- GUI: Java Swing
- Netzwerk: TCP Sockets (java.net)
- Editor: VS Code mit Java Extension Pack
- Versionskontrolle: Git / GitHub

## Screenshots

### GUI – Leer beim Start
![GUI Start](docs/screenshots/gui_start.png)

### GUI – Live Tracking (Tuana läuft!)
![GUI Live](docs/screenshots/gui_live.png)
