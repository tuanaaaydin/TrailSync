package trailsync.gui;

import trailsync.shared.ActivityData;
import trailsync.shared.ServerMessage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class TrailGUI extends JFrame {

    private static final int    SERVER_PORT  = 9002;
    private static final String DEFAULT_HOST = "localhost";

    // Helles, cleanes Farbschema
    private static final Color BG_WHITE      = new Color(255, 255, 255);
    private static final Color BG_LIGHT      = new Color(247, 249, 252);
    private static final Color BG_HEADER     = new Color(255, 255, 255);
    private static final Color BORDER_COLOR  = new Color(220, 225, 235);
    private static final Color ACCENT        = new Color(45, 130, 90);   // Sattgrün
    private static final Color ACCENT_LIGHT  = new Color(235, 248, 242); // Sehr helles Grün
    private static final Color BLUE          = new Color(50, 100, 200);
    private static final Color ORANGE        = new Color(210, 120, 30);
    private static final Color TEXT_DARK     = new Color(25, 35, 45);
    private static final Color TEXT_MID      = new Color(90, 105, 120);
    private static final Color TEXT_LIGHT    = new Color(150, 165, 180);
    private static final Color ROW_HOVER     = new Color(240, 248, 244);
    private static final Color TAG_RUN_BG    = new Color(220, 245, 230);
    private static final Color TAG_RUN_FG    = new Color(30, 130, 70);
    private static final Color TAG_HIKE_BG   = new Color(250, 235, 210);
    private static final Color TAG_HIKE_FG   = new Color(160, 90, 20);
    private static final Color TAG_BIKE_BG   = new Color(215, 230, 255);
    private static final Color TAG_BIKE_FG   = new Color(40, 80, 180);

    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private JLabel lblActiveSessions, lblTotalDist, lblAvgPulse, lblTopAthlete;
    private JLabel lblStatus, lblLive;

    public static void main(String[] args) {
        // System Look deaktivieren – eigenes Design
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        SwingUtilities.invokeLater(() -> {
            TrailGUI gui = new TrailGUI();
            gui.setVisible(true);
            gui.connectToServer(host);
        });
    }

    public TrailGUI() {
        super("TrailSync – Live Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(BG_LIGHT);
        center.setBorder(new EmptyBorder(20, 24, 20, 24));
        center.add(buildStatCards(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(16, 24, 16, 24)
        ));

        // Logo + Titel
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoArea.setBackground(BG_HEADER);

        // Grüner Akzentbalken als "Logo"
        JPanel logoBar = new JPanel();
        logoBar.setBackground(ACCENT);
        logoBar.setPreferredSize(new Dimension(5, 32));

        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 1));
        titleArea.setBackground(BG_HEADER);

        JLabel title = new JLabel("TrailSync");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel("Live Outdoor-Tracking Dashboard");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_LIGHT);

        titleArea.add(title);
        titleArea.add(subtitle);
        logoArea.add(logoBar);
        logoArea.add(titleArea);
        header.add(logoArea, BorderLayout.WEST);

        // Live Badge
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        rightPanel.setBackground(BG_HEADER);

        lblLive = new JLabel("  LIVE  ");
        lblLive.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblLive.setForeground(ACCENT);
        lblLive.setBackground(ACCENT_LIGHT);
        lblLive.setOpaque(true);
        lblLive.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1, true),
            new EmptyBorder(3, 8, 3, 8)
        ));

        new javax.swing.Timer(900, e -> lblLive.setVisible(!lblLive.isVisible())).start();
        rightPanel.add(lblLive);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel buildStatCards() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
        cards.setBackground(BG_LIGHT);

        lblActiveSessions = new JLabel("0", SwingConstants.LEFT);
        lblTotalDist      = new JLabel("0,00 km", SwingConstants.LEFT);
        lblAvgPulse       = new JLabel("-- bpm", SwingConstants.LEFT);
        lblTopAthlete     = new JLabel("-", SwingConstants.LEFT);

        cards.add(buildCard("Aktive Sessions",    lblActiveSessions, ACCENT));
        cards.add(buildCard("Gesamtdistanz",      lblTotalDist,      BLUE));
        cards.add(buildCard("Durchschnittspuls",  lblAvgPulse,       ORANGE));
        cards.add(buildCard("Bester Athlet",      lblTopAthlete,     new Color(130, 60, 180)));

        return cards;
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_MID);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(accent);

        // Dünner farbiger Balken oben
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 3));

        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setBackground(BG_WHITE);
        content.add(titleLbl, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);

        card.add(accentBar, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Tabellen-Header
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(BG_WHITE);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel tableTitle = new JLabel("Aktive Sessions");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        tableTitle.setForeground(TEXT_DARK);
        tableHeader.add(tableTitle, BorderLayout.WEST);

        panel.add(tableHeader, BorderLayout.NORTH);
        panel.add(buildSessionTable(), BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane buildSessionTable() {
        String[] cols = {"Athlet", "Aktivitaet", "Distanz (km)", "Schritte", "Puls", "Hoehe (m)", "Aktualisiert"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        sessionTable = new JTable(tableModel);
        sessionTable.setBackground(BG_WHITE);
        sessionTable.setForeground(TEXT_DARK);
        sessionTable.setGridColor(new Color(240, 243, 248));
        sessionTable.setRowHeight(42);
        sessionTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sessionTable.setShowVerticalLines(false);
        sessionTable.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = sessionTable.getTableHeader();
        header.setBackground(new Color(248, 250, 253));
        header.setForeground(TEXT_MID);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        sessionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);

                setBackground(sel ? ACCENT_LIGHT : (row % 2 == 0 ? BG_WHITE : BG_LIGHT));
                setForeground(TEXT_DARK);
                setBorder(new EmptyBorder(0, 16, 0, 16));
                setFont(new Font("SansSerif", Font.PLAIN, 13));

                // Aktivitaet als farbiges Tag
                if (col == 1 && val != null) {
                    String act = val.toString();
                    setText("  " + act + "  ");
                    switch (act) {
                        case "RUNNING" -> { setBackground(TAG_RUN_BG);  setForeground(TAG_RUN_FG); }
                        case "HIKING"  -> { setBackground(TAG_HIKE_BG); setForeground(TAG_HIKE_FG); }
                        case "CYCLING" -> { setBackground(TAG_BIKE_BG); setForeground(TAG_BIKE_FG); }
                    }
                    setFont(new Font("SansSerif", Font.BOLD, 12));
                }

                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(sessionTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_WHITE);
        return scroll;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_HEADER);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(8, 24, 8, 24)
        ));

        lblStatus = new JLabel("Verbinde mit Server...");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setForeground(TEXT_LIGHT);

        JLabel version = new JLabel("TrailSync v1.0  ·  Java Swing");
        version.setFont(new Font("SansSerif", Font.PLAIN, 11));
        version.setForeground(TEXT_LIGHT);

        bar.add(lblStatus, BorderLayout.WEST);
        bar.add(version, BorderLayout.EAST);
        return bar;
    }

    private void connectToServer(String host) {
        new Thread(() -> {
            while (true) {
                try (Socket socket = new Socket(host, SERVER_PORT);
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText("Verbunden mit TrailSync Server");
                        lblStatus.setForeground(ACCENT);
                    });
                    while (true) {
                        ServerMessage msg = (ServerMessage) in.readObject();
                        SwingUtilities.invokeLater(() -> handleMessage(msg));
                    }
                } catch (ConnectException e) {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText("Server nicht erreichbar – versuche erneut...");
                        lblStatus.setForeground(ORANGE);
                    });
                } catch (EOFException | SocketException e) {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText("Verbindung unterbrochen – reconnecte...");
                        lblStatus.setForeground(ORANGE);
                    });
                } catch (Exception e) {
                    System.err.println("[GUI] " + e.getMessage());
                }
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
                d.getClientName(),
                d.getType().name(),
                String.format("%.2f", d.getDistanceKm()),
                String.format("%,d", d.getSteps()),
                d.getHeartRate() + " bpm",
                String.format("%.0f m", d.getElevationM()),
                d.getTimestamp()
            });
        }

        double totalDist = sessions.stream().mapToDouble(ActivityData::getDistanceKm).sum();
        double avgPulse  = sessions.stream().mapToInt(ActivityData::getHeartRate).average().orElse(0);
        String top = sessions.stream()
                .max(Comparator.comparingDouble(ActivityData::getDistanceKm))
                .map(ActivityData::getClientName).orElse("-");

        lblActiveSessions.setText(String.valueOf(sessions.size()));
        lblTotalDist.setText(String.format("%.2f km", totalDist));
        lblAvgPulse.setText(avgPulse > 0 ? String.format("%.0f bpm", avgPulse) : "-- bpm");
        lblTopAthlete.setText(top);

        if (msg.getType() == ServerMessage.MessageType.SESSION_ENDED && msg.getActivityData() != null)
            lblStatus.setText("Session beendet: " + msg.getActivityData().getClientName());
    }
}