import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataModels {

    public static class ParkingSlot {
        public int id;
        public boolean available = true;
        public LocalDateTime inTime = null;
        public String bookedBy = null;
        public String vehicle = null;

        public ParkingSlot(int id) {
            this.id = id;
        }
    }

    public static class Booking {
        public int slotId;
        public String user;
        public String vehicle;
        public LocalDateTime inTime;
        public LocalDateTime outTime;
        public double cost;

        public Booking(int slotId, String user, String vehicle,
                       LocalDateTime inTime, LocalDateTime outTime, double cost) {
            this.slotId = slotId;
            this.user = user;
            this.vehicle = vehicle;
            this.inTime = inTime;
            this.outTime = outTime;
            this.cost = cost;
        }
    }

    private static final List<ParkingSlot> slots = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();

    private static String currentUser = null;
    private static String currentVehicle = null;

    public static void initializeSlots(int numberOfSlots) {
        DatabaseManager.ensureMinimumSlots(numberOfSlots);
        reloadSlots();
        
        if (slots.isEmpty()) {
            for (int i = 1; i <= numberOfSlots; i++) {
                slots.add(new ParkingSlot(i));
            }
        }
        
        reloadBookings();
    }

    public static List<ParkingSlot> getSlots() {
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadSlots();
        }
        return new ArrayList<>(slots);
    }

    public static List<Booking> getBookings() {
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadBookings();
        }
        return new ArrayList<>(bookings);
    }

    public static ParkingSlot getSlotById(int id) {
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadSlots();
        }
        for (ParkingSlot slot : slots) {
            if (slot.id == id) {
                return cloneSlot(slot);
            }
        }
        return null;
    }

    public static boolean updateSlot(ParkingSlot slot) {
        if (slot == null) {
            return false;
        }
        DatabaseManager.updateSlot(slot);
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadSlots();
        }
        return true;
    }

    public static void addSlot(int id) {
        DatabaseManager.insertSlot(id);
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadSlots();
        } else {
            slots.add(new ParkingSlot(id));
        }
    }

    public static void addBooking(Booking booking) {
        if (booking == null) {
            return;
        }
        DatabaseManager.insertBooking(booking);
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadBookings();
        } else {
            bookings.add(booking);
        }
    }

    public static void clearAllBookings() {
        DatabaseManager.resetSlotsAndBookings();
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadSlots();
            reloadBookings();
        } else {
            for (ParkingSlot s : slots) {
                s.available = true;
                s.bookedBy = null;
                s.vehicle = null;
                s.inTime = null;
            }
            bookings.clear();
        }
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentVehicle() {
        return currentVehicle;
    }

    public static void setCurrentUser(String user, String vehicle) {
        currentUser = user;
        currentVehicle = vehicle;
    }

    public static void clearCurrentUser() {
        currentUser = null;
        currentVehicle = null;
    }

    public static ParkingSlot findUserBooking(String user, String vehicle) {
        if (user == null || vehicle == null) {
            return null;
        }
        if (DatabaseManager.isDatabaseAvailable()) {
            reloadSlots();
        }
        for (ParkingSlot slot : slots) {
            if (!slot.available && user.equals(slot.bookedBy) && vehicle.equals(slot.vehicle)) {
                return slot;
            }
        }
        return null;
    }

    private static void reloadSlots() {
        slots.clear();
        slots.addAll(DatabaseManager.fetchAllSlots());
    }

    private static void reloadBookings() {
        bookings.clear();
        bookings.addAll(DatabaseManager.fetchAllBookings());
    }

    private static ParkingSlot cloneSlot(ParkingSlot slot) {
        ParkingSlot copy = new ParkingSlot(slot.id);
        copy.available = slot.available;
        copy.inTime = slot.inTime;
        copy.bookedBy = slot.bookedBy;
        copy.vehicle = slot.vehicle;
        return copy;
    }
}
