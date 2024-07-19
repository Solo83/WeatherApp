# WeatherApp

## Description

Created according to the technical specifications presented in this [course.](https://zhukovsd.github.io/java-backend-learning-course/Projects/WeatherViewer/)

WeatherApp is a web application to check up the current weather. User can signup and start adding any locations.
After this homepage starts to show added locations

## Application Features

### User related

> Classic authorization

- Sign in
- Sign up
- Sign out

### Locations related

> Classic CRUD

- **Search** a location to track the current temperature
- **Add** a location to the tracked list
- **View** a list of locations with the current temperature for each location
- **Delete** a location from the tracked list


## Technologies Used

### Back-end:
- Java 17
- Apache Tomcat Server 10.1.8
- Servlets 6
- Maven 3.9.6
- Hibernate ORM 6.4.4
- PostgreSQL database
- H2 database for tests
- JUnit5
- Mockito
- Thymeleaf 3.1.2

### Front-end:
- HTML
- CSS
- Bootstrap 4

## Installation and Running

1. Clone the repository: git clone https://github.com/Solo83/WeatherApp
2. Get your [APIKey](https://openweathermap.org/)
3. Insert your key into /src/main/resources/api.key {apiKey=YourKey}
4. Open Terminal
5. cd [path-to-repository]/WeatherApp
6. Build the maven project: mvn install
7. Install PostgreSQL
8. Deploy WeatherApp.war via Tomcat 10
 