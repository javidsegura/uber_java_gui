1. Simplified Title & Description (200–300 words, Passenger + Driver focus)
Project Title: TeeTime – Campus Ride Sharing Desktop App
Description (≈230 words)
 TeeTime is a Java-based desktop ride-sharing application designed for university communities. The system connects two core user types: Passengers, who need short rides around campus or nearby areas, and Drivers, who own registered cars and want to share rides and split fuel costs.
Using a JavaFX interface, Passengers can create an account, log in, and request rides by specifying origin, destination, time, and number of seats. Drivers can register as users, add their cars, set their availability, and browse a list of open ride requests. When a Driver accepts a request, the system creates a Ride that links Passenger, Driver, and Car, and updates its status from PENDING to CONFIRMED and later COMPLETED.
The application follows Object-Oriented Programming principles with a clear domain model: an abstract User class extended by Passenger and Driver, a Ride class that composes Passenger, Driver, and Car, and service classes such as AuthService and RideService to handle login and ride assignment. Data is stored in a relational database with tables for users, cars, and rides, ensuring persistence across sessions.
Initial file I/O support will focus on exporting a user’s ride history to CSV (for example, all rides completed by a given Driver or requested by a Passenger). Exception handling and unit tests will cover core scenarios such as invalid login, duplicate ride acceptance, and ride capacity validation, ensuring that the core Passenger–Driver matching workflow is robust.

2. Simplified Significance & Innovation (focused version)
Significance & Problem
 On many campuses, students rely on informal WhatsApp groups or word-of-mouth to arrange rides. This is unreliable, hard to track, and offers no record of who is driving whom or when. TeeTime provides a simple, centralized way to match Passengers and Drivers within the university community and keep a record of rides.
What makes it different (even in simplified form)
Closed community: Only university email accounts can register (e.g., @student.ie.edu), increasing trust and safety.


Role flexibility: The same account can act as Passenger, Driver, or both; users can switch role from within the app.


Desktop JavaFX app: Runs on lab/dorm computers without needing a mobile deployment or app store.


Target audience (kept simple)
Primary: Students and staff who either need rides (Passenger role) or have cars and want to share trips (Driver role).


Future / light mention: A simple Admin user can later be added to monitor accounts and data, but phase 1 focuses on Passenger–Driver interaction.



3. User Analysis (minimal but meets instructions)
You need 3 user types in the document, but implementation can focus on 2. So:
3.1 Primary Users
Passenger


Characteristics: Students/staff who need rides around campus or nearby neighborhoods. Comfortable with basic desktop software.


Key Needs:


Easy registration and login


Search/create ride requests (origin, destination, time, seats)


See current and past rides


Driver


Characteristics: Students/staff who own a car and want to share rides and recover part of fuel/parking costs. Same technical level as Passengers.


Key Needs:


Register as a Driver and add one or more Cars


View pending ride requests and accept or decline them


See upcoming and completed rides


Admin (minimal, mostly conceptual in phase 1)


Characteristics: Campus transportation admin or system admin.


Key Needs (for later phases):


View users and rides


Deactivate suspicious accounts, lightly monitor usage


3.2 Multi-User / Auth & Interactions (simplified)
Authentication:


Login with email + password; email domain validation (e.g., must contain @student.ie.edu or @ie.edu).


Authorization levels (phase 1):


Passenger: create ride requests, cancel pending requests, view their rides.


Driver: everything a Passenger can do + register cars, view open requests, accept rides.


Admin (future): read-only view of users and rides.


Interaction flow (core scenario):


Passenger creates a ride request → appears in Drivers’ “Available Requests” view → Driver accepts → system assigns Driver and Car to the Ride and changes its status.



4. Simplified Scope & Complexity (Phase 1 Core)
We keep only what’s needed to satisfy the course requirements while focusing on Passenger–Driver matching.
4.1 Planned Core Java Classes (10+)
Minimum set you can actually implement now:
User (abstract)


Passenger (extends User)


Driver (extends User)


Car


Ride


RideStatus (enum: PENDING, CONFIRMED, COMPLETED, CANCELLED)


AuthService (login, registration, role switching)


RideService (create request, list requests, accept ride, mark complete)


DatabaseManager or UserDAO / RideDAO (persistence layer)


CapacityExceededException (custom example)


InvalidLoginException (custom example)


Ratings, Payments, complex analytics, etc. can be added later as extra classes, but this list already meets the “10+ classes with OOP” requirement.
4.2 Database (3–5 tables, minimal)
Start with:
users (id, name, email, password_hash, role)


cars (id, driver_id, plate, brand, seats)


rides (id, passenger_id, driver_id, car_id, origin, destination, time, status, price_estimate)


This is enough for phase 1. You can later add payments and ratings tables if you extend the project.
4.3 GUI (JavaFX, minimal screens)
Login / Registration Screen


Email, password, name, choose initial role (Passenger/Driver/Both).


Passenger Dashboard


Form to create a ride request.


Table to show “My Requests / My Rides”.


Driver Dashboard


“My Cars” section (add, edit, delete cars).


“Open Requests” table with “Accept” button.


“My Accepted Rides” table.


4.4 File I/O (simple but enough)
Export all rides for a given user (Passenger or Driver) to CSV.


(Optional) Import a small CSV of demo users/cars for testing.


4.5 Exception Handling & Unit Tests
Exceptions:


InvalidLoginException (wrong credentials)


CapacityExceededException (Passenger requests more seats than car capacity)


Generic DB connection exception handling in DatabaseManager.


Unit tests (JUnit):


Fare/price estimation (even simple distance × base rate).


Ride assignment logic (after a Driver accepts, ride has correct Driver/Car and status).


Validation rules (no ride request with empty origin/destination, seats > 0, etc.).
