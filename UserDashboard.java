import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDashboard extends JPanel {
    
    private JFrame parent;
    private CardLayout mainCards;
    private JPanel mainPanel;
    
    private DefaultListModel<String> slotListModel;
    private JLabel bookingInfo;
    private JList<String> slotList;
    
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public UserDashboard(JFrame parent, CardLayout mainCards, JPanel mainPanel) {
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
        
        JPanel center = createContentArea();
        add(center, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        UIComponents.GradientPanel header = new UIComponents.GradientPanel(UIComponents.PRIMARY_COLOR, UIComponents.PRIMARY_DARK);
        header.setPreferredSize(new Dimension(1200, 90));
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
        
        JButton logout = new UIComponents.ModernButton("← Logout", UIComponents.DANGER_COLOR);
        logout.addActionListener(e -> {
            DataModels.clearCurrentUser();
            mainCards.show(mainPanel, "login");
        });
        headerRight.add(logout);
        
        header.add(headerLeft, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createContentArea() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIComponents.BG_COLOR);
        center.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel leftPanel = createAvailableSlotsPanel();
        center.add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = createBookingInfoPanel();
        center.add(rightPanel, BorderLayout.CENTER);
        
        return center;
    }
    
    private JPanel createAvailableSlotsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIComponents.CARD_BG);
        panel.setPreferredSize(new Dimension(480, 550));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel title = new JLabel("▣ Available Slots");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIComponents.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);
        
        slotListModel = new DefaultListModel<>();
        slotList = new JList<>(slotListModel);
        slotList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotList.setFixedCellHeight(40);
        slotList.setSelectionBackground(new Color(224, 231, 255));
        slotList.setSelectionForeground(UIComponents.TEXT_PRIMARY);
        slotList.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JScrollPane scrollPane = new JScrollPane(slotList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        JButton bookBtn = new UIComponents.ModernButton("Book Selected", UIComponents.PRIMARY_COLOR);
        bookBtn.setPreferredSize(new Dimension(180, 42));
        bookBtn.addActionListener(e -> bookSelectedSlot());
        
        JButton leaveBtn = new UIComponents.ModernButton("Leave My Slot", UIComponents.DANGER_COLOR);
        leaveBtn.setPreferredSize(new Dimension(180, 42));
        leaveBtn.addActionListener(e -> leaveSlot());
        
        buttonPanel.add(bookBtn);
        buttonPanel.add(leaveBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBookingInfoPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIComponents.CARD_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(550, 550));
        
        JLabel title = new JLabel("≡ My Booking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIComponents.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        bookingInfo = new JLabel("No active booking", SwingConstants.LEFT);
        bookingInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingInfo.setForeground(UIComponents.TEXT_SECONDARY);
        bookingInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(bookingInfo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton calcCostBtn = new UIComponents.ModernButton("$ Calculate Cost (now)", UIComponents.WARNING_COLOR);
        calcCostBtn.setPreferredSize(new Dimension(220, 42));
        calcCostBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcCostBtn.addActionListener(e -> calculateCurrentCost());
        panel.add(calcCostBtn);
        
        return panel;
    }
    
    private void bookSelectedSlot() {
        int selectedIndex = slotList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a slot to book.");
            return;
        }
        
        String selectedItem = slotListModel.getElementAt(selectedIndex);
        int slotId = Integer.parseInt(selectedItem.split(" ")[1]);
        DataModels.ParkingSlot slot = DataModels.getSlots().get(slotId - 1);
        
        if (!slot.available) {
            JOptionPane.showMessageDialog(parent, "Slot is no longer available.");
            refreshPanel();
            return;
        }
        
        slot.available = false;
        slot.bookedBy = DataModels.getCurrentUser();
        slot.vehicle = DataModels.getCurrentVehicle();
        slot.inTime = LocalDateTime.now();
        DataModels.updateSlot(slot);
        
        JOptionPane.showMessageDialog(parent, 
            "Booked slot " + slot.id + " at " + DF.format(slot.inTime));
        refreshPanel();
    }
    
    private void leaveSlot() {
        DataModels.ParkingSlot userSlot = DataModels.findUserBooking(
            DataModels.getCurrentUser(), 
            DataModels.getCurrentVehicle());
        
        if (userSlot == null) {
            JOptionPane.showMessageDialog(parent, "You have no active booking.");
            return;
        }
        
        LocalDateTime outTime = LocalDateTime.now();
        double cost = calculateCost(userSlot.inTime, outTime);
        
        DataModels.addBooking(new DataModels.Booking(
            userSlot.id, 
            userSlot.bookedBy, 
            userSlot.vehicle, 
            userSlot.inTime, 
            outTime, 
            cost));
        
        userSlot.available = true;
        userSlot.bookedBy = null;
        userSlot.vehicle = null;
        userSlot.inTime = null;
        DataModels.updateSlot(userSlot);
        
        JOptionPane.showMessageDialog(parent, 
            String.format("Left slot %d. Total cost: ₹%.2f", userSlot.id, cost));
        refreshPanel();
    }
    
    private void calculateCurrentCost() {
        DataModels.ParkingSlot userSlot = DataModels.findUserBooking(
            DataModels.getCurrentUser(), 
            DataModels.getCurrentVehicle());
        
        if (userSlot == null) {
            JOptionPane.showMessageDialog(parent, "You have no active booking.");
            return;
        }
        
        double cost = calculateCost(userSlot.inTime, LocalDateTime.now());
        JOptionPane.showMessageDialog(parent, 
            String.format("Cost so far: ₹%.2f (since %s)", 
                cost, DF.format(userSlot.inTime)));
    }
    
    private double calculateCost(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null) return 0;
        Duration duration = Duration.between(in, out);
        double hours = duration.toMinutes() / 60.0;
        return Math.ceil(hours) * UIComponents.RATE_PER_HOUR;
    }
    
    public void refreshPanel() {
        slotListModel.clear();
        for (DataModels.ParkingSlot slot : DataModels.getSlots()) {
            if (slot.available) {
                slotListModel.addElement("Slot " + slot.id);
            }
        }
        
        DataModels.ParkingSlot userSlot = DataModels.findUserBooking(
            DataModels.getCurrentUser(), 
            DataModels.getCurrentVehicle());
        
        if (userSlot == null) {
            bookingInfo.setText(
                "<html><div style='padding:15px; background:#f3f4f6; border-radius:8px;'>" +
                "<b style='color:#111827;'>No active booking</b><br/>" +
                "<span style='color:#6b7280;'>You can book any available slot from the left panel.</span>" +
                "</div></html>");
        } else {
            bookingInfo.setText(String.format(
                "<html><b>Slot %d</b><br/>Vehicle: %s<br/>Booked at: %s</html>", 
                userSlot.id, userSlot.vehicle, DF.format(userSlot.inTime)));
        }
    }
}
