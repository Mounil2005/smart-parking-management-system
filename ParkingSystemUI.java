// File: ParkingSystemUI.java
// Main Coordinator: Integrates all modules from team members
// This file coordinates LoginPanel, AdminDashboard, UserDashboard, and shared components

import javax.swing.*;
import java.awt.*;

/**
 * ParkingSystemUI - Main application class
 * Coordinates all modules created by team members
 */
public class ParkingSystemUI extends JFrame {
    // Data model
    static class ParkingSlot {
        int id;
        boolean available = true;
        LocalDateTime inTime = null;
        String bookedBy = null;
        String vehicle = null;

        ParkingSlot(int id) { this.id = id; }
    }

    static class Booking {
        int slotId;
        String user;
        String vehicle;
        LocalDateTime inTime;
        LocalDateTime outTime;
        double cost;
        Booking(int slotId, String user, String vehicle, LocalDateTime inTime, LocalDateTime outTime, double cost) {
            this.slotId = slotId; this.user = user; this.vehicle = vehicle;
            this.inTime = inTime; this.outTime = outTime; this.cost = cost;
        }
    }

    private final List<ParkingSlot> slots = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private String currentUser = null;
    private String currentVehicle = null;

    private CardLayout mainCards;
    private JPanel mainPanel;

    // admin panels & models (so we can refresh)
    private JPanel adminContentCards;
    private CardLayout adminCards;
    private DefaultTableModel recordsTableModel;
    private JPanel availabilityListPanel; // rebuildable

    // user panels
    private DefaultListModel<String> userSlotListModel;
    private JLabel userBookingInfo;

