# TrailSync – Vision & Mission

## Vision
TrailSync soll langfristig eine vollständige Plattform für Outdoor-Athleten
werden, auf der Sportler ihre Aktivitäten in Echtzeit verfolgen, mit anderen
teilen und auswerten können.

## Mission
TrailSync löst das Problem, dass Gruppen beim gemeinsamen Sport keine
einfache Möglichkeit haben sich gegenseitig live zu beobachten. Die App
ermöglicht es mehreren Athleten gleichzeitig zu tracken und alle Daten
zentral auf einem Dashboard anzuzeigen.

## Roadmap

| Phase | Feature | Status |
|-------|---------|--------|
| 1 | CLI-Tracker mit GPS-Simulation | ✅ Fertig |
| 1 | Java Swing Live-Dashboard | ✅ Fertig |
| 1 | Verteilte Server-Architektur | ✅ Fertig |
| 2 | Echte GPS-Integration via Smartphone | 🔜 Geplant |
| 2 | Aktivitäten speichern und History anzeigen | 🔜 Geplant |
| 3 | Web-Interface als vollständiger Ersatz für Swing-GUI | 🔜 Geplant |
| 3 | Benutzerkonten und Authentifizierung | 🔜 Geplant |

## Techstack

| Kategorie | Technologie | Begründung |
|-----------|-------------|------------|
| Sprache | Java 17 | Plattformunabhängig, objektorientiert, gut für verteilte Systeme |
| GUI | Java Swing | Eingebaut in Java, kein zusätzliches Framework nötig |
| Web-Frontend | React + Tailwind CSS | Modern, komponentenbasiert, via Bolt generiert |
| Netzwerk | TCP Sockets | Direkte Verbindung, geringer Overhead, echte Verteilung |
| Serialisierung | Java Object Serialization | Einfache Übertragung von Java-Objekten ohne manuelles Parsen |
| Build | Bash Script | Einfach, plattformunabhängig, kein Build-Tool nötig |
| Versionskontrolle | Git und GitHub | Standard in der Softwareentwicklung |
| KI-Tools | Claude, Bolt, Kilo Code | Vibe Coding für schnelle Entwicklung |
