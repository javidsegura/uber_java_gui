# TeeTime - System Diagrams

Campus Ride-Sharing Platform - Phase 1 Design Diagrams

## 📋 Class Diagram

Core classes, services, and data access layer for the TeeTime system.

```mermaid
classDiagram
    %% Core Abstract Class
    class User {
        <<abstract>>
        #int userId
        #String name
        #String email
        #String passwordHash
        #String role
        +login() boolean
        +logout() void
        +updateProfile() void
        +switchRole(newRole) void
    }

    %% User Subclasses
    class Passenger {
        -List~Ride~ myRides
        +createRideRequest(origin, destination, time, seats) Ride
        +viewMyRequests() List~Ride~
        +cancelRequest(rideId) boolean
        +viewRideHistory() List~Ride~
    }

    class Driver {
        -List~Car~ myCars
        -boolean isAvailable
        +addCar(carDetails) Car
        +removeCar(carId) boolean
        +viewOpenRequests() List~Ride~
        +acceptRide(rideId) boolean
        +declineRide(rideId) boolean
        +markRideComplete(rideId) void
        +viewMyAcceptedRides() List~Ride~
    }

    %% Core Entity Classes
    class Car {
        -int carId
        -int driverId
        -String licensePlate
        -String brand
        -String model
        -int seatingCapacity
        +getAvailableSeats() int
        +isAvailable() boolean
    }

    class Ride {
        -int rideId
        -int passengerId
        -int driverId
        -int carId
        -String origin
        -String destination
        -DateTime requestTime
        -DateTime scheduledTime
        -int seatsNeeded
        -double priceEstimate
        -RideStatus status
        +calculatePrice() double
        +assignDriver(driver, car) void
        +updateStatus(status) void
    }

    class RideStatus {
        <<enumeration>>
        PENDING
        CONFIRMED
        COMPLETED
        CANCELLED
    }

    %% Service Layer
    class AuthService {
        -DatabaseManager db
        +register(name, email, password, role) User
        +login(email, password) User
        +logout(userId) void
        +validateEmail(email) boolean
        +hashPassword(password) String
        +switchUserRole(userId, newRole) void
    }

    class RideService {
        -DatabaseManager db
        +createRideRequest(passengerDetails) Ride
        +listOpenRequests() List~Ride~
        +acceptRide(rideId, driverId, carId) boolean
        +markRideComplete(rideId) void
        +getRidesByPassenger(passengerId) List~Ride~
        +getRidesByDriver(driverId) List~Ride~
        +cancelRide(rideId) boolean
        +calculatePriceEstimate(origin, destination) double
    }

    %% Data Access Layer
    class DatabaseManager {
        -Connection connection
        -String dbUrl
        +connect() boolean
        +disconnect() void
        +executeQuery(sql) ResultSet
        +executeUpdate(sql) int
        +beginTransaction() void
        +commit() void
        +rollback() void
    }

    class UserDAO {
        -DatabaseManager db
        +save(user) boolean
        +findById(id) User
        +findByEmail(email) User
        +update(user) boolean
        +delete(id) boolean
    }

    class RideDAO {
        -DatabaseManager db
        +save(ride) boolean
        +findById(id) Ride
        +findPendingRides() List~Ride~
        +findByPassenger(passengerId) List~Ride~
        +findByDriver(driverId) List~Ride~
        +update(ride) boolean
    }

    class CarDAO {
        -DatabaseManager db
        +save(car) boolean
        +findById(id) Car
        +findByDriver(driverId) List~Car~
        +update(car) boolean
        +delete(id) boolean
    }

    %% Relationships
    User <|-- Passenger
    User <|-- Driver
    Driver "1" --> "0..*" Car : owns
    Passenger "1" --> "0..*" Ride : requests
    Driver "0..1" --> "0..*" Ride : accepts
    Ride "1" --> "1" Car : uses
    Ride "1" --> "1" RideStatus : has
    AuthService --> User : manages
    AuthService --> UserDAO : uses
    RideService --> Ride : manages
    RideService --> RideDAO : uses
    RideService --> CarDAO : uses
    UserDAO --> DatabaseManager : uses
    RideDAO --> DatabaseManager : uses
    CarDAO --> DatabaseManager : uses
```

---

## 🗄️ Entity-Relationship Diagram

