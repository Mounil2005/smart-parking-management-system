import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:parking_system.db";
    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static boolean databaseAvailable = false;

    static {
        initializeDatabase();
    }

    private DatabaseManager() {
    }

    private static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("WARNING: SQLite JDBC driver not found. Running in memory-only mode.");
            databaseAvailable = false;
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS parking_slots (" +
                    "id INTEGER PRIMARY KEY," +
                    "available INTEGER NOT NULL DEFAULT 1," +
                    "booked_by TEXT," +
                    "vehicle TEXT," +
                    "in_time TEXT" +
                ")");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "slot_id INTEGER NOT NULL," +
                    "user TEXT NOT NULL," +
                    "vehicle TEXT NOT NULL," +
                    "in_time TEXT NOT NULL," +
                    "out_time TEXT NOT NULL," +
                    "cost REAL NOT NULL," +
                    "FOREIGN KEY(slot_id) REFERENCES parking_slots(id)" +
                ")");
            
            databaseAvailable = true;
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("WARNING: Failed to initialize database. Running in memory-only mode.");
            System.err.println("Error: " + e.getMessage());
            databaseAvailable = false;
        }
    }
    
    public static boolean isDatabaseAvailable() {
        return databaseAvailable;
    }

    public static void ensureMinimumSlots(int minimumSlots) {
        if (!databaseAvailable) return;
        
        int existingSlots = countSlots();
        if (existingSlots >= minimumSlots) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO parking_slots(id, available) VALUES(?, 1)")) {
                for (int id = existingSlots + 1; id <= minimumSlots; id++) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Failed to ensure minimum slots: " + e.getMessage());
        }
    }

    public static List<DataModels.ParkingSlot> fetchAllSlots() {
        List<DataModels.ParkingSlot> slots = new ArrayList<>();
        if (!databaseAvailable) return slots;
        
        String sql = "SELECT id, available, booked_by, vehicle, in_time FROM parking_slots ORDER BY id";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DataModels.ParkingSlot slot = new DataModels.ParkingSlot(rs.getInt("id"));
                slot.available = rs.getInt("available") == 1;
                slot.bookedBy = rs.getString("booked_by");
                slot.vehicle = rs.getString("vehicle");

                String inTime = rs.getString("in_time");
                slot.inTime = (inTime == null || inTime.isEmpty())
                    ? null
                    : LocalDateTime.parse(inTime, DF);
                slots.add(slot);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch parking slots: " + e.getMessage());
        }

        return slots;
    }

    public static void insertSlot(int id) {
        if (!databaseAvailable) return;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO parking_slots(id, available) VALUES(?, 1)")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert parking slot: " + e.getMessage());
        }
    }

    public static void updateSlot(DataModels.ParkingSlot slot) {
        if (!databaseAvailable) return;
        String sql = "UPDATE parking_slots SET available = ?, booked_by = ?, vehicle = ?, in_time = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, slot.available ? 1 : 0);

            if (slot.bookedBy == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, slot.bookedBy);
            }

            if (slot.vehicle == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, slot.vehicle);
            }

            if (slot.inTime == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, DF.format(slot.inTime));
            }

            ps.setInt(5, slot.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to update parking slot: " + e.getMessage());
        }
    }

    public static List<DataModels.Booking> fetchAllBookings() {
        if (!databaseAvailable) return new ArrayList<>();
        List<DataModels.Booking> bookings = new ArrayList<>();
        String sql = "SELECT slot_id, user, vehicle, in_time, out_time, cost FROM bookings ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDateTime inTime = LocalDateTime.parse(rs.getString("in_time"), DF);
                LocalDateTime outTime = LocalDateTime.parse(rs.getString("out_time"), DF);

                bookings.add(new DataModels.Booking(
                    rs.getInt("slot_id"),
                    rs.getString("user"),
                    rs.getString("vehicle"),
                    inTime,
                    outTime,
                    rs.getDouble("cost")));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch bookings: " + e.getMessage());
        }

        return bookings;
    }

    public static void insertBooking(DataModels.Booking booking) {
        if (!databaseAvailable) return;
        String sql = "INSERT INTO bookings(slot_id, user, vehicle, in_time, out_time, cost) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, booking.slotId);
            ps.setString(2, booking.user);
            ps.setString(3, booking.vehicle);
            ps.setString(4, DF.format(booking.inTime));
            ps.setString(5, DF.format(booking.outTime));
            ps.setDouble(6, booking.cost);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert booking: " + e.getMessage());
        }
    }

    public static void resetSlotsAndBookings() {
        if (!databaseAvailable) return;
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM bookings");
                stmt.executeUpdate(
                    "UPDATE parking_slots " +
                    "SET available = 1, booked_by = NULL, vehicle = NULL, in_time = NULL");
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Failed to reset slots and bookings: " + e.getMessage());
        }
    }

    private static int countSlots() {
        if (!databaseAvailable) return 0;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM parking_slots")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Failed to count parking slots: " + e.getMessage());
            return 0;
        }
    }
}
