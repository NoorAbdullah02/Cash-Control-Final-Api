# 💰 CASH CONTROL FINAL API

<div align="center">


### 🚀 Empowering Financial Freedom Through Seamless Control

*A modern, secure, and scalable personal finance management API*

[![last commit](https://img.shields.io/github/last-commit/NoorAbdullah02/Cash-Control-Final-API?style=for-the-badge&color=brightgreen)](https://github.com/NoorAbdullah02/Cash-Control-Final-API/commits/main)
[![Java](https://img.shields.io/github/languages/top/NoorAbdullah02/Cash-Control-Final-API?style=for-the-badge&color=orange)](https://github.com/NoorAbdullah02/Cash-Control-Final-API)
[![Languages](https://img.shields.io/github/languages/count/NoorAbdullah02/Cash-Control-Final-API?style=for-the-badge&color=blue)](https://github.com/NoorAbdullah02/Cash-Control-Final-API)
[![License](https://img.shields.io/github/license/NoorAbdullah02/Cash-Control-Final-API?style=for-the-badge&color=purple)](LICENSE)

</div>

---

## 🎯 **Overview**

Cash Control Final API is a cutting-edge backend solution that revolutionizes personal finance management. Built with enterprise-grade technologies including Spring Boot, Docker, and JWT authentication, it provides a robust foundation for modern financial applications.

### 🌟 **Key Highlights**

<table>
<tr>
<td width="33%" align="center">
  <h3>🛡️ Enterprise Security</h3>
  <p>JWT-based authentication, role-based access control, and data encryption ensure your financial data stays protected</p>
</td>
<td width="33%" align="center">
  <h3>⚡ High Performance</h3>
  <p>Optimized database queries, caching mechanisms, and efficient API design for lightning-fast responses</p>
</td>
<td width="33%" align="center">
  <h3>🐳 Cloud Ready</h3>
  <p>Fully containerized with Docker, supporting microservices architecture and easy deployment to any cloud platform</p>
</td>
</tr>
</table>

---

## 🏗️ **Architecture & Tech Stack**

<div align="center">

### **Backend Technologies**
![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

### **Database & Storage**
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

### **DevOps & Tools**
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

</div>

---

## ✨ **Features**

### 🔐 **Authentication & Security**
- **JWT Token Management**: Secure, stateless authentication
- **Role-Based Access Control**: Admin and user role separation
- **Password Encryption**: BCrypt hashing for secure password storage
- **Session Management**: Automatic token refresh and expiration handling

### 💼 **Financial Management**
- **Transaction Tracking**: Record income and expenses with detailed categorization
- **Category Management**: Custom categories with icons and colors
- **Budget Planning**: Set and monitor spending limits
- **Financial Analytics**: Comprehensive reports and insights

### 📊 **Reporting & Export**
- **Excel Export**: Download financial data in Excel format
- **Custom Date Ranges**: Filter transactions by specific periods
- **Visual Charts**: Generate spending patterns and trends
- **Email Reports**: Automated monthly/weekly financial summaries

### 🔔 **Notifications & Communication**
- **Email Integration**: Password reset, welcome emails, and notifications
- **Real-time Alerts**: Budget limit warnings and transaction confirmations
- **SMTP Configuration**: Flexible email service integration

---

## 🚀 **Quick Start Guide**

### 📋 **Prerequisites**

Before you begin, ensure you have the following installed:

```bash
☑️ Java 21+ (OpenJDK recommended)
☑️ Maven 3.8+
☑️ Docker & Docker Compose
☑️ MySQL 8.0+ (also  use Docker)
☑️ Git
```

### ⚡ **Installation**

1. **Clone the Repository**
   ```bash
   git clone https://github.com/NoorAbdullah02/Cash-Control-Final-API.git
   cd Cash-Control-Final-API
   ```

2. **Environment Configuration**
   ```bash
   # Copy environment template
   cp .env.example .env
   
   # Edit configuration file
   nano .env
   ```

3. **Docker Deployment (Recommended)**
   ```bash
   # Build and start all services
   docker-compose up -d --build
   
   # Check service status
   docker-compose ps
   ```

4. **Manual Installation**
   ```bash
   # Install dependencies
   mvn clean install
   
   # Run the application
   mvn spring-boot:run
   ```

### 🌐 **Access Points**

| Service | URL | Description |
|---------|-----|-------------|
| **API Server** | `https://cash-control-final-api.onrender.com` | Main API endpoint |
| **Health Check** | `https://cash-control-final-api.onrender.com/api/v1.0/status` | Application status |

---

## 📖 **API Documentation**

### 🔑 **Authentication Endpoints**

```http
POST https://cash-control-final.vercel.app/signup     # User registration
POST https://cash-control-final.vercel.app/login      # User login
```

### 👤 **User Management**

```http
GET    /api/users/profile   # Get user profile
PUT    /api/users/profile   # Update user profile
DELETE /api/users/account   # Delete user account
```

### 💰 **Transaction Management**

```http
GET    /api/transactions              # Get all transactions
POST   /api/transactions              # Create new transaction
GET    /api/transactions/{id}         # Get transaction by ID
PUT    /api/transactions/{id}         # Update transaction
DELETE /api/transactions/{id}         # Delete transaction
```

### 📈 **Analytics & Reports**

```http
GET /api/analytics/summary           # Financial summary
GET /api/analytics/monthly-report    # Monthly report
GET /api/export/excel               # Export to Excel
```

---

## 🐳 **Docker Configuration**

### **docker-compose.yml**
```yaml
version: '3.8'
services:
  cash-control-api:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=mysql
    depends_on:
      - mysql
    networks:
      - cash-control-network

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: cashcontrol
      MYSQL_USER: cashuser
      MYSQL_PASSWORD: cashpass
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - cash-control-network

volumes:
  mysql_data:

networks:
  cash-control-network:
    driver: bridge
```

---

## ⚙️ **Configuration**

### **Environment Variables**

Create a `.env` file in the root directory:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=cashcontrol
DB_USERNAME=cashuser
DB_PASSWORD=cashpass

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Application Configuration
APP_BASE_URL=http://localhost:8080
```

---

## 🧪 **Testing**

### **Run All Tests**
```bash
# Unit tests
mvn test

# Integration tests
mvn integration-test

# Generate test coverage report
mvn jacoco:report
```

### **API Testing with Postman**
Import the provided Postman collection:
1. Open Postman
2. Import → `postman/Cash-Control-API.postman_collection.json`
3. Set environment variables
4. Run the test suite

---

## 📦 **Deployment**

### **Production Deployment**

1. **Build Production Image**
   ```bash
   docker build -t cash-control-api:prod -f Dockerfile.prod .
   ```

2. **Deploy to Cloud**
   ```bash
   # AWS ECS
   aws ecs update-service --cluster production --service cash-control-api
   
   # Kubernetes
   kubectl apply -f k8s/
   ```

### **Environment-Specific Configurations**

- **Development**: `application-dev.yml`
- **Testing**: `application-test.yml`
- **Production**: `application-prod.yml`

---

## 📈 **Monitoring & Observability**

### **Health Checks**
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity
curl http://localhost:8080/actuator/health/db

# Disk space
curl http://localhost:8080/actuator/health/diskSpace
```

### **Metrics & Logging**
- **Micrometer**: Application metrics
- **Logback**: Structured logging
- **Actuator**: Production-ready features

---

## 🤝 **Contributing**

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### **Development Guidelines**
- Follow Java coding standards
- Write comprehensive tests
- Update documentation
- Use conventional commit messages

---

## 📄 **License**

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## 🆘 **Support & Contact**

<div align="center">

### **Need Help?**

[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-red?style=for-the-badge&logo=github)](https://github.com/NoorAbdullah02/Cash-Control-Final-API/issues)
[![Documentation](https://img.shields.io/badge/Read-Documentation-blue?style=for-the-badge&logo=gitbook)](https://github.com/NoorAbdullah02/Cash-Control-Final-API/wiki)

**Email**: [sheikhnoorabdullah02@gmail.com](mailto:sheikhnoorabdullah02@gmail.com)
**LinkedIn**: [Noor Abdullah](https://linkedin.com/in/noorabdullah02)

</div>

---

## 🙏 **Acknowledgments**

Special thanks to:
- Spring Boot community for the amazing framework
- Docker team for containerization excellence
- All contributors who helped improve this project

---

<div align="center">

### ⭐ **Star this repository if it helped you!**

![Footer](https://via.placeholder.com/800x100/1e3a8a/ffffff?text=Thank+You+for+Using+Cash+Control+API+💰)

</div>