    private static final double RATE_PER_HOUR = 20.0;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);  // Indigo
    private static final Color PRIMARY_DARK = new Color(67, 56, 202);
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129);  // Emerald
    private static final Color DANGER_COLOR = new Color(239, 68, 68);  // Red
    private static final Color WARNING_COLOR = new Color(245, 158, 11);  // Amber
    private static final Color BG_COLOR = new Color(249, 250, 251);  // Light gray
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

    public ParkingSystemUI() {
        setTitle("Smart Parking System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);



        // initialize some slots
        for (int i = 1; i <= 8; i++) slots.add(new ParkingSlot(i));

        mainCards = new CardLayout();
        mainPanel = new JPanel(mainCards);

        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createAdminDashboard(), "admin");
        mainPanel.add(createUserDashboardPanel(), "user");

        add(mainPanel);
        mainCards.show(mainPanel, "login");
    }

    // ---------------- LOGIN PANEL ----------------
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        // Left Banner with gradient
        GradientPanel left = new GradientPanel(PRIMARY_COLOR, PRIMARY_DARK);
        left.setPreferredSize(new Dimension(450, 750));
        left.setLayout(new GridBagLayout());
        
        JPanel bannerContent = new JPanel();
        bannerContent.setLayout(new BoxLayout(bannerContent, BoxLayout.Y_AXIS));
        bannerContent.setOpaque(false);
        
        JLabel icon = new JLabel("P", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 80));
        icon.setForeground(Color.WHITE);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel big = new JLabel("SMART PARKING", SwingConstants.CENTER);
        big.setFont(new Font("Segoe UI", Font.BOLD, 42));
        big.setForeground(Color.WHITE);
        big.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Effortless Parking Management", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(224, 231, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        bannerContent.add(icon);
        bannerContent.add(Box.createRigidArea(new Dimension(0, 20)));
        bannerContent.add(big);
        bannerContent.add(Box.createRigidArea(new Dimension(0, 10)));
        bannerContent.add(subtitle);
        
        left.add(bannerContent);

        // Right login area with card
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(BG_COLOR);
        
        // Create card panel
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel("Welcome Back", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(header, gbc);
        
        JLabel subheader = new JLabel("Sign in to continue", SwingConstants.LEFT);
        subheader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subheader.setForeground(TEXT_SECONDARY);
        gbc.gridy++;
        card.add(subheader, gbc);
        
        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        // role select
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(TEXT_PRIMARY);
        card.add(roleLabel, gbc);
        JComboBox<String> roleSelect = new JComboBox<>(new String[]{"User", "Admin"});
        roleSelect.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleSelect.setPreferredSize(new Dimension(250, 38));
        gbc.gridx = 1;
        card.add(roleSelect, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(TEXT_PRIMARY);
        card.add(userLabel, gbc);
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setPreferredSize(new Dimension(250, 38));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridx = 1;
        card.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel secondLabel = new JLabel("Vehicle No:");
        secondLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        secondLabel.setForeground(TEXT_PRIMARY);
        card.add(secondLabel, gbc);
        JTextField vehicleOrPass = new JTextField();
        vehicleOrPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vehicleOrPass.setPreferredSize(new Dimension(250, 38));
        vehicleOrPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridx = 1;
        card.add(vehicleOrPass, gbc);

        // toggle second label for admin
        roleSelect.addActionListener(e -> {
            if (roleSelect.getSelectedItem().equals("Admin")) {
                secondLabel.setText("Password:");
                vehicleOrPass.setText("");
            } else {
                secondLabel.setText("Vehicle No:");
                vehicleOrPass.setText("");
            }
        });

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        card.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        
        gbc.gridy++;
        JButton loginBtn = new ModernButton("Sign In", PRIMARY_COLOR);
        loginBtn.setPreferredSize(new Dimension(250, 44));
        card.add(loginBtn, gbc);

        gbc.gridy++;
        JButton demoUser = new ModernButton("Quick Demo User", SECONDARY_COLOR);
        demoUser.setPreferredSize(new Dimension(250, 44));
        card.add(demoUser, gbc);
        
        // Add card to right panel
        GridBagConstraints rightGbc = new GridBagConstraints();
        right.add(card, rightGbc);

        // actions
        loginBtn.addActionListener(e -> {
            String role = (String) roleSelect.getSelectedItem();
            String user = usernameField.getText().trim();
            String second = vehicleOrPass.getText().trim();
            if (role.equals("Admin")) {
                if (user.equals("admin") && second.equals("1234")) {
                    // go to admin dashboard
                    refreshAdminPanels();
                    mainCards.show(mainPanel, "admin");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid admin credentials (use admin / 1234).");
                }
            } else { // user
                if (user.isEmpty() || second.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Enter username and vehicle number.");
                    return;
                }
                currentUser = user;
                currentVehicle = second;
                refreshUserPanel();
                mainCards.show(mainPanel, "user");
            }
        });

        demoUser.addActionListener(e -> {
            currentUser = "demo_user";
            currentVehicle = "DL-01-DEMO";
            refreshUserPanel();
            mainCards.show(mainPanel, "user");
        });

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);
        return panel;
    }

    // ---------------- ADMIN DASHBOARD ----------------
    private JPanel createAdminDashboard() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBackground(BG_COLOR);

        // Header (gradient)
        GradientPanel header = new GradientPanel(PRIMARY_COLOR, PRIMARY_DARK);
        header.setPreferredSize(new Dimension(1200, 90));
        header.setLayout(new BorderLayout());
        
        JPanel headerContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 25));
        headerContent.setOpaque(false);
        
        JLabel icon = new JLabel("⚙");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        icon.setForeground(Color.WHITE);
        headerContent.add(icon);
        
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerContent.add(title);
        
        header.add(headerContent, BorderLayout.WEST);
        adminPanel.add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 660));
        sidebar.setBackground(CARD_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)),
            new EmptyBorder(25, 20, 25, 20)
        ));

        String[] btns = {"▣ Parking Availability", "≡ User Records", "$ Cost Calculator", "+ Add Slot", "✕ Clear All Bookings", "← Logout"};
        for (String s : btns) {
            JButton b = new SidebarButton(s);
            b.setMaximumSize(new Dimension(220, 48));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(b);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            b.addActionListener(e -> adminSidebarAction(s.replaceAll("[▣≡$+✕←] ", "")));
        }

        adminPanel.add(sidebar, BorderLayout.WEST);

        // Admin content area with internal card layout
        adminCards = new CardLayout();
        adminContentCards = new JPanel(adminCards);
        adminContentCards.setBackground(BG_COLOR);

        // Availability panel (we'll redraw when needed)
        availabilityListPanel = new JPanel();
        availabilityListPanel.setBackground(BG_COLOR);
        availabilityListPanel.setLayout(new BoxLayout(availabilityListPanel, BoxLayout.Y_AXIS));
        availabilityListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JScrollPane availScroll = new JScrollPane(availabilityListPanel);
        availScroll.setBorder(null);
        availScroll.getViewport().setBackground(BG_COLOR);
        adminContentCards.add(availScroll, "availability");

        // Records table with styling
        recordsTableModel = new DefaultTableModel(new Object[]{"Slot", "User", "Vehicle", "In Time", "Out Time", "Cost"}, 0);
        JTable recordsTable = new JTable(recordsTableModel);
        recordsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recordsTable.setRowHeight(32);
        recordsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        recordsTable.getTableHeader().setBackground(new Color(243, 244, 246));
        recordsTable.getTableHeader().setForeground(TEXT_PRIMARY);
        recordsTable.setGridColor(new Color(229, 231, 235));
        recordsTable.setSelectionBackground(new Color(224, 231, 255));
        JScrollPane tableScroll = new JScrollPane(recordsTable);
        tableScroll.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BG_COLOR);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        adminContentCards.add(tablePanel, "records");

        // Cost calculator
        adminContentCards.add(createAdminCostCalculatorPanel(), "calculator");

        // Add slot panel
        adminContentCards.add(createAddSlotPanel(), "addslot");

        adminPanel.add(adminContentCards, BorderLayout.CENTER);
        return adminPanel;
    }

    private void adminSidebarAction(String action) {
        switch (action) {
            case "Parking Availability":
                refreshAdminAvailabilityPanel();
                adminCards.show(adminContentCards, "availability");
                break;
            case "User Records":
                refreshRecordsTable();
                adminCards.show(adminContentCards, "records");
                break;
            case "Cost Calculator":
                adminCards.show(adminContentCards, "calculator");
                break;
            case "Add Slot":
                adminCards.show(adminContentCards, "addslot");
                break;
            case "Fill Test Booking":
                // create a dummy booking to test
                if (!slots.isEmpty()) {
                    ParkingSlot s = slots.get(0);
                    if (s.available) {
                        s.available = false;
                        s.bookedBy = "test";
                        s.vehicle = "TEST-123";
                        s.inTime = LocalDateTime.now().minusHours(3).minusMinutes(15);
                        JOptionPane.showMessageDialog(this, "Filled slot 1 with a test booking.");
                    } else JOptionPane.showMessageDialog(this, "Slot 1 already occupied.");
                }
                refreshAdminAvailabilityPanel();
                break;
            case "Clear All Bookings":
                for (ParkingSlot s : slots) {
                    s.available = true; s.bookedBy = null; s.vehicle = null; s.inTime = null;
                }
                bookings.clear();
                refreshAdminAvailabilityPanel();
                refreshRecordsTable();
                JOptionPane.showMessageDialog(this, "Cleared all bookings.");
                break;
            case "Logout":
                currentUser = null; currentVehicle = null;
                mainCards.show(mainPanel, "login");
                break;
        }
    }

    private void refreshAdminPanels() {
        refreshAdminAvailabilityPanel();
        refreshRecordsTable();
    }

    private void refreshAdminAvailabilityPanel() {
        availabilityListPanel.removeAll();
        for (ParkingSlot s : slots) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(18, 20, 18, 20)
            ));
            card.setMaximumSize(new Dimension(900, 90));
            
            // Left side with slot info
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setOpaque(false);
            
            JPanel slotHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            slotHeader.setOpaque(false);
            JLabel slotNum = new JLabel("Slot " + s.id);
            slotNum.setFont(new Font("Segoe UI", Font.BOLD, 16));
            slotNum.setForeground(TEXT_PRIMARY);
            slotHeader.add(slotNum);
            
            JLabel status = new JLabel(s.available ? "● Available" : "● Occupied");
            status.setFont(new Font("Segoe UI", Font.BOLD, 13));
            status.setForeground(s.available ? SECONDARY_COLOR : DANGER_COLOR);
            slotHeader.add(status);
            
            leftPanel.add(slotHeader);
            
            if (!s.available) {
                JLabel details = new JLabel("User: " + s.bookedBy + " | Vehicle: " + s.vehicle + " | Since: " + DF.format(s.inTime));
                details.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                details.setForeground(TEXT_SECONDARY);
                leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                leftPanel.add(details);
            }
            
            card.add(leftPanel, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.setOpaque(false);
            JButton toggle = new ModernButton(s.available ? "Book" : "Free Slot", s.available ? PRIMARY_COLOR : DANGER_COLOR);
            toggle.setPreferredSize(new Dimension(120, 38));
            actions.add(toggle);

            toggle.addActionListener(e -> {
                if (s.available) {
                    // booking via admin: ask for user & vehicle
                    JTextField userF = new JTextField();
                    JTextField vehF = new JTextField();
                    Object[] msg = {"User:", userF, "Vehicle:", vehF};
                    int res = JOptionPane.showConfirmDialog(this, msg, "Book Slot " + s.id, JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.OK_OPTION && !userF.getText().trim().isEmpty() && !vehF.getText().trim().isEmpty()) {
                        s.available = false;
                        s.bookedBy = userF.getText().trim();
                        s.vehicle = vehF.getText().trim();
                        s.inTime = LocalDateTime.now();
                        refreshAdminAvailabilityPanel();
                    }
                } else {
                    // free slot -> record booking with outTime now, compute cost
                    LocalDateTime out = LocalDateTime.now();
                    double cost = calculateCost(s.inTime, out);
                    bookings.add(new Booking(s.id, s.bookedBy, s.vehicle, s.inTime, out, cost));
                    s.available = true;
                    s.bookedBy = null; s.vehicle = null; s.inTime = null;
                    refreshAdminAvailabilityPanel();
                    refreshRecordsTable();
                    JOptionPane.showMessageDialog(this, "Freed slot " + s.id + ". Booking recorded. Cost ₹" + cost);
                }
            });

            card.add(actions, BorderLayout.EAST);
            availabilityListPanel.add(card);
            availabilityListPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }
        availabilityListPanel.revalidate();
        availabilityListPanel.repaint();
    }

    private JPanel createAdminCostCalculatorPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("$ Cost Calculator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        card.add(title, gbc);
        
        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 15)), gbc);

        gbc.gridwidth=1;
        gbc.gridy++;
        JLabel slotLabel = new JLabel("Select slot:");
        slotLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        slotLabel.setForeground(TEXT_PRIMARY);
        card.add(slotLabel, gbc);
        JComboBox<String> slotCombo = new JComboBox<>();
        slotCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        slotCombo.setPreferredSize(new Dimension(300, 38));
        gbc.gridx=1;
        card.add(slotCombo, gbc);

        gbc.gridx=0; gbc.gridy++;
        JLabel timeLabel = new JLabel("In Time (if occupied):");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        timeLabel.setForeground(TEXT_PRIMARY);
        card.add(timeLabel, gbc);
        JLabel inTimeLabel = new JLabel("-");
        inTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inTimeLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx=1;
        card.add(inTimeLabel, gbc);

        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2;
        card.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        
        gbc.gridy++;
        JButton calcNow = new ModernButton("Calculate Cost", PRIMARY_COLOR);
        calcNow.setPreferredSize(new Dimension(300, 42));
        card.add(calcNow, gbc);

        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        
        gbc.gridy++;
        JLabel result = new JLabel("Cost: ₹0.00");
        result.setFont(new Font("Segoe UI", Font.BOLD, 20));
        result.setForeground(PRIMARY_COLOR);
        card.add(result, gbc);

        // fill combo
        slotCombo.removeAllItems();
        for (ParkingSlot s : slots) slotCombo.addItem("Slot " + s.id);

        slotCombo.addActionListener(e -> {
            int id = slotCombo.getSelectedIndex();
            ParkingSlot s = slots.get(id);
            if (!s.available) {
                inTimeLabel.setText(DF.format(s.inTime));
            } else inTimeLabel.setText("n/a");
            result.setText("Cost: ₹0.00");
        });

        calcNow.addActionListener(e -> {
            int id = slotCombo.getSelectedIndex();
            ParkingSlot s = slots.get(id);
            if (s.available) {
                JOptionPane.showMessageDialog(this, "Slot is available (no active in-time).");
                result.setText("Cost: ₹0.00");
            } else {
                double cost = calculateCost(s.inTime, LocalDateTime.now());
                result.setText(String.format("Cost: ₹%.2f (from %s to now)", cost, DF.format(s.inTime)));
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createAddSlotPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets=new Insets(12,12,12,12);
        gbc.fill=GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("+ Add Parking Slots");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        card.add(title, gbc);
        
        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 15)), gbc);

        gbc.gridwidth=1;
        gbc.gridy++;
        JLabel numLabel = new JLabel("Number of slots to add:");
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        numLabel.setForeground(TEXT_PRIMARY);
        card.add(numLabel, gbc);
        JTextField numField = new JTextField("1");
        numField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        numField.setPreferredSize(new Dimension(200, 38));
        numField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridx=1;
        card.add(numField, gbc);

        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2;
        card.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        
        gbc.gridy++;
        JButton addBtn = new ModernButton("Add Slots", SECONDARY_COLOR);
        addBtn.setPreferredSize(new Dimension(200, 42));
        card.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(numField.getText().trim());
                if (n <= 0) throw new NumberFormatException();
                int start = slots.size() + 1;
                for (int i=0;i<n;i++) slots.add(new ParkingSlot(start+i));
                JOptionPane.showMessageDialog(this, "Added " + n + " slots.");
                refreshAdminAvailabilityPanel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid positive integer.");
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private void refreshRecordsTable() {
        recordsTableModel.setRowCount(0);
        for (Booking b : bookings) {
            recordsTableModel.addRow(new Object[]{b.slotId, b.user, b.vehicle, DF.format(b.inTime), DF.format(b.outTime), "₹" + String.format("%.2f", b.cost)});
        }
    }

    // ---------------- USER DASHBOARD ----------------
    private JPanel createUserDashboardPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(BG_COLOR);

        // header
        GradientPanel header = new GradientPanel(PRIMARY_COLOR, PRIMARY_DARK);
        header.setPreferredSize(new Dimension(1200,90));
        header.setLayout(new BorderLayout());
        
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 25));
        headerLeft.setOpaque(false);
        
        JLabel icon = new JLabel("◉");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        icon.setForeground(Color.WHITE);
        headerLeft.add(icon);
        
        JLabel title = new JLabel("User Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerLeft.add(title);
        
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        headerRight.setOpaque(false);
        
        JButton logout = new ModernButton("← Logout", DANGER_COLOR);
        logout.addActionListener(e -> {
            currentUser = null; currentVehicle = null;
            mainCards.show(mainPanel, "login");
        });
        headerRight.add(logout);
        
        header.add(headerLeft, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);
        userPanel.add(header, BorderLayout.NORTH);

        // center content
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_COLOR);
        center.setBorder(new EmptyBorder(25,25,25,25));

        // left: slot list + book
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(CARD_BG);
        left.setPreferredSize(new Dimension(480, 550));
        left.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel leftTitle = new JLabel("▣ Available Slots");
        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leftTitle.setForeground(TEXT_PRIMARY);
        left.add(leftTitle, BorderLayout.NORTH);
        
        userSlotListModel = new DefaultListModel<>();
        JList<String> slotList = new JList<>(userSlotListModel);
        slotList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotList.setFixedCellHeight(40);
        slotList.setSelectionBackground(new Color(224, 231, 255));
        slotList.setSelectionForeground(TEXT_PRIMARY);
        slotList.setBorder(new EmptyBorder(10, 0, 10, 0));
        left.add(new JScrollPane(slotList), BorderLayout.CENTER);

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        leftBtns.setOpaque(false);
        JButton bookBtn = new ModernButton("Book Selected", PRIMARY_COLOR);
        bookBtn.setPreferredSize(new Dimension(180, 42));
        JButton leaveBtn = new ModernButton("Leave My Slot", DANGER_COLOR);
        leaveBtn.setPreferredSize(new Dimension(180, 42));
        leftBtns.add(bookBtn); leftBtns.add(leaveBtn);
        left.add(leftBtns, BorderLayout.SOUTH);

        // right: booking info & cost
        JPanel right = new JPanel();
        right.setBackground(CARD_BG);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        right.setPreferredSize(new Dimension(550, 550));
        
        JLabel rightTitle = new JLabel("≡ My Booking");
        rightTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightTitle.setForeground(TEXT_PRIMARY);
        rightTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(rightTitle);
        right.add(Box.createRigidArea(new Dimension(0,20)));
        
        userBookingInfo = new JLabel("No active booking", SwingConstants.LEFT);
        userBookingInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userBookingInfo.setForeground(TEXT_SECONDARY);
        userBookingInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(userBookingInfo);
        right.add(Box.createRigidArea(new Dimension(0,20)));

        JButton calcCostBtn = new ModernButton("$ Calculate Cost (now)", WARNING_COLOR);
        calcCostBtn.setPreferredSize(new Dimension(220, 42));
        calcCostBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(calcCostBtn);

        center.add(left, BorderLayout.WEST);
        center.add(right, BorderLayout.CENTER);
        userPanel.add(center, BorderLayout.CENTER);

        // actions
        bookBtn.addActionListener(e -> {
            int sel = slotList.getSelectedIndex();
            if (sel == -1) { JOptionPane.showMessageDialog(this, "Select a slot to book."); return; }
            int slotId = Integer.parseInt(userSlotListModel.getElementAt(sel).split(" ")[1]);
            ParkingSlot s = slots.get(slotId - 1);
            if (!s.available) { JOptionPane.showMessageDialog(this, "Slot not available."); refreshUserPanel(); return; }
            s.available = false; s.bookedBy = currentUser; s.vehicle = currentVehicle; s.inTime = LocalDateTime.now();
            JOptionPane.showMessageDialog(this, "Booked slot " + s.id + " at " + DF.format(s.inTime));
            refreshUserPanel();
        });

        leaveBtn.addActionListener(e -> {
            // find user's current slot
            ParkingSlot my = null;
            for (ParkingSlot s : slots) if (!s.available && currentUser.equals(s.bookedBy) && currentVehicle.equals(s.vehicle)) { my = s; break; }
            if (my == null) { JOptionPane.showMessageDialog(this, "You have no active booking."); return; }
            LocalDateTime out = LocalDateTime.now();
            double cost = calculateCost(my.inTime, out);
            bookings.add(new Booking(my.id, my.bookedBy, my.vehicle, my.inTime, out, cost));
            my.available = true; my.bookedBy = null; my.vehicle = null; my.inTime = null;
            JOptionPane.showMessageDialog(this, String.format("Left slot %d. Total cost: ₹%.2f", my.id, cost));
            refreshUserPanel();
        });

        calcCostBtn.addActionListener(e -> {
            ParkingSlot my = null;
            for (ParkingSlot s : slots) if (!s.available && currentUser.equals(s.bookedBy) && currentVehicle.equals(s.vehicle)) { my = s; break; }
            if (my == null) { JOptionPane.showMessageDialog(this, "You have no active booking."); return; }
            double cost = calculateCost(my.inTime, LocalDateTime.now());
            JOptionPane.showMessageDialog(this, String.format("Cost so far: ₹%.2f (since %s)", cost, DF.format(my.inTime)));
        });

        return userPanel;
    }

    private void refreshUserPanel() {
        // refresh available list
        userSlotListModel.clear();
        for (ParkingSlot s : slots) if (s.available) userSlotListModel.addElement("Slot " + s.id);
        // show user's booking if any
        ParkingSlot my = null;
        if (currentUser != null) {
            for (ParkingSlot s : slots) {
                if (!s.available && currentUser.equals(s.bookedBy) && currentVehicle.equals(s.vehicle)) { my = s; break; }
            }
        }
        if (my == null) {
            userBookingInfo.setText("<html><div style='padding:15px; background:#f3f4f6; border-radius:8px;'><b style='color:#111827;'>No active booking</b><br/><span style='color:#6b7280;'>You can book any available slot from the left panel.</span></div></html>");
        } else {
            userBookingInfo.setText(String.format("<html><b>Slot %d</b><br/>Vehicle: %s<br/>Booked at: %s</html>", my.id, my.vehicle, DF.format(my.inTime)));
        }
        // also refresh admin panels (records) so both views consistent
        refreshAdminAvailabilityPanel();
        refreshRecordsTable();
    }

    // ---------------- UTIL ----------------
    private static double calculateCost(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null) return 0;
        Duration d = Duration.between(in, out);
        double hours = d.toMinutes() / 60.0;
        return Math.ceil(hours) * RATE_PER_HOUR;
    }

    // ---------------- STYLING HELPERS ----------------
    static class ModernButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;
        
        ModernButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.hoverColor = color.brighter();
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(150, 40));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color bgColor = isHovered ? hoverColor : baseColor;
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            
            super.paintComponent(g);
            g2.dispose();
        }
    }
    
    static class SidebarButton extends JButton {
        private boolean isHovered = false;
        
        SidebarButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(new Color(55, 65, 81));
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 16, 12, 16));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isHovered) {
                g2.setColor(new Color(243, 244, 246));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class GradientPanel extends JPanel {
        private final Color c1, c2;
        GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0,0,c1,w,h,c2);
            g2.setPaint(gp);
            g2.fillRect(0,0,w,h);
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ParkingSystemUI app = new ParkingSystemUI();
            app.setVisible(true);
        });
    }
}
