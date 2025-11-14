import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    
    private JFrame parent;
    private CardLayout mainCards;
    private JPanel mainPanel;
    private ActionListener onAdminLogin;
    private ActionListener onUserLogin;
    
    public LoginPanel(JFrame parent, CardLayout mainCards, JPanel mainPanel) {
        this.parent = parent;
        this.mainCards = mainCards;
        this.mainPanel = mainPanel;
        
        setLayout(new BorderLayout());
        setBackground(UIComponents.BG_COLOR);
        initializeComponents();
    }
    
    private void initializeComponents() {
        UIComponents.GradientPanel leftBanner = createLeftBanner();
        
        JPanel rightPanel = createRightPanel();
        
        add(leftBanner, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private UIComponents.GradientPanel createLeftBanner() {
        UIComponents.GradientPanel left = new UIComponents.GradientPanel(UIComponents.PRIMARY_COLOR, UIComponents.PRIMARY_DARK);
        left.setPreferredSize(new Dimension(450, 750));
        left.setLayout(new GridBagLayout());
        
        JPanel bannerContent = new JPanel();
        bannerContent.setLayout(new BoxLayout(bannerContent, BoxLayout.Y_AXIS));
        bannerContent.setOpaque(false);
        
        JLabel icon = new JLabel("P", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 80));
        icon.setForeground(Color.WHITE);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel title = new JLabel("SMART PARKING", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Effortless Parking Management", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(224, 231, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        bannerContent.add(icon);
        bannerContent.add(Box.createRigidArea(new Dimension(0, 20)));
        bannerContent.add(title);
        bannerContent.add(Box.createRigidArea(new Dimension(0, 10)));
        bannerContent.add(subtitle);
        
        left.add(bannerContent);
        return left;
    }
    
    private JPanel createRightPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UIComponents.BG_COLOR);
        
        JPanel card = createLoginCard();
        
        GridBagConstraints rightGbc = new GridBagConstraints();
        right.add(card, rightGbc);
        
        return right;
    }
    
    private JPanel createLoginCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIComponents.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel header = new JLabel("Welcome Back", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(UIComponents.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(header, gbc);
        
        JLabel subheader = new JLabel("Sign in to continue", SwingConstants.LEFT);
        subheader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subheader.setForeground(UIComponents.TEXT_SECONDARY);
        gbc.gridy++;
        card.add(subheader, gbc);
        
        gbc.gridy++;
        card.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel roleLabel = createLabel("Role:");
        card.add(roleLabel, gbc);
        
        JComboBox<String> roleSelect = new JComboBox<>(new String[]{"User", "Admin"});
        roleSelect.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleSelect.setPreferredSize(new Dimension(250, 38));
        gbc.gridx = 1;
        card.add(roleSelect, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        JLabel userLabel = createLabel("Username:");
        card.add(userLabel, gbc);
        
        JTextField usernameField = UIComponents.createStyledTextField();
        gbc.gridx = 1;
        card.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        JLabel secondLabel = createLabel("Vehicle No:");
        card.add(secondLabel, gbc);
        
        JTextField vehicleOrPass = UIComponents.createStyledTextField();
        gbc.gridx = 1;
        card.add(vehicleOrPass, gbc);
        
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
        JButton loginBtn = new UIComponents.ModernButton("Sign In", UIComponents.PRIMARY_COLOR);
        loginBtn.setPreferredSize(new Dimension(250, 44));
        card.add(loginBtn, gbc);
        
        gbc.gridy++;
        JButton demoUser = new UIComponents.ModernButton("Quick Demo User", UIComponents.SECONDARY_COLOR);
        demoUser.setPreferredSize(new Dimension(250, 44));
        card.add(demoUser, gbc);
        
        loginBtn.addActionListener(e -> {
            String role = (String) roleSelect.getSelectedItem();
            String user = usernameField.getText().trim();
            String second = vehicleOrPass.getText().trim();
            
            if (role.equals("Admin")) {
                if (user.equals("admin") && second.equals("1234")) {
                    if (onAdminLogin != null) {
                        onAdminLogin.actionPerformed(null);
                    }
                    mainCards.show(mainPanel, "admin");
                } else {
                    JOptionPane.showMessageDialog(parent, 
                        "Invalid admin credentials (use admin / 1234).");
                }
            } else {
                if (user.isEmpty() || second.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, 
                        "Enter username and vehicle number.");
                    return;
                }
                DataModels.setCurrentUser(user, second);
                if (onUserLogin != null) {
                    onUserLogin.actionPerformed(null);
                }
                mainCards.show(mainPanel, "user");
            }
        });
        
        demoUser.addActionListener(e -> {
            DataModels.setCurrentUser("demo_user", "DL-01-DEMO");
            if (onUserLogin != null) {
                onUserLogin.actionPerformed(null);
            }
            mainCards.show(mainPanel, "user");
        });
        
        return card;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(UIComponents.TEXT_PRIMARY);
        return label;
    }
    
    public void setOnAdminLogin(ActionListener listener) {
        this.onAdminLogin = listener;
    }
    
    public void setOnUserLogin(ActionListener listener) {
        this.onUserLogin = listener;
    }
}
