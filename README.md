# MSL ratings data client

## Overview
Data Client Layer
Simplification to a traditional edge/middle architecture, this project uses a edge/data client architecture instead.
The data clients are jars, each containing all the methods and DAOs for accessing all the tables within a Cassandra cluster.
To enhance scalability and configuration flexibility, the Cassandra tables are split into three independent clusters: account, catalog, and rating.
Each of these clusters has a data client jar dedicated to accessing it: account-data-client, catalog-data-client, and rating-data-client, respectively.

So a microservice that needs to access Cassandra data will include one or more of the data client jars.

| Table           | Method  |
|:-------------:| -----:|
| **average_ratings** |addOrUpdateAverageRating(AverageRatingDao) |
| | Observable<AverageRatingDao> getAverageRating(UUID contentId, String contentType) |
| | deleteAverageRating(UUID contentId, String contentType) |
| **user_ratings** | addOrUpdateUserRatings(UserRatingsDao) |
| | Observable<UserRatingsDao> getUserRatings(UUID userUuid, String contentType, UUID contentUuid) |
| | Observable<ResultSet> getUserRatings(UUID userUuid, String contentType, Optional<Integer> limit) |
| | Observable<ResultSet> getUserRatings(UUID userUuid, Optional<Integer> limit) |
| | Observable<Result<UserRatingsDao> map(Observable<ResultSet>) |
| | deleteUserRatings(UUID userUuid, String contentType, UUID contentUuid) | 

## Packaging & Installation

```bash 
mvn clean package && mvn -P install compile
```