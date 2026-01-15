# E-commerce Multi-Vendor Backend

A comprehensive Spring Boot-based e-commerce platform with multi-vendor support, AI-powered features, and Azure cloud deployment.

## 🚀 Features

### Core E-commerce Features
- **Multi-vendor marketplace** with seller management
- **Product catalog** with categories, reviews, and ratings
- **Shopping cart** and wishlist functionality
- **Order management** with status tracking
- **Payment integration** (Stripe & Razorpay)
- **Coupon and deal** management
- **User authentication** with JWT tokens
- **Admin dashboard** with revenue analytics

### AI-Powered Features
- **AI Chatbot** for customer support
- **AI Product Recommendations**
- **Product Details Bot** for enhanced product information
- **Gemini AI Integration** for intelligent features

### Technical Features
- **Spring Boot 3.3.2** with Java 17
- **Spring Security** with JWT authentication
- **Spring Data JPA** with MySQL/SQL Server support
- **RESTful APIs** with OpenAPI documentation
- **Email notifications** with Spring Mail
- **Dockerized** application
- **Azure Container Apps** deployment
- **Terraform** infrastructure as code
- **GitHub Actions** CI/CD pipeline

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │────│   Spring Boot   │────│   Database      │
│   (React)       │    │   Backend API   │    │   (SQL Server)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                       ┌─────────────────┐
                       │   Azure Cloud   │
                       │  Container Apps │
                       └─────────────────┘
```

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.3.2, Spring Security, Spring Data JPA
- **Database**: MySQL / SQL Server
- **Authentication**: JWT (JSON Web Tokens)
- **Payment**: Stripe, Razorpay
- **AI**: Spring AI with Gemini integration
- **Documentation**: SpringDoc OpenAPI
- **Containerization**: Docker
- **Cloud**: Azure Container Apps, Azure SQL Database
- **Infrastructure**: Terraform
- **CI/CD**: GitHub Actions

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- Docker
- Azure CLI (for deployment)
- Terraform (for infrastructure)

## 🚀 Quick Start

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/pankajdahiya008/final-backend.git
   cd final-backend
   ```

2. **Configure application properties**
   ```bash
   cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
   ```
   Update the database and API credentials in the properties file.

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Actuator: http://localhost:8080/actuator

### Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t ecommerce-backend .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=dev \
     -e SPRING_DATASOURCE_URL=your_db_url \
     -e SPRING_DATASOURCE_USERNAME=your_username \
     -e SPRING_DATASOURCE_PASSWORD=your_password \
     ecommerce-backend
   ```

### Azure Cloud Deployment

1. **Configure Terraform variables**
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   ```
   Update the variables with your Azure and API credentials.

2. **Deploy infrastructure**
   ```bash
   terraform init
   terraform plan
   terraform apply
   ```

3. **Setup GitHub Secrets**
   Configure the following secrets in your GitHub repository:
   - `AZURE_CREDENTIALS`
   - `ACR_NAME`
   - `CONTAINER_APP_NAME`
   - `RESOURCE_GROUP`

4. **Deploy via GitHub Actions**
   Push to the `main` branch to trigger automatic deployment.

## 📚 API Documentation

Once the application is running, visit:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Key API Endpoints

#### Authentication
- `POST /auth/signup` - User registration
- `POST /auth/signin` - User login
- `POST /auth/reset-password` - Password reset

#### Products
- `GET /api/products` - List products
- `POST /api/products` - Create product (seller)
- `GET /api/products/{id}` - Get product details
- `PUT /api/products/{id}` - Update product (seller)

#### Orders
- `POST /api/orders` - Create order
- `GET /api/orders/user` - Get user orders
- `PUT /api/orders/{id}/status` - Update order status

#### Cart
- `GET /api/cart` - Get user cart
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/item/{id}` - Update cart item

## 🔧 Configuration

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `SPRING_PROFILES_ACTIVE` | Active profile (dev/prod) | Yes |
| `SPRING_DATASOURCE_URL` | Database connection URL | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Yes |
| `RAZORPAY_API_KEY` | Razorpay API key | Yes |
| `RAZORPAY_API_SECRET` | Razorpay API secret | Yes |
| `STRIPE_API_KEY` | Stripe API key | Yes |
| `MAIL_USERNAME` | Email username | Yes |
| `MAIL_PASSWORD` | Email password | Yes |
| `GEMINI_API_KEY` | Gemini AI API key | Yes |

### Database Schema

The application uses JPA with automatic schema generation. Key entities:
- `User` - User accounts and profiles
- `Seller` - Vendor information
- `Product` - Product catalog
- `Order` - Order management
- `Cart` - Shopping cart
- `Review` - Product reviews
- `Category` - Product categories

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run tests with coverage
./mvnw test jacoco:report
```

## 📦 Project Structure

```
src/
├── main/
│   ├── java/com/zosh/
│   │   ├── ai/                 # AI-powered features
│   │   ├── config/             # Configuration classes
│   │   ├── controller/         # REST controllers
│   │   ├── domain/             # Enums and constants
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── exception/          # Exception handling
│   │   ├── mapper/             # Entity-DTO mappers
│   │   ├── model/              # JPA entities
│   │   ├── repository/         # Data repositories
│   │   ├── request/            # Request DTOs
│   │   ├── response/           # Response DTOs
│   │   ├── service/            # Business logic
│   │   └── utils/              # Utility classes
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-local.properties
└── test/                       # Test classes
```

## 🔒 Security

- **JWT Authentication** with refresh tokens
- **Role-based access control** (Admin, Seller, Customer)
- **Password encryption** with BCrypt
- **CORS configuration** for cross-origin requests
- **Input validation** with Bean Validation
- **SQL injection protection** with JPA

## 🚀 Deployment

### GitHub Actions Workflow

The repository includes a complete CI/CD pipeline:

1. **Build** - Compiles and tests the application
2. **Docker Build** - Creates container image
3. **Push to ACR** - Uploads to Azure Container Registry
4. **Deploy** - Updates Azure Container App

### Infrastructure as Code

Terraform configuration includes:
- Azure Resource Group
- Virtual Network with subnets
- Azure Container Registry
- Azure SQL Database with private endpoint
- Azure Container Apps environment
- Role assignments and security configurations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 👥 Authors

- **Pankaj Dahiya** - *Initial work* - [@pankajdahiya008](https://github.com/pankajdahiya008)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Azure team for the cloud platform
- Open source community for the amazing libraries

## 📞 Support

For support, email pankajofficialemail@gmail.com or create an issue in the repository.

---

⭐ Star this repository if you find it helpful!