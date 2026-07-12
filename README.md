# 🏃 TrailSync – Verteiltes Outdoor-Tracking System

## Live Demo

### Aufgabe A – Web-GUI (erstellt mit Bolt)
🌐 **[TrailSync Web-Dashboard öffnen](https://outdoor-activity-das-ocin.bolt.host)**

> Erstellt mit Bolt.new.
> Zeigt das Dashboard-Design mit Statistik-Karten,
> Aktivitätsfilter und Live-Tabelle.

### Aufgabe B – Pet-Projekt (TrailSync CLI-Tracker)
TrailSync ist ein mittleres Pet-Projekt. Ein vollständiger
Aktivitäts-Tracker bei dem Nutzer ihre Outdoor-Aktivitäten
live tracken können.

**Features:**
- Aktivitätsauswahl: Laufen, Wandern, Radfahren
- Automatische GPS-Simulation alle 2 Sekunden
- Live-Anzeige von Distanz, Puls, Schritte, Höhenmeter
- Mehrere Nutzer gleichzeitig trackbar
- Session starten, pausieren und beenden

### Aufgabe C – Verteilte Java-App
TrailSync besteht aus drei separaten JVM-Prozessen, die ausschließlich über
TCP-Sockets miteinander kommunizieren und keinen gemeinsamen Speicher teilen.
Der CLI-Client sendet alle 2 Sekunden Aktivitätsdaten an den Server. Der Server
verarbeitet sie und leitet sie weiter an das Java Swing Dashboard, das alles
live anzeigt. Der Screenshot unten zeigt zwei Athleten die gleichzeitig in
getrennten Prozessen tracken. Das ist der Beweis für die echte Verteilung.

---

## Verwendete KI-Tools

| Aufgabe | Tool | Ergebnis |
|---------|------|----------|
| A – Web-GUI | **Bolt (bolt.new)** | Web-Dashboard, live unter obigem Link |
| B & C – Java-App | **Claude (claude.ai)** | Verteilte Java-App mit Server, Client, GUI |

---

## Projektidee
TrailSync ist ein verteiltes Echtzeit-System zum Tracken von
Outdoor-Aktivitäten (Laufen, Wandern, Radfahren).
Entwickelt als Einsendeaufgabe für das Modul Softwaretechnik.

---

## Warum ist es wirklich verteilt?
Das System besteht aus **3 separaten JVM-Prozessen**:

| Prozess | Modul | Aufgabe |
|---------|-------|---------|
| 1 | `client` (CLI) | Simuliert GPS, sendet Daten via TCP an Port 9001 |
| 2 | `server` | Empfängt Daten, verarbeitet sie, broadcastet an Port 9002 |
| 3 | `gui` | Empfängt vom Server, zeigt Live-Dashboard in Java Swing |

Die Prozesse teilen **keinen gemeinsamen Speicher**.
Kommunikation läuft ausschließlich über **TCP-Sockets**.

---

## Architektur

    CLI-Client  ->  [TCP Port 9001]  ->  Server  ->  [TCP Port 9002]  ->  GUI
    (Prozess 1)                       (Prozess 2)                     (Prozess 3)
    
---

## Module
| Modul | Datei | Beschreibung |
|-------|-------|-------------|
| `shared` | `ActivityData.java` | Gemeinsames Datenobjekt (Serializable) |
| `shared` | `ServerMessage.java` | Nachrichten vom Server an die GUI |
| `server` | `TrailServer.java` | TCP-Server auf Port 9001 + 9002 |
| `client` | `TrailClient.java` | CLI-Tracker mit GPS-Simulation |
| `gui` | `TrailGUI.java` | Java Swing Live-Dashboard |

---

## Technische Konzepte

### Java Object Serialization
Daten werden als Byte-Stream über TCP übertragen.
ActivityData implementiert Serializable – der Client sendet
das Objekt mit writeObject(), der Server empfängt es mit readObject().

### Multithreading im Server
Für jeden neuen Client startet der Server einen eigenen Thread.
Die Session-Verwaltung nutzt ConcurrentHashMap für Thread-Sicherheit.

### Swing EDT-Thread-Safety
Alle UI-Updates laufen via SwingUtilities.invokeLater()
im Event Dispatch Thread – sonst würde die GUI einfrieren.

---

## Build & Start

### Voraussetzungen
- Java 17 oder höher installiert
- macOS / Linux (für build.sh)

### 1. Repository klonen
```bash
git clone https://github.com/tuanaaaydin/TrailSync.git
cd TrailSync
```

### 2. Kompilieren
```bash
chmod +x build.sh
./build.sh
```

### 3. Starten – 3 separate Terminals öffnen!

**Terminal 1 – Server zuerst starten:**
```bash
cd server
java -cp out:../shared/out trailsync.server.TrailServer
```

**Terminal 2 – GUI starten:**
```bash
cd gui
java -cp out:../shared/out trailsync.gui.TrailGUI
```

**Terminal 3 – CLI Client starten:**
```bash
cd client
java -cp out:../shared/out trailsync.client.TrailClient
```

### 4. CLI-Befehle
- `start` → Aktivität starten (Laufen / Wandern / Radfahren wählen)
- `status` → Aktuelle Stats anzeigen
- `stop` → Aktivität beenden
- `quit` → Programm beenden

---

## Verteilung beweisen
Starte 2 CLI-Clients gleichzeitig in verschiedenen Terminals –
beide erscheinen live im GUI-Dashboard, obwohl sie in
komplett separaten JVM-Prozessen laufen!

---

## Screenshots

### Aufgabe A – Verwendeter Prompt in Bolt
<img width="1434" height="721" alt="Bildschirmfoto 2026-07-12 um 22 07 52" src="https://github.com/user-attachments/assets/6375390a-df6f-4652-92ce-9350e3d54125" />

### Aufgabe A – Bolt Web-GUI
<img width="1439" height="723" alt="Bildschirmfoto 2026-07-12 um 17 22 13" src="https://github.com/user-attachments/assets/0ba31f7e-ee87-465d-a85c-f62556bebce7" />

### Aufgabe B – CLI-Client (Terminal)
<img width="634" height="359" alt="Bildschirmfoto 2026-07-12 um 21 24 15" src="https://github.com/user-attachments/assets/cef253e7-d902-47ae-bda4-cb14e3e7faf3" />

### Aufgabe B & C – Java Swing GUI (2 Athleten live)
<img width="990" height="642" alt="Bildschirmfoto 2026-07-12 um 15 03 54" src="https://github.com/user-attachments/assets/60c54a82-049c-4d4a-9ceb-c99488238dc7" />

---

## Tools & Technologien
| Kategorie | Tool |
|-----------|------|
| Sprache | Java 17 |
| GUI | Java Swing |
| Web-Frontend | React + Tailwind CSS (via Bolt) |
| Netzwerk | TCP Sockets (java.net) |
| Serialisierung | Java Object Serialization |
| Editor | VS Code mit Java Extension Pack |
| Versionskontrolle | Git / GitHub |
| KI-Tools | Claude, Bolt |
