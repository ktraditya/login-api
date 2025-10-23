# Curl API Proxy

A Spring Boot REST API service that exposes curl commands as REST endpoints. The service accepts payloads with parameters and executes curl commands in the backend, returning structured JSON responses.

## Features

- **Environment-based URL routing**: Supports test, qa, and qap1 environments
- **Asynchronous execution**: Uses CompletableFuture for non-blocking curl command execution
- **Security validation**: Input sanitization and validation using Bean Validation
- **Flexible endpoints**: Support for GET, POST, and raw curl command execution
- **Comprehensive logging**: SLF4J logging for debugging and monitoring
- **Health checks**: Built-in health endpoint for monitoring
- **Cross-origin support**: CORS enabled for web application integration

## Prerequisites

- **Java 21 LTS** (OpenJDK or Oracle JDK)
- **Maven 3.6+**
- **curl** command-line tool (must be available in system PATH)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd login-api
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file directly:

```bash
java -jar target/curl-api-proxy-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### 3. Verify Installation

Check the health endpoint:
```bash
curl http://localhost:8080/api/curl/health
```

Expected response: `"Curl API Proxy is running"`

## API Endpoints

### 1. Execute Environment-based Request

**POST** `/api/curl/execute`

Executes a curl command based on environment and SMID.

**Request Body:**
```json
{
    "smid": "12345",
    "env": "test"
}
```

**Response:**
```json
{
    "success": true,
    "output": "API response data...",
    "errorOutput": "",
    "exitCode": 0,
    "executionTimeMs": 1245
}
```

**Supported Environments:**
- `test`: https://api-test.example.com
- `qa`: https://api-qa.example.com
- `qap1`: https://api-qap1.example.com

### 2. Execute Raw Curl Command

**POST** `/api/curl/execute-raw`

Executes a raw curl command with custom URL and parameters.

**Request Body:**
```json
{
    "url": "https://api.example.com/data",
    "parameters": "-X GET -H 'Authorization: Bearer token123'"
}
```

### 3. Simple GET Request

**POST** `/api/curl/get`

**Parameters:**
- `url` (required): Target URL
- `headers` (optional): Additional headers

**Example:**
```bash
curl -X POST "http://localhost:8080/api/curl/get?url=https://httpbin.org/get&headers=-H 'User-Agent: MyApp'"
```

### 4. Simple POST Request

**POST** `/api/curl/post`

**Parameters:**
- `url` (required): Target URL
- `data` (optional): POST data
- `headers` (optional): Additional headers

**Example:**
```bash
curl -X POST "http://localhost:8080/api/curl/post" \
  -d "url=https://httpbin.org/post" \
  -d "data={\"key\":\"value\"}" \
  -d "headers=-H 'Content-Type: application/json'"
```

### 5. Health Check

**GET** `/api/curl/health`

Returns the service status.

## Building the Project

### Development Build

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Create Production JAR

```bash
mvn clean package
```

### Skip Tests During Build

```bash
mvn clean package -DskipTests
```

### Run with Specific Profile

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Testing the API

### Using curl

1. **Test health endpoint:**
```bash
curl -X GET http://localhost:8080/api/curl/health
```

2. **Test environment-based request:**
```bash
curl -X POST http://localhost:8080/api/curl/execute \
  -H "Content-Type: application/json" \
  -d '{"smid": "12345", "env": "test"}'
```

3. **Test raw curl execution:**
```bash
curl -X POST http://localhost:8080/api/curl/execute-raw \
  -H "Content-Type: application/json" \
  -d '{"url": "https://httpbin.org/get", "parameters": "-X GET"}'
```

### Using Postman

Import the following examples into Postman:

**Environment Request:**
- Method: POST
- URL: `http://localhost:8080/api/curl/execute`
- Headers: `Content-Type: application/json`
- Body: `{"smid": "12345", "env": "test"}`

**Raw Curl Request:**
- Method: POST
- URL: `http://localhost:8080/api/curl/execute-raw`
- Headers: `Content-Type: application/json`
- Body: `{"url": "https://httpbin.org/json", "parameters": "-X GET -H 'Accept: application/json'"}`

## Configuration

### Application Properties

Create `src/main/resources/application.properties`:

```properties
# Server configuration
server.port=8080
server.servlet.context-path=/

# Logging configuration
logging.level.com.example.curlapiproxy=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Actuator endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

### Environment-specific Configuration

For different environments, create:
- `application-dev.properties`
- `application-test.properties`
- `application-prod.properties`

## Security Considerations

1. **Input Validation**: All inputs are validated using Bean Validation annotations
2. **Command Injection Prevention**: Parameters are sanitized before execution
3. **Timeout Protection**: Commands have a 30-second timeout limit
4. **Error Handling**: Sensitive information is not exposed in error responses

## Monitoring and Logging

### Actuator Endpoints

- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`

### Log Files

Logs are output to the console by default. Configure file logging in `application.properties`:

```properties
logging.file.name=logs/curl-api-proxy.log
logging.level.root=INFO
logging.level.com.example.curlapiproxy=DEBUG
```

## Troubleshooting

### Common Issues

1. **"curl command not found"**
   - Ensure curl is installed and available in system PATH
   - Windows: Install curl or use Git Bash
   - Linux/Mac: `sudo apt-get install curl` or `brew install curl`

2. **Connection timeout errors**
   - Check target URL accessibility
   - Verify network connectivity
   - Check firewall settings

3. **Invalid environment error**
   - Ensure environment is one of: test, qa, qap1
   - Check case sensitivity (use lowercase)

4. **JSON parsing errors**
   - Validate JSON format in request body
   - Ensure Content-Type header is set correctly

### Debug Mode

Enable debug logging:
```properties
logging.level.com.example.curlapiproxy=DEBUG
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/example/curlapiproxy/
│   │   ├── CurlApiProxyApplication.java
│   │   ├── controller/
│   │   │   └── CurlController.java
│   │   ├── model/
│   │   │   ├── ApiRequest.java
│   │   │   ├── CurlRequest.java
│   │   │   └── CurlResponse.java
│   │   └── service/
│   │       └── CurlExecutorService.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/curlapiproxy/
        └── [test files]
```

### Adding New Features

1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement changes following existing patterns
3. Add unit tests
4. Update documentation
5. Submit pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review existing issues in the repository
3. Create a new issue with detailed information