# Rate Limiter with Spring Boot and Redis

![System Architecture](docs/system-design.png)

This project implements a rate limiting system for REST APIs using Spring Boot and Redis. The goal is to limit the number of requests a client can make within a configurable time window.

## Features

- Limits the number of requests per IP in a configurable time window.
- Uses Redis to store request counters.
- Returns HTTP 429 (Too Many Requests) when the limit is exceeded.
- Automated tests with MockMvc.

## Project Structure

- `src/main/java/com/meurer/ratelimiter/`: Main application code.
- `src/main/java/com/meurer/ratelimiter/config/`: Rate limiter configurations.
- `src/main/java/com/meurer/ratelimiter/controller/`: Test controller.
- `src/test/java/com/meurer/ratelimiter/`: Automated tests.
- `docker-compose.yml`: Redis configuration via Docker.

## Prerequisites

- Java 17+
- Maven
- Docker (to run Redis)

## Step-by-step to run the application

1. **Start Redis with Docker**

   In the project directory, run:

   ```sh
   docker-compose up -d
   ```

2. **Configure environment variables (if necessary)**

   - The `application.properties` file is already configured to connect to the default Redis (`localhost:6379`).
   - If you change the Redis configuration, update the file at `src/main/resources/application.properties`.

3. **Build and run the Spring Boot application**

   In the project directory, run:

   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

4. **Test the API**

   - Access the test endpoint: `http://localhost:8080/test`
   - Make requests to verify the rate limiter functionality.

5. **Run automated tests**

   ```sh
   mvn test
   ```

## How to run the application with Redis

### 1. Start Redis with Docker

In the project directory, run:

```sh
docker-compose up -d
```

## Running with Docker on WSL (Windows Subsystem for Linux)

If you are using Docker installed on WSL:

1. Open your WSL terminal (e.g., Ubuntu).
2. Navigate to your project directory (where the `docker-compose.yml` file is located). For example:
   ```sh
   cd /path/to/your/project
   ```
3. Make sure Docker Desktop is running on Windows.
4. Run the following command to start Redis:
   ```sh
   docker-compose up -d
   ```

If you encounter permission issues, try running the command with sudo:
```sh
sudo docker-compose up -d
```

Now you can proceed with the rest of the steps (build, run, test) as described above.
