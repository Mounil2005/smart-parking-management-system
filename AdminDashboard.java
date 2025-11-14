import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboard extends JPanel {
    
    private JFrame parent;
    private CardLayout mainCards;
    private JPanel mainPanel;
    
    private CardLayout adminCards;
    private JPanel adminContentCards;
    private DefaultTableModel recordsTableModel;
    private JPanel availabilityListPanel;
    
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public AdminDashboard(JFrame parent, CardLayout mainCards, JPanel mainPanel) {
        this.parent = parent;
        this.mainCards = mainCards;
        this.mainPanel = mainPanel;
        
        setLayout(new BorderLayout());
        setBackground(UIComponents.BG_COLOR);
        initializeComponents();
    }
    
    private void initializeComponents() {
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);
        
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        adminCards = new CardLayout();
        adminContentCards = new JPanel(adminCards);
        adminContentCards.setBackground(UIComponents.BG_COLOR);
        
        setupContentPanels();
        add(adminContentCards, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        UIComponents.GradientPanel header = new UIComponents.GradientPanel(UIComponents.PRIMARY_COLOR, UIComponents.PRIMARY_DARK);
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
        return header;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 660));
        sidebar.setBackground(UIComponents.CARD_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)),
            new EmptyBorder(25, 20, 25, 20)
        ));
        
        String[] buttons = {
            "▣ Parking Availability", 
            "≡ User Records", 
            "$ Cost Calculator", 
            "+ Add Slot", 
            "✕ Clear All Bookings", 
            "← Logout"
        };
        
        for (String buttonText : buttons) {
            JButton button = new UIComponents.SidebarButton(buttonText);
            button.setMaximumSize(new Dimension(220, 48));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(button);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            
            String action = buttonText.replaceAll("[▣≡$+✕←] ", "");
            button.addActionListener(e -> handleSidebarAction(action));
        }
        
        return sidebar;
    }
    
    private void setupContentPanels() {
        availabilityListPanel = new JPanel();
        availabilityListPanel.setBackground(UIComponents.BG_COLOR);
        availabilityListPanel.setLayout(new BoxLayout(availabilityListPanel, BoxLayout.Y_AXIS));
        availabilityListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JScrollPane availScroll = new JScrollPane(availabilityListPanel);
        availScroll.setBorder(null);
        availScroll.getViewport().setBackground(UIComponents.BG_COLOR);
        adminContentCards.add(availScroll, "availability");
        
        recordsTableModel = new DefaultTableModel(
            new Object[]{"Slot", "User", "Vehicle", "In Time", "Out Time", "Cost"}, 0);
        JTable recordsTable = new JTable(recordsTableModel);
        recordsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recordsTable.setRowHeight(32);
        recordsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        recordsTable.getTableHeader().setBackground(new Color(243, 244, 246));
        recordsTable.getTableHeader().setForeground(UIComponents.TEXT_PRIMARY);
        recordsTable.setGridColor(new Color(229, 231, 235));
        recordsTable.setSelectionBackground(new Color(224, 231, 255));
        JScrollPane tableScroll = new JScrollPane(recordsTable);
        tableScroll.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIComponents.BG_COLOR);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        adminContentCards.add(tablePanel, "records");
        
        adminContentCards.add(createCostCalculatorPanel(), "calculator");
        
        adminContentCards.add(createAddSlotPanel(), "addslot");
    }
    
    private void handleSidebarAction(String action) {
        switch (action) {
            case "Parking Availability":
                refreshAvailabilityPanel();
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
            case "Clear All Bookings":
                DataModels.clearAllBookings();
                refreshAvailabilityPanel();
                refreshRecordsTable();
                JOptionPane.showMessageDialog(parent, "Cleared all bookings.");
                break;
            case "Logout":
                DataModels.clearCurrentUser();
                mainCards.show(mainPanel, "login");
                break;
        }
    }
    
    public void refreshAvailabilityPanel() {
        availabilityListPanel.removeAll();
        
        for (DataModels.ParkingSlot slot : DataModels.getSlots()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(UIComponents.CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(18, 20, 18, 20)
            ));
            card.setMaximumSize(new Dimension(900, 90));
            
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setOpaque(false);
            
            JPanel slotHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            slotHeader.setOpaque(false);
            JLabel slotNum = new JLabel("Slot " + slot.id);
            slotNum.setFont(new Font("Segoe UI", Font.BOLD, 16));
            slotNum.setForeground(UIComponents.TEXT_PRIMARY);
            slotHeader.add(slotNum);
            
            JLabel status = new JLabel(slot.available ? "● Available" : "● Occupied");
            status.setFont(new Font("Segoe UI", Font.BOLD, 13));
            status.setForeground(slot.available ? UIComponents.SECONDARY_COLOR : UIComponents.DANGER_COLOR);
            slotHeader.add(status);
            
            leftPanel.add(slotHeader);
            
            if (!slot.available) {
                JLabel details = new JLabel("User: " + slot.bookedBy + 
                    " | Vehicle: " + slot.vehicle + " | Since: " + DF.format(slot.inTime));
                details.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                details.setForeground(UIComponents.TEXT_SECONDARY);
                leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                leftPanel.add(details);
            }
            
            card.add(leftPanel, BorderLayout.CENTER);
            
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.setOpaque(false);
            JButton toggle = new UIComponents.ModernButton(slot.available ? "Book" : "Free Slot", 
                slot.available ? UIComponents.PRIMARY_COLOR : UIComponents.DANGER_COLOR);
            toggle.setPreferredSize(new Dimension(120, 38));
            actions.add(toggle);
            
            toggle.addActionListener(e -> handleSlotAction(slot));
            
            card.add(actions, BorderLayout.EAST);
            availabilityListPanel.add(card);
            availabilityListPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }
        
        availabilityListPanel.revalidate();
        availabilityListPanel.repaint();
    }
    
    private void handleSlotAction(DataModels.ParkingSlot slot) {
        if (slot.available) {
            JTextField userField = new JTextField();
            JTextField vehicleField = new JTextField();
            Object[] message = {"User:", userField, "Vehicle:", vehicleField};
            int result = JOptionPane.showConfirmDialog(parent, message, 
                "Book Slot " + slot.id, JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION && 
                !userField.getText().trim().isEmpty() && 
                !vehicleField.getText().trim().isEmpty()) {
                slot.available = false;
                slot.bookedBy = userField.getText().trim();
                slot.vehicle = vehicleField.getText().trim();
                slot.inTime = LocalDateTime.now();
                DataModels.updateSlot(slot);
                refreshAvailabilityPanel();
                JOptionPane.showMessageDialog(parent, 
                    "Booked slot " + slot.id + " for user " + slot.bookedBy + ".");
            }
        } else {
            LocalDateTime outTime = LocalDateTime.now();
            double cost = calculateCost(slot.inTime, outTime);
            DataModels.addBooking(new DataModels.Booking(slot.id, slot.bookedBy, 
                slot.vehicle, slot.inTime, outTime, cost));
            slot.available = true;
            slot.bookedBy = null;
            slot.vehicle = null;
            slot.inTime = null;
            DataModels.updateSlot(slot);
            refreshAvailabilityPanel();
            refreshRecordsTable();
            JOptionPane.showMessageDialog(parent, 
                "Freed slot " + slot.id + ". Booking recorded. Cost ₹" + cost);
        }
    }
    
    private JPanel createCostCalculatorPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIComponents.BG_COLOR);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIComponents.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("$ Cost Calculator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIComponents.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);
        
        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel slotLabel = new JLabel("Select slot:");
        slotLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        slotLabel.setForeground(UIComponents.TEXT_PRIMARY);
        card.add(slotLabel, gbc);
        
        JComboBox<String> slotCombo = new JComboBox<>();
        slotCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        slotCombo.setPreferredSize(new Dimension(300, 38));
        gbc.gridx = 1;
        card.add(slotCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        JLabel timeLabel = new JLabel("In Time (if occupied):");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        timeLabel.setForeground(UIComponents.TEXT_PRIMARY);
        card.add(timeLabel, gbc);
        
        JLabel inTimeLabel = new JLabel("-");
        inTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inTimeLabel.setForeground(UIComponents.TEXT_SECONDARY);
        gbc.gridx = 1;
        card.add(inTimeLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        card.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        
        gbc.gridy++;
        JButton calcNow = new UIComponents.ModernButton("Calculate Cost", UIComponents.PRIMARY_COLOR);
        calcNow.setPreferredSize(new Dimension(300, 42));
        card.add(calcNow, gbc);
        
        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        
        gbc.gridy++;
        JLabel result = new JLabel("Cost: ₹0.00");
        result.setFont(new Font("Segoe UI", Font.BOLD, 20));
        result.setForeground(UIComponents.PRIMARY_COLOR);
        card.add(result, gbc);
        
        slotCombo.removeAllItems();
        for (DataModels.ParkingSlot s : DataModels.getSlots()) {
            slotCombo.addItem("Slot " + s.id);
        }
        
        slotCombo.addActionListener(e -> {
            int id = slotCombo.getSelectedIndex();
            if (id >= 0 && id < DataModels.getSlots().size()) {
                DataModels.ParkingSlot s = DataModels.getSlots().get(id);
                if (!s.available) {
                    inTimeLabel.setText(DF.format(s.inTime));
                } else {
                    inTimeLabel.setText("N/A");
                }
                result.setText("Cost: ₹0.00");
            }
        });
        
        calcNow.addActionListener(e -> {
            int id = slotCombo.getSelectedIndex();
            if (id >= 0 && id < DataModels.getSlots().size()) {
                DataModels.ParkingSlot s = DataModels.getSlots().get(id);
                if (s.available) {
                    JOptionPane.showMessageDialog(parent, 
                        "Slot is available (no active in-time).");
                    result.setText("Cost: ₹0.00");
                } else {
                    double cost = calculateCost(s.inTime, LocalDateTime.now());
                    result.setText(String.format("Cost: ₹%.2f (from %s to now)", 
                        cost, DF.format(s.inTime)));
                }
            }
        });
        
        wrapper.add(card);
        return wrapper;
    }
    
    private JPanel createAddSlotPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIComponents.BG_COLOR);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIComponents.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("+ Add Parking Slots");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIComponents.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);
        
        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel numLabel = new JLabel("Number of slots to add:");
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        numLabel.setForeground(UIComponents.TEXT_PRIMARY);
        card.add(numLabel, gbc);
        
        JTextField numField = new JTextField("1");
        numField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        numField.setPreferredSize(new Dimension(200, 38));
        numField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridx = 1;
        card.add(numField, gbc);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        card.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        
        gbc.gridy++;
        JButton addBtn = new UIComponents.ModernButton("Add Slots", UIComponents.SECONDARY_COLOR);
        addBtn.setPreferredSize(new Dimension(200, 42));
        card.add(addBtn, gbc);
        
        addBtn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(numField.getText().trim());
                if (n <= 0) throw new NumberFormatException();
                int start = DataModels.getSlots().size() + 1;
                for (int i = 0; i < n; i++) {
                    DataModels.addSlot(start + i);
                }
                JOptionPane.showMessageDialog(parent, "Added " + n + " slots.");
                refreshAvailabilityPanel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, 
                    "Enter a valid positive integer.");
            }
        });
        
        wrapper.add(card);
        return wrapper;
    }
    
    public void refreshRecordsTable() {
        recordsTableModel.setRowCount(0);
        for (DataModels.Booking booking : DataModels.getBookings()) {
            recordsTableModel.addRow(new Object[]{
                booking.slotId, 
                booking.user, 
                booking.vehicle, 
                DF.format(booking.inTime), 
                DF.format(booking.outTime), 
                "₹" + String.format("%.2f", booking.cost)
            });
        }
    }
    
    private double calculateCost(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null) return 0;
        Duration duration = Duration.between(in, out);
        double hours = duration.toMinutes() / 60.0;
        return Math.ceil(hours) * UIComponents.RATE_PER_HOUR;
    }
    
    public void refreshPanels() {
        refreshAvailabilityPanel();
        refreshRecordsTable();
    }
}
