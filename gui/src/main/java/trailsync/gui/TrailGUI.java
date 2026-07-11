package trailsync.gui;

import trailsync.shared.ActivityData;
import trailsync.shared.ServerMessage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class TrailGUI extends JFrame {
    private static final int SERVER_PORT = 9002;
    private static final String DEFAULT_HOST = "localhost";
    private static final Color BG_DARK       = new Color(28, 32, 40);
    private static final Color BG_PANEL      = new Color(38, 44, 56);
    private static final Color BG_CARD       = new Color(48, 56, 72);
    private static final Color ACCENT_BLUE   = new Color(64, 156, 255);
    private static final Color ACCENT_GREEN  = new Color(72, 200, 120);
    private static final Color ACCENT_ORANGE = new Color(255, 165, 60);
    private static final Color TEXT_MAIN     = new Color(230, 235, 245);
    private static final Color TEXT_DIM      = new Color(140, 150, 170);

    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalDist, lblActiveSessions, lblAvgPulse, lblTopAthlete, lblStatus;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        SwingUtilities.invokeLater(() -> { TrailGUI g = new TrailGUI(); g.setVisible(true); g.connectToServer(host); });
    }

    public TrailGUI() {
        super("🏃 TrailSync Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(10, 10));
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(0, 15, 0, 15));
        center.add(buildStatCards(), BorderLayout.NORTH);
        center.add(buildSessionTable(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_PANEL);
        h.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("🏃 TrailSync Live Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(TEXT_MAIN);
        JLabel sub = new JLabel("Echtzeit-Tracking von Outdoor-Aktivitäten");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXT_DIM);
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setBackground(BG_PANEL);
        titles.add(title); titles.add(sub);
        h.add(titles, BorderLayout.WEST);
        JLabel live = new JLabel("● LIVE");
        live.setFont(new Font("SansSerif", Font.BOLD, 13));
        live.setForeground(ACCENT_GREEN);
        new javax.swing.Timer(800, e -> live.setVisible(!live.isVisible())).start();
        h.add(live, BorderLayout.EAST);
        return h;
    }

    private JPanel buildStatCards() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setBackground(BG_DARK);
        cards.setBorder(new EmptyBorder(10, 0, 10, 0));
        lblActiveSessions = new JLabel("0", SwingConstants.CENTER);
        lblTotalDist      = new JLabel("0.00 km", SwingConstants.CENTER);
        lblAvgPulse       = new JLabel("-- bpm", SwingConstants.CENTER);
        lblTopAthlete     = new JLabel("–", SwingConstants.CENTER);
        cards.add(buildCard("🏃 Aktive Sessions", lblActiveSessions, ACCENT_BLUE));
        cards.add(buildCard("📏 Gesamtdistanz", lblTotalDist, ACCENT_GREEN));
        cards.add(buildCard("❤️ Ø Puls", lblAvgPulse, new Color(255, 90, 90)));
        cards.add(buildCard("🥇 Top Athlet", lblTopAthlete, ACCENT_ORANGE));
        return cards;
    }

    private JPanel buildCard(String title, JLabel val, Color accent) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 1), new EmptyBorder(12, 15, 12, 15)));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12)); t.setForeground(TEXT_DIM);
        val.setFont(new Font("SansSerif", Font.BOLD, 20)); val.setForeground(accent);
        card.add(t); card.add(val);
        return card;
    }

    private JScrollPane buildSessionTable() {
        String[] cols = {"Athlet","Aktivität","Distanz (km)","Schritte","Puls","Höhe (m)","Aktualisiert"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        sessionTable = new JTable(tableModel);
        sessionTable.setBackground(BG_CARD); sessionTable.setForeground(TEXT_MAIN);
        sessionTable.setGridColor(BG_PANEL); sessionTable.setRowHeight(32);
        sessionTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JTableHeader hdr = sessionTable.getTableHeader();
        hdr.setBackground(BG_PANEL); hdr.setForeground(TEXT_DIM);
        hdr.setFont(new Font("SansSerif", Font.BOLD, 12));
        sessionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? ACCENT_BLUE.darker() : BG_CARD);
                String act = (String) t.getValueAt(row, 1);
                setForeground(sel ? TEXT_MAIN : switch (act != null ? act : "") {
                    case "RUNNING" -> ACCENT_BLUE;
                    case "HIKING"  -> ACCENT_GREEN;
                    case "CYCLING" -> ACCENT_ORANGE;
                    default -> TEXT_MAIN;
                });
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
        JScrollPane scroll = new JScrollPane(sessionTable);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BG_PANEL));
        return scroll;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PANEL); bar.setBorder(new EmptyBorder(8, 15, 8, 15));
        lblStatus = new JLabel("⏳ Verbinde...");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12)); lblStatus.setForeground(TEXT_DIM);
        bar.add(lblStatus, BorderLayout.WEST);
        return bar;
    }

    private void connectToServer(String host) {
        new Thread(() -> {
            while (true) {
                try (Socket socket = new Socket(host, SERVER_PORT);
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    SwingUtilities.invokeLater(() -> { lblStatus.setText("✅ Verbunden"); lblStatus.setForeground(ACCENT_GREEN); });
                    while (true) {
                        ServerMessage msg = (ServerMessage) in.readObject();
                        SwingUtilities.invokeLater(() -> handleMessage(msg));
                    }
                } catch (ConnectException e) {
                    SwingUtilities.invokeLater(() -> { lblStatus.setText("🔴 Kein Server – versuche erneut..."); lblStatus.setForeground(new Color(255,80,80)); });
                } catch (EOFException | SocketException e) {
                    SwingUtilities.invokeLater(() -> { lblStatus.setText("⚠️ Unterbrochen – reconnecte..."); lblStatus.setForeground(ACCENT_ORANGE); });
                } catch (Exception e) { System.err.println("[GUI] " + e.getMessage()); }
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        }, "ServerReceiver").start();
    }

    private void handleMessage(ServerMessage msg) {
        List<ActivityData> sessions = msg.getAllSessions();
        if (sessions == null) sessions = new ArrayList<>();
        tableModel.setRowCount(0);
        for (ActivityData d : sessions) {
            tableModel.addRow(new Object[]{
                d.getClientName(), d.getType().name(),
                String.format("%.2f", d.getDistanceKm()),
                d.getSteps(), d.getHeartRate() + " bpm",
                String.format("%.0f m", d.getElevationM()), d.getTimestamp()
            });
        }
        double tot = sessions.stream().mapToDouble(ActivityData::getDistanceKm).sum();
        double avg = sessions.stream().mapToInt(ActivityData::getHeartRate).average().orElse(0);
        String top = sessions.stream().max(Comparator.comparingDouble(ActivityData::getDistanceKm)).map(ActivityData::getClientName).orElse("–");
        lblActiveSessions.setText(String.valueOf(sessions.size()));
        lblTotalDist.setText(String.format("%.2f km", tot));
        lblAvgPulse.setText(avg > 0 ? String.format("%.0f bpm", avg) : "-- bpm");
        lblTopAthlete.setText(top);
        if (msg.getType() == ServerMessage.MessageType.SESSION_ENDED && msg.getActivityData() != null)
            lblStatus.setText("🏁 Beendet: " + msg.getActivityData().getClientName());
    }
}