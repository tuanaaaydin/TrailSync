# Step 2 – Implementierung

## Reihenfolge der Entwicklung

### Shared-Modul (zuerst)
Als erstes wurden die gemeinsamen Datenklassen erstellt, weil alle drei Prozesse sie benötigen.

ActivityData enthält alle Tracking-Daten eines Athleten (Name, Distanz, Puls, Schritte, Höhe) und implementiert Serializable, damit es über TCP übertragen werden kann.

ServerMessage ist der Umschlag, den der Server an die GUI schickt. Er enthält immer die komplette Liste aller aktiven Sessions.

### Server-Modul (zweiter Schritt)
Der Server lauscht auf zwei Ports gleichzeitig. Port 9001 nimmt Daten von CLI-Clients entgegen und Port 9002 sendet Updates an alle verbundenen GUIs.

Für jeden neuen Client startet der Server einen eigenen Thread, damit mehrere Athleten gleichzeitig tracken können. Die Session-Verwaltung nutzt eine ConcurrentHashMap, weil mehrere Threads gleichzeitig schreiben können.

### Client-Modul (dritter Schritt)
Der CLI-Client simuliert ein GPS-Gerät. Alle 2 Sekunden berechnet ein Timer neue Werte für Distanz, Puls, Schritte und Höhe und sendet das ActivityData-Objekt über TCP an den Server.

### GUI-Modul (vierter Schritt)
Das Java Swing Dashboard verbindet sich mit dem Server und empfängt live Updates. Alle UI-Updates laufen über SwingUtilities.invokeLater() weil Swing nicht thread-sicher ist.

Das Design wurde iterativ verbessert. Zuerst war es ein dunkles Theme und wurde dann auf ein helles cleanes Design umgestellt.

## Probleme und Lösungen

Ein häufiges Problem war das Object-Caching von ObjectOutputStream. Ohne out.reset() nach jedem writeObject() würde Java dasselbe Objekt zwischenspeichern und die GUI würde immer dieselben alten Werte sehen.
