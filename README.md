# Price Tracker - Kubernetes Microservices-Based Web Application

A full-stack price tracking solution built with a microservices architecture, designed to monitor product prices across e-commerce platforms.

## Features

- **Web Scraping Microservice**: Automated price collection using FastAPI
- **Product Management Microservice**: Spring Boot backend with MongoDB storage
- **React Frontend**: Modern UI with responsive design. Create graphs to showcase collected data.
- **Multi-database Architecture**: PostgreSQL for scraping data, MongoDB for product information
- **Containerized Deployment**: Docker-based environment for seamless setup

## Getting Started
- **Utils**: Contains utility scripts and configurations to facilitate set up and deployment.
- **Prerequisites**:
  - `minikube`
  - `kubectl`
- **Installing**:
  - Clone the repository
  - Make use of the provided `utils` directory for setting up the environment
  - set-up-minikube.sh start will set up profile "price-tracker".
  - Use build-images.sh to build docker images to local registry.
  - set-up-minikube.sh apply will apply the manifests, initializing the development environment.