Database schema with 3 core tables: USERS, CARS, and RIDES.

```mermaid
erDiagram
    USERS ||--o{ CARS : "owns"
    USERS ||--o{ RIDES : "requests/accepts"

    USERS {
        int user_id PK "Auto-increment primary key"
        string name "User full name"
        string email UK "University email (unique)"
        string password_hash "Hashed password"
        string role "PASSENGER, DRIVER, or BOTH"
        datetime created_at "Account creation timestamp"
    }

    CARS {
        int car_id PK "Auto-increment primary key"
        int driver_id FK "References USERS(user_id)"
        string license_plate UK "Unique plate number"
        string brand "Car manufacturer"
        string model "Car model"
        int seating_capacity "Total seats (1-8)"
        datetime registered_at "Car registration timestamp"
    }

    RIDES {
        int ride_id PK "Auto-increment primary key"
        int passenger_id FK "References USERS(user_id)"
        int driver_id FK "References USERS(user_id), nullable until accepted"
        int car_id FK "References CARS(car_id), nullable until accepted"
        string origin "Starting location"
        string destination "End location"
        datetime request_time "When passenger created request"
        datetime scheduled_time "When ride should happen"
        int seats_needed "Number of seats passenger needs"
        decimal price_estimate "Estimated cost"
        string status "PENDING, CONFIRMED, COMPLETED, CANCELLED"
    }
```

---

## 👥 Use Case Diagram

Core interactions for Passenger-Driver matching workflow.

```mermaid
graph LR
    Passenger((Passenger))
    Driver((Driver))

    subgraph System["TeeTime Ride-Sharing System"]
        direction TB

        UC1[Register Account]
        UC2[Login/Logout]
        UC3[Create Ride Request]
        UC4[View My Requests]
        UC5[Cancel Pending Request]
        UC6[View Ride History]
        UC7[Switch to Driver Role]

        UC8[Add Car]
        UC9[Remove Car]
        UC10[View My Cars]
        UC11[View Open Ride Requests]
        UC12[Accept Ride Request]
        UC13[Decline Ride Request]
        UC14[View My Accepted Rides]
        UC15[Mark Ride Complete]
        UC16[Switch to Passenger Role]

        UC17[Update Profile]
        UC18[Export Ride History CSV]
    end

    Passenger --> UC1
    Passenger --> UC2
    Passenger --> UC3
    Passenger --> UC4
    Passenger --> UC5
    Passenger --> UC6
    Passenger --> UC7
    Passenger --> UC17
    Passenger --> UC18

    Driver --> UC8
    Driver --> UC9
    Driver --> UC10
    Driver --> UC11
    Driver --> UC12
    Driver --> UC13
    Driver --> UC14
    Driver --> UC15
    Driver --> UC16
    Driver --> UC3
    Driver --> UC4
    Driver --> UC6
    Driver --> UC17
    Driver --> UC18
```

---

## 🔄 Sequence Diagram - Ride Matching Flow

Complete interaction showing how a passenger request gets matched with a driver.

