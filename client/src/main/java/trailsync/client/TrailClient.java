package trailsync.client;

import trailsync.shared.ActivityData;
import java.io.*;
import java.net.*;
import java.util.*;

public class TrailClient {
    private static final int SERVER_PORT = 9001;
    private static final String DEFAULT_HOST = "localhost";
    private static final Map<ActivityData.ActivityType, int[]> SIM_PARAMS = Map.of(
        ActivityData.ActivityType.RUNNING, new int[]{130, 160, 120, 180},
        ActivityData.ActivityType.HIKING,  new int[]{100, 140,  60, 300},
        ActivityData.ActivityType.CYCLING, new int[]{ 90, 150,   0, 150}
    );
    private final String host;
    private final String clientName;
    private final Random random = new Random();

    public TrailClient(String host, String clientName) {
        this.host = host;
        this.clientName = clientName;
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        Scanner scanner = new Scanner(System.in);
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║    🏃 TrailSync Tracker Client       ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Dein Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Athlet";
        new TrailClient(host, name).run(scanner);
    }

    public void run(Scanner scanner) throws Exception {
        System.out.println("\nBefehle: start | status | stop | quit");
        try (Socket socket = new Socket(host, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            System.out.println("✅ Verbunden!\n");
            ActivityData currentSession = null;
            Timer updateTimer = null;
            while (true) {
                System.out.print("> ");
                String cmd = scanner.nextLine().trim().toLowerCase();
                switch (cmd) {
                    case "start" -> {
                        if (currentSession != null) { System.out.println("⚠️ Stoppe zuerst."); break; }
                        ActivityData.ActivityType type = chooseActivity(scanner);
                        String sid = clientName + "-" + System.currentTimeMillis();
                        currentSession = new ActivityData(sid, clientName, type);
                        System.out.println("🚀 Tracking gestartet: " + type);
                        final ActivityData session = currentSession;
                        updateTimer = new Timer("GPS", true);
                        updateTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override public void run() {
                                simulateStep(session);
                                try {
                                    session.updateTimestamp();
                                    out.writeObject(session);
                                    out.flush();
                                    out.reset();
                                } catch (IOException e) { cancel(); }
                            }
                        }, 0, 2000);
                    }
                    case "status" -> {
                        if (currentSession == null) System.out.println("❌ Keine Session.");
                        else printStatus(currentSession);
                    }
                    case "stop" -> {
                        if (currentSession == null) { System.out.println("❌ Keine Session."); break; }
                        if (updateTimer != null) updateTimer.cancel();
                        currentSession.setSessionEnd(true);
                        currentSession.updateTimestamp();
                        out.writeObject(currentSession);
                        out.flush();
                        System.out.println("🏁 Beendet!");
                        printStatus(currentSession);
                        currentSession = null;
                    }
                    case "quit" -> {
                        if (updateTimer != null) updateTimer.cancel();
                        if (currentSession != null) {
                            currentSession.setSessionEnd(true);
                            out.writeObject(currentSession);
                            out.flush();
                        }
                        System.out.println("👋 Tschüss!"); return;
                    }
                    default -> System.out.println("Unbekannt: start | status | stop | quit");
                }
            }
        } catch (ConnectException e) {
            System.err.println("❌ Kein Server auf " + host + ":" + SERVER_PORT);
        }
    }

    private ActivityData.ActivityType chooseActivity(Scanner sc) {
        System.out.println("1) Laufen  2) Wandern  3) Radfahren");
        System.out.print("Wahl: ");
        return switch (sc.nextLine().trim()) {
            case "2" -> ActivityData.ActivityType.HIKING;
            case "3" -> ActivityData.ActivityType.CYCLING;
            default  -> ActivityData.ActivityType.RUNNING;
        };
    }

    private void simulateStep(ActivityData data) {
        int[] p = SIM_PARAMS.getOrDefault(data.getType(), new int[]{110, 150, 80, 100});
        data.setDistanceKm(data.getDistanceKm() + 0.01 + random.nextDouble() * 0.04);
        if (p[2] > 0) data.setSteps(data.getSteps() + p[2] + random.nextInt(40));
        data.setHeartRate(p[0] + random.nextInt(p[1] - p[0]));
        data.setElevationM(Math.max(0, data.getElevationM() + random.nextInt(p[3]/10) - p[3]/20));
    }

    private void printStatus(ActivityData d) {
        System.out.println("\n┌────────── Status ──────────┐");
        System.out.printf("│ Aktivität : %-16s│%n", d.getType());
        System.out.printf("│ Distanz   : %-13.2f km  │%n", d.getDistanceKm());
        System.out.printf("│ Schritte  : %-16d│%n", d.getSteps());
        System.out.printf("│ Puls      : %-13d bpm │%n", d.getHeartRate());
        System.out.printf("│ Höhe      : %-13.0f m   │%n", d.getElevationM());
        System.out.println("└────────────────────────────┘\n");
    }
}