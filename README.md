# Weather API Service

A Spring Boot RESTful API that aggregates weather data from multiple sources, providing current weather, forecasts, and location search. The API includes caching, rate limiting, error handling, health checks, and API documentation.

---

## Features

- Get current weather for a location
- Get weather forecast for multiple days
- Search for locations
- Response caching with TTL to reduce redundant API calls
- Rate limiting to prevent abuse
- Comprehensive error handling
- Health check endpoint
- Logging of incoming requests and errors
- API documentation with OpenAPI / Swagger UI

---

## Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/weather/current?location={city}` | GET | Returns current weather for the specified city |
| `/weather/forecast?location={city}&days={n}` | GET | Returns weather forecast for the next `n` days |
| `/locations/search?q={query}` | GET | Search for locations matching the query |
| `/health` | GET | Returns the health status of the service |

---

## Setup Instructions

### Prerequisites

- Java 21 (JDK)
- Maven 3.8+
- Git
- Internet connection (for API calls)
- Postman/Swwagger UI to test endpoints

### Clone the Repository

git clone <(https://github.com/bharathb-r/WeatherApi.git)>
cd weather-api

**Configure API Keys**

The project uses OpenWeather and WeatherAPI.
You need to create your own API keys and replace the placeholders in:
src/main/resources/application.properties
# Replace these with your actual keys
openweather.api.key=YOUR_OPENWEATHER_KEY
weatherapi.api.key=YOUR_WEATHERAPI_KEY
⚠️ Important: Without valid API keys, the endpoints will return errors.

Build the Project
mvn clean install
This will download dependencies and compile the project.

Run the Application
mvn spring-boot:run
Or run the main class directly in your IDE:
WeatherApiApplication.java

The service will start on http://localhost:8080 by default.

API Documentation (Swagger UI)
Once the application is running, you can access Swagger UI for interactive API docs:
http://localhost:8080/swagger-ui.html
Testing with Postman
You can use Postman to test all endpoints. Example requests:

Current Weather:
GET http://localhost:8080/weather/current?location=Chennai

Forecast:
GET http://localhost:8080/weather/forecast?location=Chennai&days=5

Location Search:
GET http://localhost:8080/locations/search?q=Chennai

Health Check:
GET http://localhost:8080/health
Make sure your API keys are configured correctly in application.properties.

Approach / Notes
Aggregates data from multiple weather providers using Spring WebClient
Implements caching with Caffeine to reduce redundant API calls
Uses Bucket4j for rate limiting
Provides detailed error handling and proper HTTP status codes
Logging is enabled for all requests and errors
Fully documented using Springdoc OpenAPI
Designed for easy expansion to additional weather providers
