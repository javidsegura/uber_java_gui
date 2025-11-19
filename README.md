# TeeTime - Campus Ride Sharing

A JavaFX desktop application for university ride sharing that connects Passengers and Drivers.

## Features

- **User Registration & Login** with IE University email validation
- **Passenger Features:**
  - Create ride requests (origin, destination, time, seats)
  - View all your rides
  - Export ride history to CSV

- **Driver Features:**
  - Manage cars (add/delete)
  - View and accept pending ride requests
  - View accepted rides
  - Complete rides
  - Export ride history to CSV

- **Database:** In-memory storage using Java collections (HashMap, ArrayList)
- **File I/O:** CSV export functionality
- **Exception Handling:** Custom exceptions for capacity and login validation

## Technology Stack

- Java 17
- JavaFX 21
- Maven

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

Simply run:
```bash
make
```

Or use Maven directly:
```bash
mvn clean javafx:run
```

The application will:
1. Initialize the in-memory database with a default admin user
2. Open the login screen

### Default Admin Account

A default admin user is created on startup:
- **Email:** `admin@ie.edu`
- **Password:** `admin`
- **Role:** BOTH (can act as both Passenger and Driver)

### First Time Setup

1. Click "Register" to create a new account
2. Enter your details:
   - Name: Your full name
   - Email: Must be @student.ie.edu or @ie.edu domain
   - Password: At least 4 characters
   - Role: Choose PASSENGER, DRIVER, or BOTH
3. Click "Register"
4. Login with your credentials

### Using as a Passenger

1. Login and you'll see the Passenger Dashboard
2. Fill in the ride request form:
   - Origin and destination
   - Date and time (format: HH:MM, e.g., 14:30)
   - Number of seats needed
3. Click "Create Request"
4. Your rides will appear in the table below
5. Click "Export to CSV" to save your ride history

### Using as a Driver

1. Login (must have DRIVER or BOTH role)
2. You'll see the Driver Dashboard with 3 tabs:

**My Cars Tab:**
- Add your cars with plate, brand, and number of seats
- Delete cars if needed

**Available Requests Tab:**
- View all pending ride requests from passengers
- Click "Accept" to accept a ride
- Select which car to use
- System validates that car has enough seats

**My Accepted Rides Tab:**
- View all rides you've accepted
- Click "Complete" to mark rides as completed
- Export to CSV

## Project Structure

```
src/main/java/com/teetime/
├── Main.java                      # Application entry point
├── domain/                        # Domain model classes
│   ├── User.java (abstract)
│   ├── Passenger.java
│   ├── Driver.java
│   ├── Car.java
│   ├── Ride.java
│   └── RideStatus.java (enum)
├── exception/                     # Custom exceptions
│   ├── InvalidLoginException.java
│   └── CapacityExceededException.java
├── service/                       # Business logic
│   ├── AuthService.java
│   ├── RideService.java
│   └── CSVExportService.java
├── database/                      # Data persistence
│   └── DatabaseManager.java
└── gui/                          # JavaFX UI
    ├── LoginScreen.java
    ├── PassengerDashboard.java
    └── DriverDashboard.java
```

## Data Model

**Users** (HashMap<Integer, User>):
- id, name, email, password_hash, role

**Cars** (HashMap<Integer, Car>):
- id, driver_id, plate, brand, seats

**Rides** (HashMap<Integer, Ride>):
- id, passenger_id, driver_id, car_id, origin, destination, time, seats_needed, status, price_estimate

## Testing

To test the application:

1. Register two users:
   - One as PASSENGER
   - One as DRIVER

2. **As Passenger:**
   - Login and create a ride request
   - Note the details

3. **As Driver:**
   - Add a car first
   - Go to "Available Requests" tab
   - Accept the passenger's request
   - Complete the ride

4. **Test CSV Export:**
   - Both users can export their rides to CSV

## Notes

- All data is stored in-memory and will be lost when the application closes
- A default admin user (admin@ie.edu / admin) is created on startup
- Email validation requires @student.ie.edu or @ie.edu domains
- Price estimation is calculated automatically based on distance and seats
- Password hashing uses SHA-256

## Documentation

See the [Diagrams folder](./Diagrams/README.md) for system design diagrams (class, ER, architecture, sequence, state, activity, and use case diagrams).
