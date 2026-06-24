# BFHL REST API — Spring Boot

Chitkara University | Java API Round | 24 June

---

## Quick Start (local)

```bash
# 1. Set your identity as environment variables (or edit application.properties)
export APP_USER_NAME=your_name      # e.g. john_doe  (lowercase, underscored)
export APP_USER_DOB=ddmmyyyy        # e.g. 17091999
export APP_USER_EMAIL=you@email.com
export APP_USER_ROLL=ROLLNO123

# 2. Build & run
mvn clean package -DskipTests
java -jar target/bfhl-1.0.0.jar

# 3. Test
curl -X POST http://localhost:8080/bfhl \
  -H "Content-Type: application/json" \
  -d '{"data":["a","1","334","4","R","$"]}'
```

---

## Run Tests

```bash
mvn test
```

12 test cases cover:
- Example A / B / C from the spec
- Empty array edge case
- Validation errors (missing field, malformed JSON)
- Service-layer unit tests (odd/even, sum, alphabets, special chars, concat_string)

---

## Deploy to Render

1. Push this project to GitHub.
2. Go to [render.com](https://render.com) → New Web Service → connect your repo.
3. Build command: `mvn clean package -DskipTests`
4. Start command: `java -jar target/bfhl-1.0.0.jar`
5. Add environment variables: `APP_USER_NAME`, `APP_USER_DOB`, `APP_USER_EMAIL`, `APP_USER_ROLL`
6. Your endpoint will be: `https://<your-service>.onrender.com/bfhl`

## Deploy to Railway

```bash
railway login
railway init
railway up
```
Set the same four env vars in the Railway dashboard.

---

## API Reference

**POST** `/bfhl`

### Request
```json
{ "data": ["a", "1", "334", "4", "R", "$"] }
```

### Response (200 OK)
```json
{
  "is_success": true,
  "user_id": "john_doe_17091999",
  "email": "john@xyz.com",
  "roll_number": "ABCD123",
  "odd_numbers": ["1"],
  "even_numbers": ["334", "4"],
  "alphabets": ["A", "R"],
  "special_characters": ["$"],
  "sum": "339",
  "concat_string": "Ra"
}
```

### concat_string logic
1. Collect every individual alphabetical character from the input in order.
2. Reverse the list.
3. Apply alternating caps: index 0 → UPPER, index 1 → lower, …

Examples:
| Input chars | Reversed | concat_string |
|---|---|---|
| a, R | R, a | Ra |
| a, y, b | b, y, a | ByA |
| A, A,B,C,D, D,O,E | E,O,D,D,C,B,A,A | EoDdCbAa |

---

## Project Structure

```
src/
├── main/java/com/chitkara/bfhl/
│   ├── BfhlApplication.java          ← Spring Boot entry point
│   ├── controller/BfhlController.java ← POST /bfhl
│   ├── service/
│   │   ├── BfhlService.java          ← Interface
│   │   └── BfhlServiceImpl.java      ← Business logic
│   ├── dto/
│   │   ├── BfhlRequestDto.java       ← Request DTO
│   │   └── BfhlResponseDto.java      ← Response DTO
│   └── exception/
│       └── GlobalExceptionHandler.java ← 400 / 500 error handling
└── test/java/com/chitkara/bfhl/
    └── BfhlApplicationTests.java     ← 12 test cases
```
