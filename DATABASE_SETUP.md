# PostgreSQL Database Setup

This project now uses PostgreSQL instead of MySQL for improved performance and advanced features.

## Prerequisites

1. Install PostgreSQL on your system:
   - **macOS**: `brew install postgresql`
   - **Ubuntu/Debian**: `sudo apt-get install postgresql postgresql-contrib`
   - **Windows**: Download from https://www.postgresql.org/download/

2. Start PostgreSQL service:
   - **macOS**: `brew services start postgresql`
   - **Ubuntu/Debian**: `sudo systemctl start postgresql`
   - **Windows**: Use PostgreSQL Service Manager

## Database Setup

1. **Create the main database:**
   ```sql
   createdb training_tracker
   ```

2. **Create the test database:**
   ```sql
   createdb training_tracker_test
   ```

3. **Create a PostgreSQL user (optional):**
   ```sql
   psql postgres
   CREATE USER training_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE training_tracker TO training_user;
   GRANT ALL PRIVILEGES ON DATABASE training_tracker_test TO training_user;
   \q
   ```

## Configuration

The application is configured to connect to PostgreSQL with these default settings:

- **Host**: localhost
- **Port**: 5432
- **Database**: training_tracker
- **Username**: postgres
- **Password**: postgres

To customize these settings, update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/training_tracker
    username: your_username
    password: your_password
```

## PostgreSQL Advantages for Training Tracker

1. **JSONB Support**: Perfect for storing complex workout data and progression algorithms
2. **Advanced Indexing**: Better performance for time-series workout data
3. **Temporal Data Types**: Native support for timestamps with time zones
4. **Window Functions**: Ideal for calculating workout progressions and trends
5. **Full-Text Search**: Better search capabilities for exercises and notes
6. **Better Concurrency**: Superior handling of concurrent workout updates

## Running the Application

1. Ensure PostgreSQL is running
2. Run the application: `mvn spring-boot:run`
3. The schema will be automatically created on first run

## Running Tests

Tests use a separate test database (`training_tracker_test`) and will automatically create/drop the schema for each test run.

```bash
mvn test
```
