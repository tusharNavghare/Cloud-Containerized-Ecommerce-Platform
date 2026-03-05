# Cloud-Native-Containerized-Ecommerce-Platform

A scalable e-commerce backend built using a **Spring Boot microservices architecture**.
The project demonstrates modern backend development practices including **service discovery, inter-service communication, event-driven processing, containerization, and container orchestration** using Docker and Kubernetes.

The main goal is to **showcase cloud-native application design and deployment patterns**, rather than focusing solely on business logic implementation.

---

# Project Overview

This system simulates the backend of an e-commerce platform where different business capabilities are implemented as independent microservices.

* Each service is **independently deployable**
* Services communicate via **REST APIs and OpenFeign clients**
* Supports deployment using **Docker Compose** for local orchestration
* Supports deployment using **Kubernetes** (local or AWS EKS) for production-style orchestration

---

# Architecture

The application follows a **microservices architecture**. Core components include:

* **Auth Service** – Handles authentication and authorization with secure token-based mechanisms
* **Product Service** – Manages product catalog
* **Inventory Service** – Tracks product availability and stock levels
* **Order Service** – Handles order placement and processing
* **Discovery Server** – Service registry used only for Docker Compose deployments

Services communicate using **REST APIs, OpenFeign clients**, and **RabbitMQ for asynchronous event-driven communication**.

---

# System Architecture Flows

### Order Placement Flow (Main Flow)

1. Client places an order via the **Order Service**
2. Validates if products present in order actually exist and are active via feign client call to **Product Service** 
3. **Order Service** saves the order in a **pending state**
4. **Order Service** publishes an event to **RabbitMQ** to trigger inventory validation
5. **Inventory Service** consumes the event and checks stock availability
6. If stock is available:
   * Inventory is updated
   * Order status is updated to **confirmed**
7. If stock is not available:
   * Order status is updated to **failed**

### Authentication Flow

1. User logs in via the **Auth Service**
2. Credentials are validated
3. A secure authentication token is generated
4. Token is used to access protected APIs

### Product Management

1. Admin can create/update/retrieve products via **Product Service**
2. Product information is stored in the database

### Inventory Management

1. Inventory can be added or updated via **Inventory Service**
2. Inventory Service maintains product stock levels

**Note:** Only the **Order Placement flow** is described in detail; other flows are summarized for context.

---

# Tech Stack

## Backend

* Java
* Spring Boot
* Spring Cloud
* Spring Data JPA
* OpenFeign
* Event-driven asynchronous processing using RabbitMQ

## Infrastructure

* Docker
* Docker Compose
* Kubernetes

## Service Discovery

* Eureka Server (used in Docker Compose for service discovery)
* Kubernetes internal DNS (used in Kubernetes deployments)

## Database

* PostgreSQL

## Build Tool

* Maven

---

# Project Structure

```
.
├── docker
│   └── docker-compose.yml
├── kubernetes
│   ├── aws-eks
│   │   └── *.yaml
│   └── local
│       └── *.yaml
└── services
    ├── Auth-Service
    ├── Common-Config
    ├── Inventory-Service    
    ├── Order-Service
    ├── Product-Service
    └── Service-Registry
```

Each microservice is developed as an independent Spring Boot application.

---

# Running the Project

## 1. Run Using Docker Compose

```bash
docker-compose up --build
```

Services started:

* Discovery Server
* Auth Service
* Product Service
* Inventory Service
* Order Service
* Database containers

**Note:** Eureka service registry is used for service discovery in this deployment.

---

## 2. Run Using Kubernetes (Local or AWS EKS)

### Local Kubernetes

```bash
kubectl apply -f kubernetes/
```

Verify pods:

```bash
kubectl get pods
```

Expose a service locally:

```bash
kubectl port-forward service/<service-name> 8080:80
```

Example:

```bash
kubectl port-forward service/product-service 8080:80
```

### AWS EKS Deployment

1. Create an EKS cluster using AWS Management Console or `eksctl`
2. Configure `kubectl` to point to the EKS cluster
3. Apply Kubernetes manifests:

```bash
kubectl apply -f kubernetes/
```



**Note:** In Kubernetes/EKS, services communicate using **internal DNS**, so Eureka is not required.

---

# Environment Configuration

```bash
# Database
DB_URL=
DB_USERNAME=
DB_PASSWORD=

# Service Discovery
EUREKA_SERVER_URL=

# Messaging
RABBITMQ_DEFAULT_USER=
RABBITMQ_DEFAULT_PASS=
RABBITMQ_HOST=
RABBITMQ_PORT=

# Auth
JWT_SECRET=

# Cache
REDIS_HOST=
REDIS_PORT=

# Spring Boot
SPRING_APPLICATION_NAME=
```

Can be configured via **Kubernetes ConfigMaps/Secrets** or **environment variables in Docker Compose**.

---

# Example API Endpoints

**Product Service**

```http
GET /products
POST /products
```

**Inventory Service**

```http
GET /inventory/{productId}
```

**Order Service**

```http
POST /orders
```

---

# Learning Objectives

This project demonstrates:

* Microservices architecture using Spring Boot
* Service discovery using Eureka
* Inter-service communication using OpenFeign
* Event-driven asynchronous processing with RabbitMQ
* Containerization using Docker
* Container orchestration using Kubernetes
* Environment-based configuration management
* Deploying distributed systems locally and on AWS EKS

---

# Future Improvements

* API Gateway – provides a single entry point for clients with routing, authentication, rate-limiting, and monitoring, even when internal service discovery exists
* Circuit breaker pattern using Resilience4j
* CI/CD pipeline integration
* Cloud deployment on AWS with LoadBalancer services
* Fine-tuning business logic for order processing and inventory management

---

# Design Decisions

* RabbitMQ enables **asynchronous decoupled order processing**
* Eureka is used only in Docker Compose; Kubernetes relies on **internal DNS**
* Event-driven architecture ensures **scalability and resilience**

---

# Note

* Service discovery differs by environment:

  * **Docker Compose:** Eureka Server
  * **Kubernetes (Local/EKS):** Internal DNS

* Only the **Order Placement flow** is detailed; authentication, product, and inventory flows are summarized.

* Project focuses on **cloud-native backend design and deployment patterns**, not frontend functionality.

---

# Author

**Tushar Navghare**
Backend Engineer | Java | Spring Boot | Microservices
