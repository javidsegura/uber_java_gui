#!/usr/bin/env python3
"""
Display Mermaid diagrams directly in the terminal
"""

diagrams = {
    "1. System Overview": """
graph TB
    User["👤 User<br/>(Passenger/Driver)"]
    Auth["🔐 Auth Service<br/>Login/Register"]
    Ride["🚗 Ride Service<br/>Manage Rides"]
    Car["🏎️ Car Service<br/>Manage Vehicles"]
    DB[(💾 Database<br/>Users/Rides/Cars)]

    User -->|Login| Auth
    Auth -->|Create/Update User| DB

    User -->|Request/Accept Ride| Ride
    Ride -->|Query/Update Rides| DB

    User -->|Add/Remove Car| Car
    Car -->|Query/Update Cars| DB

    Ride -->|Check Capacity| Car

    style User fill:#e1f5ff
    style Auth fill:#fff4e1
    style Ride fill:#e8f5e8
    style Car fill:#f0e8f5
    style DB fill:#f5f5f5
""",

    "2. Ride Request Flow": """
sequenceDiagram
    actor Passenger
    actor Driver
    participant App as App
    participant Service as Ride Service
    participant DB as Database

    Passenger ->> App: Request Ride
    App ->> Service: createRequest(origin, dest)
    Service ->> Service: calculatePrice()
    Service ->> DB: Save Ride (PENDING)
    DB -->> Service: Success
    Service -->> App: Ride Created

    Driver ->> App: View Open Requests
    App ->> Service: listOpenRequests()
    Service ->> DB: Get PENDING rides
    DB -->> Service: Ride List
    Service -->> App: Show Requests

    Driver ->> App: Accept Ride + Select Car
    App ->> Service: acceptRide(rideId, carId)
    Service ->> Service: checkCapacity()
    alt Capacity OK
        Service ->> DB: Update (CONFIRMED)
        DB -->> Service: Success
        Service -->> App: Ride Accepted
        App ->> Passenger: Driver Found!
    else Not Enough Seats
        Service -->> App: Error: Capacity Exceeded
    end
""",

    "3. Architecture": """
graph TB
    subgraph UI["📱 Presentation Layer"]
        Login["Login/Register"]
        PassDash["Passenger Dashboard"]
        DrivDash["Driver Dashboard"]
    end

    subgraph BIZ["⚙️ Business Logic"]
        AuthSvc["AuthService"]
        RideSvc["RideService"]
        CarSvc["CarService"]
    end

    subgraph DAO["📊 Data Access"]
        UserDAO["UserDAO"]
        RideDAO["RideDAO"]
        CarDAO["CarDAO"]
        DbMgr["DatabaseManager"]
    end

    subgraph DATA["💾 Data Layer"]
        Users[(Users)]
        Rides[(Rides)]
        Cars[(Cars)]
    end

    Login --> AuthSvc
    PassDash --> RideSvc
    DrivDash --> RideSvc

    AuthSvc --> UserDAO
    RideSvc --> RideDAO
    RideSvc --> CarDAO
    CarSvc --> CarDAO

    UserDAO --> DbMgr
    RideDAO --> DbMgr
    CarDAO --> DbMgr

    DbMgr --> Users
    DbMgr --> Rides
    DbMgr --> Cars

    style UI fill:#e1f5ff
    style BIZ fill:#fff4e1
    style DAO fill:#e8f5e8
    style DATA fill:#f5f5f5
""",

    "4. Database Schema": """
graph LR
    Users["📋 USERS<br/>---<br/>user_id PK<br/>name<br/>email UK<br/>password_hash<br/>role<br/>created_at"]

    Cars["🚗 CARS<br/>---<br/>car_id PK<br/>driver_id FK<br/>license_plate UK<br/>brand<br/>model<br/>seating_capacity"]

    Rides["🛣️ RIDES<br/>---<br/>ride_id PK<br/>passenger_id FK<br/>driver_id FK<br/>car_id FK<br/>origin<br/>destination<br/>scheduled_time<br/>seats_needed<br/>price_estimate<br/>status"]

    Users ---|1:N| Cars
    Users ---|1:N| Rides
    Cars ---|1:N| Rides

    style Users fill:#e1f5ff
    style Cars fill:#fff4e1
    style Rides fill:#e8f5e8
"""
}

def main():
    print("\n" + "="*60)
    print("TeeTime - Mermaid Diagrams")
    print("="*60 + "\n")

    for title, diagram in diagrams.items():
        print(f"\n{title}")
        print("-" * 60)
        print(diagram)
        print()

if __name__ == "__main__":
    main()
