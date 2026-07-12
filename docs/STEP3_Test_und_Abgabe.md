# Step 3 – Test und Abgabe

## Testen der Verteilung

Um die echte Verteilung zu beweisen wurden zwei CLI-Clients gleichzeitig in separaten Terminals gestartet. Beide Athleten (Tuana und Toni) erschienen live im GUI-Dashboard, obwohl sie in komplett separaten JVM-Prozessen liefen.

## Verwendete KI-Tools

Für Aufgabe A wurde Bolt (bolt.new) verwendet. Per Prompt wurde ein modernes Web-Dashboard in React und Tailwind CSS generiert, das direkt im Browser läuft.

Für Aufgabe B und C wurde Claude (claude.ai) als KI-Assistent verwendet. Die Projektidee wurde beschrieben und Claude hat daraufhin Vorschläge für die Architektur gemacht. Der Code wurde schrittweise generiert, getestet und durch iteratives Feedback verfeinert bis das System vollständig funktioniert hat.

## Was ich gelernt habe

Durch dieses Projekt habe ich verstanden, wie verteilte Systeme funktionieren. Der wichtigste Unterschied zu einer normalen App ist, dass die Prozesse keinen gemeinsamen Speicher teilen und ausschließlich über das Netzwerk kommunizieren. Das macht das System flexibel, weil beliebig viele Clients gleichzeitig verbunden sein können.

Außerdem habe ich gelernt wie wichtig Thread-Sicherheit ist. Ohne ConcurrentHashMap im Server und ohne invokeLater() in der GUI würde das System bei mehreren gleichzeitigen Verbindungen abstürzen.

## Abgabe

Repository: https://github.com/tuanaaaydin/TrailSync

Bolt Web-Dashboard: https://outdoor-activity-das-ocin.bolt.host