```mermaid
sequenceDiagram
    actor P as Passenger
    participant UI as JavaFX UI
    participant Auth as AuthService
    participant RS as RideService
    participant DB as DatabaseManager
    actor D as Driver

    Note over P,D: 1. Passenger Login & Ride Request
    P->>UI: Login (email, password)
    UI->>Auth: login(email, password)
    Auth->>DB: Query user by email
    DB-->>Auth: User data
    Auth->>Auth: Verify password hash
    Auth-->>UI: User object (role: PASSENGER)
    UI-->>P: Show Passenger Dashboard

    P->>UI: Create ride request<br/>(origin, destination, time, seats)
    UI->>RS: createRideRequest(passengerDetails)
    RS->>RS: validateRequest()
    RS->>RS: calculatePriceEstimate()
    RS->>DB: INSERT INTO rides (status=PENDING)
    DB-->>RS: ride_id
    RS-->>UI: Ride created successfully
    UI-->>P: Request submitted!

    Note over P,D: 2. Driver Views & Accepts Request
    D->>UI: Login (email, password)
    UI->>Auth: login(email, password)
    Auth-->>UI: User object (role: DRIVER)
    UI-->>D: Show Driver Dashboard

    D->>UI: View open ride requests
    UI->>RS: listOpenRequests()
    RS->>DB: SELECT * FROM rides WHERE status=PENDING
    DB-->>RS: List of pending rides
    RS-->>UI: Pending rides with details
    UI-->>D: Display requests table

    D->>UI: Accept ride (select car)
    UI->>RS: acceptRide(rideId, driverId, carId)
    RS->>DB: SELECT seating_capacity FROM cars
    DB-->>RS: Car capacity

    alt Capacity sufficient
        RS->>RS: Validate: car.capacity >= ride.seatsNeeded
        RS->>DB: BEGIN TRANSACTION
        RS->>DB: UPDATE rides SET driver_id=?, car_id=?, status=CONFIRMED
        RS->>DB: COMMIT
        DB-->>RS: Success
        RS-->>UI: Ride accepted!
        UI-->>D: Ride confirmed!
    else Capacity exceeded
        RS->>RS: Throw CapacityExceededException
        RS-->>UI: Error: Car doesn't have enough seats
    end

    Note over P,D: 3. Ride Completion
    D->>UI: Mark ride complete
    UI->>RS: markRideComplete(rideId)
    RS->>DB: UPDATE rides SET status=COMPLETED
    DB-->>RS: Success
    RS-->>UI: Ride marked complete
    UI-->>D: Ride completed successfully
```

---

## 🏗️ System Architecture

4-layer architecture with clear separation of concerns.

```mermaid
graph TB
    subgraph PresentationLayer["Presentation Layer - JavaFX"]
        LoginView[Login/Register Screen]
        PassengerDash[Passenger Dashboard<br/>- Create Request<br/>- My Requests<br/>- History]
        DriverDash[Driver Dashboard<br/>- My Cars<br/>- Open Requests<br/>- My Accepted Rides]
    end

    subgraph BusinessLayer["Business Logic Layer"]
        AuthService[AuthService<br/>- Login/Register<br/>- Email validation<br/>- Role switching]
        RideService[RideService<br/>- Create/Accept rides<br/>- List requests<br/>- Calculate price<br/>- Complete rides]
        CSVExporter[CSVExporter<br/>- Export ride history]
    end

    subgraph DataAccessLayer["Data Access Layer - DAO Pattern"]
        UserDAO[UserDAO]
        RideDAO[RideDAO]
        CarDAO[CarDAO]
        DatabaseManager[DatabaseManager<br/>- Connection pool<br/>- Transactions]
    end

    subgraph DataLayer["Data Layer"]
        Database[(MySQL/PostgreSQL<br/>Tables:<br/>- users<br/>- cars<br/>- rides)]
        FileSystem[File System<br/>- CSV exports]
    end

    LoginView --> AuthService
    PassengerDash --> RideService
    DriverDash --> RideService
    DriverDash --> CSVExporter
    PassengerDash --> CSVExporter

    AuthService --> UserDAO
    RideService --> RideDAO
    RideService --> CarDAO

    UserDAO --> DatabaseManager
    RideDAO --> DatabaseManager
    CarDAO --> DatabaseManager

    DatabaseManager --> Database
    CSVExporter --> FileSystem

    style PresentationLayer fill:#e1f5ff
    style BusinessLayer fill:#fff4e1
    style DataAccessLayer fill:#e8f5e8
    style DataLayer fill:#f0f0f0
```

---

## 🔴 State Diagram - Ride Lifecycle

Simplified 4-state lifecycle for rides: PENDING → CONFIRMED → COMPLETED/CANCELLED.

```mermaid
stateDiagram-v2
    [*] --> PENDING: Passenger creates ride request

    PENDING --> CONFIRMED: Driver accepts with valid car
    PENDING --> CANCELLED: Passenger cancels<br/>or 24h timeout

    CONFIRMED --> COMPLETED: Driver marks complete
    CONFIRMED --> CANCELLED: Passenger or Driver cancels

    COMPLETED --> [*]
    CANCELLED --> [*]

    note right of PENDING
        - Ride request created
        - Visible to all drivers
        - driver_id and car_id are NULL
        - Auto-cancel after 24 hours
    end note

    note right of CONFIRMED
        - Driver and car assigned
        - driver_id and car_id populated
        - Passenger notified
        - Ready for pickup
    end note

    note right of COMPLETED
        - Ride finished
        - Can be exported to CSV
        - Visible in history
    end note

    note right of CANCELLED
        - Ride terminated
        - Reason recorded
        - Visible in history
    end note
```

