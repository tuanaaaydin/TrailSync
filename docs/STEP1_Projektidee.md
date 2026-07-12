# Step 1 – Projektidee und Planung

## Idee
TrailSync ist ein verteiltes Echtzeit-Tracking-System für Outdoor-Aktivitäten wie Laufen, Wandern und Radfahren.

## Warum dieses Projekt?
Die Idee entstand aus dem persönlichen Interesse an Sport und Fitness. Ein System das mehrere Athleten gleichzeitig trackt und die Daten live auf einem Dashboard anzeigt ist sowohl technisch interessant als auch praktisch nutzbar.

## Geplante Architektur
Das System sollte von Anfang an verteilt sein – also aus mehreren unabhängigen Prozessen bestehen die über ein Netzwerk kommunizieren.

Die drei Prozesse wurden wie folgt geplant:

- Prozess 1 ist der CLI-Client der GPS-Daten simuliert und sendet
- Prozess 2 ist der Server der die Daten empfängt und weiterleitet
- Prozess 3 ist das GUI-Dashboard das alles live anzeigt

## KI-Tool
Für die Planung und Umsetzung wurde Claude (claude.ai) als KI-Assistent verwendet.
