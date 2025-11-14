import javax.swing.*;
import java.awt.*;

public class ParkingSystemMain extends JFrame {
    
    private CardLayout mainCardLayout;
    private JPanel mainPanel;
    
    private LoginPanel loginPanel;
    private AdminDashboard adminDashboard;
    private UserDashboard userDashboard;
    
    public ParkingSystemMain() {
        setTitle("Smart Parking System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        DataModels.initializeSlots(8);
        
        mainCardLayout = new CardLayout();
        mainPanel = new JPanel(mainCardLayout);
        
        createPanels();
        
        setupCallbacks();
        
        add(mainPanel);
        
        mainCardLayout.show(mainPanel, "login");
    }
    
    private void createPanels() {
        loginPanel = new LoginPanel(this, mainCardLayout, mainPanel);
        mainPanel.add(loginPanel, "login");
        
        adminDashboard = new AdminDashboard(this, mainCardLayout, mainPanel);
        mainPanel.add(adminDashboard, "admin");
        
        userDashboard = new UserDashboard(this, mainCardLayout, mainPanel);
        mainPanel.add(userDashboard, "user");
    }
    
    private void setupCallbacks() {
        loginPanel.setOnAdminLogin(e -> {
            adminDashboard.refreshPanels();
        });
        
        loginPanel.setOnUserLogin(e -> {
            userDashboard.refreshPanel();
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ParkingSystemMain app = new ParkingSystemMain();
            app.setVisible(true);
        });
    }
}
