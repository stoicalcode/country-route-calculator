# Country Route Calculator
The Country Route Calculator is a Spring Boot service that calculates land routes from one country to another using border information.

## Table of contents
- [Specifications](#specifications)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Algorithm](#algorithm)
- [Error handling](#error-handling)
- [Next improvements](#next-improvements)
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
```shell
git clone https://github.com/stoicalcode/country-route-calculator.git
```

 2. Build the project:
```
cd country-route-calculator
mvn clean install
```
3. Run the application:
```shell
java -jar target/country-route-calculator-1.0.0.jar
```

## Usage
The Country Route Calculator service provides an API endpoint for finding land routes between countries. You can use this service to plan routes between different countries.

API
* `GET /routing/{origin}/{destination}`: Finds a land route between the specified origin and destination countries using their `cca3` code.
  "cca3" is a commonly used field in country data to represent a three-letter country code based on the ISO 3166-1 alpha-3 standard.
    Example:
```
curl -X GET http://localhost:8080/routing/CZE/ITA
```

Response:
```
{
    "route": ["CZE", "AUT", "ITA"]
}
```

If there is no land route between the origin and destination, the API will return an HTTP 400 Bad Request.

## Algorithm
The Country Route Calculator uses the Breadth-First-Search (BFS) algorithm in an unweighted graph as an efficient 
algorithm to calculate the shortest path between the origin and destination countries based on the country border 
information.

## Error handling
The service handles various error scenarios and provides meaningful error messages for bad requests:

1. **InvalidCountryException** - Occurs when the origin and destination countries are the same.

   Example: Bad Request (400) - Origin and destination countries are the same: 'ESP'

2. **PathNotFoundException** - Occurs when there is no land path between the origin and destination countries.

  - Bad Request (400) - Origin and destination not connected by land: 'ESP' (Europe region), 'CAN' (Americas region)
  - Bad Request (400) - Origin 'USA' or destination 'LCA' countries have no borders
  - Bad Request (400) - Not found land path from 'POL' to 'CAN'

These error messages help users understand the specific reason for the bad request, ensuring a more informative and user-friendly experience.

## Next improvements
1. Cache the country data information as this information will not change so frequently.
2. Implement integration test with different use cases.
3. Dockerize the service