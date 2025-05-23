# MapMates: Collaborative Road Trip Planning Platform

## Project Overview

MapMates is an innovative web application designed to revolutionize road trip planning by providing a collaborative, interactive platform for travelers. Our mission is to transform the way people plan, organize, and experience road trips by offering a comprehensive tool that allows multiple users to co-create, customize, and manage their travel adventures.

## Technologies Used

- **Backend**: 
  - Java 17
  - Spring Boot 3.1.5
  - PostgreSQL Database with PostGIS extension
  - Hibernate with Spatial Extensions
  - Docker

- **Cloud & Infrastructure**:
  - Google Cloud App Engine for running the backend
  - Google Cloud Compute Engine VM hosting the production Database in a Docker Container
  - Google Cloud Storage for images
  - SonarCloud for Code Quality

- **Testing**:
  - JUnit Jupiter
  - Spring Boot Test
  - Jacoco for Test Coverage

## Key Components

MapMates' architecture is built around several core components that work together to provide a seamless road trip planning experience:

1. **[UserService](src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java)**: 
   - Manages user authentication, registration, and profile management
   - Handles user-related business logic and interactions

2. **[RoadtripService](src/main/java/ch/uzh/ifi/hase/soprafs24/service/RoadtripService.java)**: 
   - Coordinates the creation, modification, and management of road trips
   - Enables collaborative trip planning features

3. **[RouteService](src/main/java/ch/uzh/ifi/hase/soprafs24/service/RouteService.java)**: 
   - Manages route creation, editing, and geospatial calculations
   - Integrates with mapping and routing functionalities

4. **[PointOfInterestService](src/main/java/ch/uzh/ifi/hase/soprafs24/service/PointOfInterestService.java)**: 
   - Handles discovery, management, and interaction with Points of Interest (POIs)
   - Supports adding, editing, and sharing POIs along road trip routes

## Roadmap

For developers looking to contribute to MapMates, here are some exciting features to consider:

1. **Real-time Collaborative Route Editing**
   - Enhance the existing route editing functionality with real-time collaborative features
   - Implement WebSocket-based live updates for route modifications
   - Add conflict resolution mechanisms for simultaneous edits by multiple users

2. **Comprehensive Trip Analytics and Insights**
   - Develop a robust analytics module to provide users with insights about their trips
   - Create visualizations of travel statistics, route efficiency, and group travel dynamics
   - Implement features to track and compare different route options

## Launch & Deployment

### Prerequisites

- Docker
- Docker Compose
- (Optional) Google Cloud account for advanced features

### Local Development Setup

1. Clone the repository
   ```bash
   git clone https://github.com/your-organization/mapmates-server.git
   cd mapmates-server
   ```

2. Start the Development Environment
   ```bash
   docker-compose --profile dev up
   ```

> **Note:**  
> For image upload/download functionality to work correctly, a service account key is required.
> Place a JSON key file named `sopra-service-account.json` in the top-level `keys/` directory of the project.
> This file must contain the credentials for a **Google Cloud service account** with **Role:** `roles/storage.admin`  



### Running Tests

Execute the comprehensive test suite using Docker:
```bash
docker-compose --profile test up
```

### Deployment

- The application is fully dockerized for consistent deployment
- Configured for deployment on Google Cloud Platform
- CI/CD pipeline integrated with SonarCloud for continuous code quality checks

## Authors

- Adrian Hauser
- Sebastian Gmür
- Carlo Muntwyler
- Rico Camenzind
- Kim Schlup

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- University of Zurich, Software Engineering Course
- Lukas Niedhart, our TA.
