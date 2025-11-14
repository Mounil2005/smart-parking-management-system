import java.sql.*;

public class CheckDatabase {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:parking_system.db";
        
        System.out.println("=== Checking Database ===\n");
        
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("✓ SQLite JDBC driver loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ SQLite JDBC driver not found");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            System.out.println("✓ Database connection successful\n");
            
            // Check parking_slots table
            System.out.println("--- PARKING SLOTS ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM parking_slots ORDER BY id")) {
                
                int count = 0;
                while (rs.next()) {
                    count++;
                    int id = rs.getInt("id");
                    boolean available = rs.getInt("available") == 1;
                    String bookedBy = rs.getString("booked_by");
                    String vehicle = rs.getString("vehicle");
                    
                    System.out.printf("Slot %d: %s", id, available ? "Available" : "Occupied");
                    if (!available) {
                        System.out.printf(" (User: %s, Vehicle: %s)", bookedBy, vehicle);
                    }
                    System.out.println();
                }
                System.out.println("Total slots: " + count);
            }
            
            System.out.println("\n--- BOOKINGS ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM bookings ORDER BY id DESC LIMIT 10")) {
                
                int count = 0;
                while (rs.next()) {
                    count++;
                    int slotId = rs.getInt("slot_id");
                    String user = rs.getString("user");
                    String vehicle = rs.getString("vehicle");
                    double cost = rs.getDouble("cost");
                    
                    System.out.printf("Slot %d - User: %s, Vehicle: %s, Cost: ₹%.2f\n", 
                        slotId, user, vehicle, cost);
                }
                System.out.println("Total bookings: " + count);
            }
            
        } catch (SQLException e) {
            System.out.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