---

## 📊 Activity Diagram - End-to-End Flow

Complete workflow from passenger request creation through driver acceptance to ride completion.

```mermaid
graph TB
    Start([Passenger wants a ride]) --> Login1{Logged in?}

    Login1 -->|No| Register[Register with<br/>university email]
    Register --> Verify[Verify email]
    Verify --> Login1

    Login1 -->|Yes| CreateRequest[Fill ride request form:<br/>- Origin<br/>- Destination<br/>- Time<br/>- Seats needed]

    CreateRequest --> Validate{Input valid?}
    Validate -->|No| ShowError1[Show validation errors]
    ShowError1 --> CreateRequest

    Validate -->|Yes| Calculate[Calculate price estimate<br/>distance × base_rate]
    Calculate --> SaveRequest[Save ride to DB<br/>status = PENDING]
    SaveRequest --> WaitDriver[Wait for driver to accept]

    WaitDriver --> Timeout{24h passed?}
    Timeout -->|Yes| AutoCancel[Auto-cancel ride]
    AutoCancel --> End1([Ride cancelled])

    Timeout -->|No| DriverCheck{Driver views<br/>open requests?}

    DriverCheck -->|No| WaitDriver

    DriverCheck -->|Yes| DriverLogin{Driver<br/>logged in?}
    DriverLogin -->|No| DriverReg[Driver registers<br/>and adds car]
    DriverReg --> DriverLogin

    DriverLogin -->|Yes| ViewRequests[Driver sees list of<br/>PENDING rides]
    ViewRequests --> SelectRide[Driver selects a ride]
    SelectRide --> ChooseCar[Driver chooses which car to use]

    ChooseCar --> CheckCapacity{Car capacity >=<br/>seats needed?}

    CheckCapacity -->|No| ErrorCapacity[Show error:<br/>CapacityExceededException]
    ErrorCapacity --> ChooseCar

    CheckCapacity -->|Yes| AcceptRide[Assign driver & car to ride<br/>status = CONFIRMED]
    AcceptRide --> NotifyPassenger[Notify passenger:<br/>Driver found!]
    NotifyPassenger --> RideDay[Wait for scheduled time]

    RideDay --> Pickup[Driver picks up passenger]
    Pickup --> Complete[Driver marks<br/>ride COMPLETED]
    Complete --> UpdateDB[Update ride status in DB]
    UpdateDB --> ExportOption{Want to export<br/>history?}

    ExportOption -->|Yes| ExportCSV[Export rides to CSV]
    ExportCSV --> End2([Done])

    ExportOption -->|No| End2

    style Start fill:#e1f5ff
    style End1 fill:#ffcccc
    style End2 fill:#ccffcc
    style CreateRequest fill:#fff4e1
    style AcceptRide fill:#ccffcc
    style ErrorCapacity fill:#ffcccc
```

---

## 📁 Files in this Directory

- **simplified-class-diagram.mermaid** - UML class diagram
- **simplified-er-diagram.mermaid** - Entity-relationship diagram
- **simplified-use-case-diagram.mermaid** - Use case diagram
- **simplified-sequence-diagram.mermaid** - Sequence diagram
- **simplified-architecture-diagram.mermaid** - Architecture diagram
- **simplified-state-diagram.mermaid** - State diagram
- **simplified-activity-diagram.mermaid** - Activity diagram
- **teetime-simplified.html** - Original HTML with all diagrams
- **teetime-simplified-fixed.html** - Fixed HTML with working diagrams
- **README.md** - This file

## 🚀 Viewing the Diagrams

**On GitHub**: All diagrams above render automatically in this README!

**Locally in VS Code**:
1. Install "Markdown Preview Mermaid Support" extension
2. Open this README file
3. Click the preview button to see diagrams

**In a Browser**:
```bash
# Start the web server (from project root)
python3 view_diagrams.py

# Then open http://localhost:8000/teetime-simplified-fixed.html
```

**In Terminal**:
```bash
# Display raw Mermaid syntax (from project root)
python3 show_diagrams.py
```

---

**Phase 1 - Core Implementation**
TeeTime Campus Ride-Sharing Platform
