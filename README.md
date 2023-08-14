# Country Route Calculator
The Country Route Calculator is a Spring Boot service that calculates land routes from one country to another using border information.

## Table of contents
- [Specifications](#specifications)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Algorithm](#algorithm)
- [Error handling](#error-handling)
- [Potential improvements](#potential-improvements)
---
## Specifications
* Spring Boot & Maven: create a Spring boot service that is able to calculate any possible land
  route from one country to another. The objective is to take a list of country data in JSON format
  and calculate the route by utilizing individual countries border information.
* Data link: https://raw.githubusercontent.com/mledoze/countries/master/countries.json
* The application exposes REST endpoint /routing/{origin}/{destination} that
  returns a list of border crossings to get from origin to destination
* Single route is returned if the journey is possible
* Algorithm needs to be efficient
* If there is no land crossing, the endpoint returns HTTP 400
* Countries are identified by `cca3` field in country data
* HTTP request sample (land route from Czech Republic to Italy):
    - `GET /routing/CZE/ITA HTTP/1.0` :
      ```
      {
      "route": ["CZE", "AUT", "ITA"]
      }
      ```

**Expected deliveries**
1. Source code
2. Instructions on how to build and run the application

## Prerequisites
To run the Country Route Calculator, you'll need:

* Java 8 or higher
* Maven
* An HTTP client (e.g., curl, Postman) for testing the API

## Installation
1. Clone the repository:
    ```
    git clone https://github.com/stoicalcode/country-route-calculator.git
    ```
2. Build the project:
* On Unix (Linux/macOS) systems:
    ```
    cd country-route-calculator
    ./mvnw clean install
    ```
* On Windows systems:
    ```
    cd country-route-calculator
    mvnw.cmd clean install
    ```
3. Run the application:
* On Unix (Linux/macOS) systems:
    ```
    cd country-route-calculator
    ./mvnw spring-boot:run    
    ```
* On Windows systems:
    ```
    cd country-route-calculator
    mvnw.cmd spring-boot:run
    ```
or 
    ```
    java -jar target/country-route-calculator-1.0.0.jar
    ```
## Usage
The Country Route Calculator service provides an API endpoint for finding land routes between countries. You can use this service to plan routes between different countries.

API
* `GET /routing/{origin}/{destination}`: Finds a land route between the specified origin and destination countries using their `cca3` code.
  "cca3" is a commonly used field in country data to represent a three-letter country code based on the ISO 3166-1 alpha-3 standard.
* The `origin` and `destination` cca3 codes are always converted to uppercase format.
* Examples of valid calls:
    ```
    curl -X GET http://localhost:8080/routing/CZE/ITA
    curl -X GET http://localhost:8080/routing/cze/ItA
    ```
    
    Both produce a successful response (HTTP 200 OK):
    ```
    {
        "route": ["CZE", "AUT", "ITA"]
    }
    ```

* If there is no land route between the origin and destination, the API will return an HTTP 400 Bad Request.
* To enable DEBUG level logging specifically for the `BreadthFirstSearchCountryService`, you can include the following 
configuration in your application's logging settings:
    ```yaml
    logging:
      level:
        com.stoicalcode.router.service.BreadthFirstSearchCountryService: DEBUG
    ```
    This produces the following output in the console. Example:
    ```
    Found route from 'Malaysia (MYS)' to 'South Africa (ZAF)': Malaysia (MYS) > Thailand (THA) > Myanmar (MMR) > China (CHN) > Afghanistan (AFG) > Iran (IRN) > Iraq (IRQ) > Jordan (JOR) > Israel (ISR) > Egypt (EGY) > Sudan (SDN) > Central African Republic (CAF) > DR Congo (COD) > Angola (AGO) > Namibia (NAM) > South Africa (ZAF)
    ```
## Algorithm
The Country Route Calculator service employs the **Breadth-First Search (BFS)** algorithm within an unweighted graph. 
This algorithm efficiently determines the shortest path between the origin and destination countries based on the border
information of each country.

In this version of BFS, we have implemented some improvements. The service initially validates the countries using their 
"cca3-code" (a unique country code) for the origin and destination and compare with them against the available country data in 
`countries.json`. If any invalid "cca3 code" is detected or if a land connection between the origin and destination is not possible, 
the service throws an appropriate exception. Specifically, it throws the `InvalidCountryException` when an invalid cca3 
code is encountered, and it throws the `PathNotFoundException` when there is no land connection between the origin and destination.

## Error handling
The service handles various error scenarios and provides meaningful error messages for bad requests:

1. **InvalidCountryException** - Occurs when the origin and/or the destination countries cca3 codes are not valid or both origin and destination are the same.
   Examples:
   - Bad Request (400) - invalid origin country: 'INV1', invalid destination country: 'INV2'
   - Bad Request (400) - Origin and destination countries are the same: 'ESP'

2. **PathNotFoundException** - Occurs when there is no land path between the origin and destination countries. 
   This may happen if they belong to different geographical regions (e.g. Americas and Europe) or one of the countries has no borders.
   Examples:
   - Bad Request (400) - Origin and destination not connected by land: 'ESP' (Europe region), 'CAN' (Americas region)
   - Bad Request (400) - Origin 'USA' or destination 'LCA' countries have no borders

These error messages help users understand the specific reason for the bad request, ensuring a more informative and user-friendly experience.

## Potential improvements
1. Cache the country data information as this will not change so frequently. Consider also an expiration value for the 
cached values (example: 1 day). 
2. Instead of caching, preloading the data country information when the app is started and provide an 
   additional service to update this information when required. Example: ```/routing/update-country-data```
3. Implement integration test to cover the most relevant use cases.
4. Dockerize the service.