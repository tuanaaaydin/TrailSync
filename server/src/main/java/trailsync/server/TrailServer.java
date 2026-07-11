package trailsync.server;

import trailsync.shared.ActivityData;
import trailsync.shared.ServerMessage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TrailServer {

    private static final int CLIENT_PORT = 9001;
    private static final int GUI_PORT    = 9002;

    private final Map<String, ActivityData> activeSessions = new ConcurrentHashMap<>();
    private final List<ObjectOutputStream> guiStreams =
            Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      🏃 TrailSync Server v1.0        ║");
        System.out.println("║  Client-Port : 9001                  ║");
        System.out.println("║  GUI-Port    : 9002                  ║");
        System.out.println("╚══════════════════════════════════════╝");
        new TrailServer().start();
    }

    public void start() throws IOException {
        new Thread(this::listenForClients, "ClientListener").start();
        listenForGUI();
    }

    private void listenForClients() {
        try (ServerSocket ss = new ServerSocket(CLIENT_PORT)) {
            System.out.println("[Server] Warte auf Tracker auf Port " + CLIENT_PORT + "...");
            while (true) {
                Socket client = ss.accept();
                System.out.println("[Server] Tracker verbunden: " + client.getInetAddress());
                new Thread(() -> handleTracker(client)).start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Fehler: " + e.getMessage());
        }
    }

    private void handleTracker(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                ActivityData data = (ActivityData) in.readObject();
                activeSessions.put(data.getSessionId(), data);
                System.out.println("[Server] Update: " + data);

                ServerMessage msg = new ServerMessage(
                        data.isSessionEnd()
                                ? ServerMessage.MessageType.SESSION_ENDED
                                : ServerMessage.MessageType.ACTIVITY_UPDATE);
                msg.setActivityData(data);
                msg.setAllSessions(new ArrayList<>(activeSessions.values()));
                broadcastToGUIs(msg);

                if (data.isSessionEnd()) {
                    activeSessions.remove(data.getSessionId());
                }
            }
        } catch (EOFException e) {
            System.out.println("[Server] Tracker getrennt.");
        } catch (Exception e) {
            System.err.println("[Server] Fehler: " + e.getMessage());
        }
    }

    private void listenForGUI() throws IOException {
        try (ServerSocket gs = new ServerSocket(GUI_PORT)) {
            System.out.println("[Server] Warte auf GUI auf Port " + GUI_PORT + "...");
            while (true) {
                Socket gui = gs.accept();
                System.out.println("[Server] GUI verbunden.");
                ObjectOutputStream out = new ObjectOutputStream(gui.getOutputStream());
                guiStreams.add(out);

                ServerMessage init = new ServerMessage(ServerMessage.MessageType.SESSION_SUMMARY);
                init.setAllSessions(new ArrayList<>(activeSessions.values()));
                init.setInfo("Verbunden. " + activeSessions.size() + " aktive Sessions.");
                out.writeObject(init);
                out.flush();
            }
        }
    }

    private void broadcastToGUIs(ServerMessage message) {
        synchronized (guiStreams) {
            Iterator<ObjectOutputStream> it = guiStreams.iterator();
            while (it.hasNext()) {
                try {
                    ObjectOutputStream out = it.next();
                    out.writeObject(message);
                    out.flush();
                    out.reset();
                } catch (IOException e) {
                    it.remove();
                }
            }
        }
    }
}