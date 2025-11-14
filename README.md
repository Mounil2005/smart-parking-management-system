# 🚗 Smart Parking System

A modern Java Swing-based parking management system with database integration and role-based access control.

## 📋 Features

### User Module
- **Real-time Slot Availability**: View all available parking slots
- **Easy Booking**: Select and book parking slots instantly
- **Cost Calculator**: Calculate parking charges based on duration
- **Booking Management**: View current bookings and release slots
- **Modern UI**: Clean, intuitive interface with gradient headers

### Admin Module
- **Dashboard Overview**: Quick stats on total, available, and occupied slots
- **Slot Management**: View and manage all parking slots
- **Booking Records**: Complete history of all parking transactions
- **Cost Calculator**: Calculate charges for any occupied slot
- **Dynamic Slot Addition**: Add new parking slots on-the-fly
- **Bulk Operations**: Clear all bookings at once

## 🛠️ Technical Stack

- **Language**: Java (JDK 11+)
- **GUI Framework**: Java Swing
- **Database**: SQLite (with in-memory fallback)
- **Architecture**: Modular MVC pattern
- **Libraries**: 
  - SQLite JDBC Driver
  - SLF4J for logging

## 📁 Project Structure

```
Java Project 2025/
├── ParkingSystemMain.java      # Main entry point and coordinator
├── DataModels.java              # Data structures and business logic
├── DatabaseManager.java         # Database operations and persistence
├── UIComponents.java            # Custom UI components and styling
├── LoginPanel.java              # Authentication and role selection
├── AdminDashboard.java          # Admin module interface
├── UserDashboard.java           # User module interface
├── CheckDatabase.java           # Database verification utility
├── run.bat                      # Quick run script for Windows
└── README.md                    # This file
```

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Git (for cloning)

### Quick Start (Easiest Way)

1. **Clone the repository**
   ```bash
   git clone https://github.com/Mounil2005/smart-parking-management-system.git
   cd smart-parking-management-system
   ```

2. **Run using the batch file** (Windows)
   ```bash
   run.bat
   ```
   
   That's it! The application will compile and run automatically.

### Manual Installation

If you prefer to run manually or are on Linux/Mac:

1. **Clone the repository**
   ```bash
   git clone https://github.com/Mounil2005/smart-parking-management-system.git
   cd smart-parking-management-system
   ```

2. **Download Dependencies** (if not included)
   - SQLite JDBC: `sqlite-jdbc-3.46.1.3.jar`
   - SLF4J API: `slf4j-api-2.0.9.jar`
   - SLF4J Simple: `slf4j-simple-2.0.9.jar`
   
   Place these JAR files in the project root directory.

3. **Compile the project**
   
   **Windows:**
   ```bash
   javac -cp ".;sqlite-jdbc-3.46.1.3.jar;slf4j-api-2.0.9.jar;slf4j-simple-2.0.9.jar" *.java
   ```
   
   **Linux/Mac:**
   ```bash
   javac -cp ".:sqlite-jdbc-3.46.1.3.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar" *.java
   ```

4. **Run the application**
   
   **Windows:**
   ```bash
   java -cp ".;sqlite-jdbc-3.46.1.3.jar;slf4j-api-2.0.9.jar;slf4j-simple-2.0.9.jar" ParkingSystemMain
   ```
   
   **Linux/Mac:**
   ```bash
   java -cp ".:sqlite-jdbc-3.46.1.3.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar" ParkingSystemMain
   ```

## 🔐 Login Credentials

### Admin Access
- **Username**: `admin`
- **Password**: `1234`

### User Access
- **Username**: Any name (e.g., "John")
- **Vehicle Number**: Any vehicle number (e.g., "MH12AB1234")

## 💰 Pricing

- **Rate**: ₹20 per hour
- **Billing**: Rounded up to the nearest hour
- **Example**: 1 hour 15 minutes = 2 hours = ₹40

## 🎨 UI Features

- Modern gradient headers
- Card-based layouts
- Color-coded status indicators (Green = Available, Red = Occupied)
- Responsive design
- Hover effects on buttons
- Clean typography

## 🗄️ Database Schema

### parking_slots Table
| Column     | Type    | Description                    |
|------------|---------|--------------------------------|
| id         | INTEGER | Primary key, slot number       |
| available  | INTEGER | 1 = available, 0 = occupied    |
| booked_by  | TEXT    | Username of the person booking |
| vehicle    | TEXT    | Vehicle registration number    |
| in_time    | TEXT    | Check-in timestamp             |

### bookings Table
| Column    | Type    | Description                 |
|-----------|---------|----------------------------|
| id        | INTEGER | Auto-increment primary key |
| slot_id   | INTEGER | Reference to parking slot  |
| user      | TEXT    | Username                   |
| vehicle   | TEXT    | Vehicle number             |
| in_time   | TEXT    | Check-in time              |
| out_time  | TEXT    | Check-out time             |
| cost      | REAL    | Total parking cost         |

## 🔧 Configuration

### Modify Parking Rate
Edit `UIComponents.java`:
```java
public static final double RATE_PER_HOUR = 20.0; // Change this value
```

### Change Initial Slot Count
Edit `ParkingSystemMain.java`:
```java
DataModels.initializeSlots(8); // Change number of slots
```

## 🤝 Team Contributions

This project was developed as a team effort with the following module distribution:

- **UI/UX Team** (2 members): `UIComponents.java`, `LoginPanel.java`
- **Admin Module** (1 member): `AdminDashboard.java`
- **User Module** (1 member): `UserDashboard.java`
- **Data Management** (Shared): `DataModels.java`, `DatabaseManager.java`

## 📝 Key Design Patterns

- **MVC Architecture**: Separation of data, view, and control logic
- **Singleton Pattern**: DatabaseManager for single database connection
- **Observer Pattern**: UI updates on data changes
- **Factory Pattern**: UI component creation
- **Graceful Degradation**: Fallback to in-memory mode if database fails

## 🐛 Known Issues

- SQLite native library compatibility with JDK 23 on some Windows systems
- Application falls back to in-memory mode if database connection fails
- Data persistence requires proper SQLite library installation

## 🔮 Future Enhancements

- [ ] Export booking data to Excel/PDF
- [ ] Email notifications for bookings
- [ ] Multi-level admin access
- [ ] Payment gateway integration
- [ ] Mobile app integration
- [ ] Real-time slot monitoring with cameras
- [ ] Automated reports and analytics
- [ ] QR code-based entry/exit

## 📄 License

This project is developed for educational purposes.

## 👥 Authors

- Your Team Name
- Contact: your.email@example.com

## 🙏 Acknowledgments

- Java Swing documentation
- SQLite JDBC driver developers
- Modern UI design principles

---

**Note**: This is a demonstration project showcasing modular Java development, database integration, and modern UI design principles.
