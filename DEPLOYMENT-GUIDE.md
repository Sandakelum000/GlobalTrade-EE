# GlobalTrade Logistics - Deployment Guide

## 1. Project Requirements

The following software is required:

- Java JDK 17
- IntelliJ IDEA
- Maven
- MySQL 
- Payara Server 6.2025.11
- ngrok
- PayHere 

---

## 2. Open the Project

Open the `GlobalTrade-Logistics` project in IntelliJ IDEA.

The project contains the following Maven modules:

```text
GlobalTrade-Logistics
├── globaltrade-core
├── globaltrade-ejb
├── globaltrade-web
├── globaltrade-ear
└── pom.xml

```
---

## 3. Java Configuration

The project requires **JDK 17**.

In IntelliJ IDEA, configure the project SDK:

```text
File → Project Structure → Project
```
---

## 4. MySQL Database Setup

The project includes a `globaltrade.sql` file that contains the required database creation, tables, and initial data.

Make sure MySQL is running, then execute the SQL file.

From the directory containing `globaltrade.sql`, run:

```bash
mysql -u root -p < globaltrade.sql
```

---
---

## 5. PayHere Configuration

PayHere credentials are configured in:

```text
globaltrade-core/src/main/java/com/globaltrade/logistics/core/util/PayHereUtil.java
```
Open `PayHereUtil.java` and update the PayHere credentials:

```java
public class PayHereUtil {

    private static final String MERCHANT_ID = "YOUR_MERCHANT_ID";
    private static final String MERCHANT_SECRET = "YOUR_MERCHANT_SECRET";

    // ...
}
```

## 6. Configure ngrok Public URL

The application requires a public URL for payment-related callbacks.

Start ngrok on Payara's HTTP port:

```bash
ngrok http 8080
```
Then open:
```text
globaltrade-ejb/src/main/resources/app.properties
```
Then Set the app.public.url property to the ngrok HTTPS URL:
```properties
app.public.url=https://YOUR-NGROK-URL/globaltrade/logistics
```

---

## 7. Configure JWT Access Token Expiration

The JWT access token expiration time can be changed in:

```text
globaltrade-ejb/src/main/resources/META-INF/microprofile-config.properties
```
Find the following property:
```properties
jwt.access-token-expiration-seconds=3200
```

---

## 8. Sample User Credentials

The following sample accounts are available for testing the application.

| Role              | Username   | Password       |
|-------------------|------------|----------------|
| Admin             | `admin`    | `@Admin123456` |
| Logistics Manager | `manager`  | `@Manager123`  |
| Customer          | `customer` | `@Cus123456`   |

> **Note:** These credentials are provided for testing purposes.

---

## 9. Final Deployment Checklist

Before running the application, make sure the following are completed:

- [ ] JDK 17 is installed and configured.
- [ ] MySQL is running.
- [ ] `globaltrade.sql` has been executed successfully.
- [ ] Payara Server 6.2025.11 is installed and running.
- [ ] MySQL JDBC driver is configured in Payara.
- [ ] Payara JDBC Connection Pool is configured.
- [ ] Payara JDBC Resource is configured correctly.
- [ ] ngrok is running on port `8080`.
- [ ] The ngrok URL is configured in `app.properties`.
- [ ] JWT expiration is configured in `microprofile-config.properties`.
- [ ] PayHere credentials are configured.
- [ ] The project builds successfully using Maven.
- [ ] The EAR file is deployed to Payara.
- [ ] Login works with the provided test accounts.
- [ ] Role-based functionality has been tested.
- [ ] REST APIs have been tested using Postman.
- [ ] Payment functionality has been tested.

---

## 10. Build and Deploy

Build the project from the project root:

```bash
mvn clean package