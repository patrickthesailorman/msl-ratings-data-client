/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.ratings.client.services;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.ratings.client.cassandra.QueryAccessor;
import com.kenzan.msl.ratings.client.cassandra.query.AverageRatingsQuery;
import com.kenzan.msl.ratings.client.cassandra.query.UserRatingsQuery;
import com.kenzan.msl.ratings.client.dao.AverageRatingsDao;
import com.kenzan.msl.ratings.client.dao.UserRatingsDao;
import rx.Observable;

import java.util.UUID;

public class CassandraRatingsService
    implements RatingsService {

    private QueryAccessor queryAccessor;
    private MappingManager mappingManager;

    private static CassandraRatingsService instance = null;

    private CassandraRatingsService() {
        // TODO: Get the contact point from config param
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();

        // TODO: Get the keyspace from config param
        Session session = cluster.connect("msl");

        mappingManager = new MappingManager(session);
        queryAccessor = mappingManager.createAccessor(QueryAccessor.class);
    }

    public static CassandraRatingsService getInstance() {
        if ( instance == null ) {
            instance = new CassandraRatingsService();
        }
        return instance;
    }

    // ================================================================================================================
    // AVERAGE RATINGS
    // ================================================================================================================

    /**
     * Adds or update an average rating to the average_ratings table
     *
     * @param averageRatingDao com.kenzan.msl.ratings.client.dao.AverageRatingsDao
     * @return Observable<Void>
     */
    public Observable<Void> addOrUpdateAverageRating(AverageRatingsDao averageRatingDao) {
        AverageRatingsQuery.add(queryAccessor, mappingManager, averageRatingDao);
        return Observable.empty();
    }

    /**
     * Retrieves an average rating from the average_ratings table
     *
     * @param contentId java.util.UUID
     * @param contentType String
     * @return Observable<AverageRatingsDao>
     */
    public Observable<AverageRatingsDao> getAverageRating(UUID contentId, String contentType) {
        return Observable.just(AverageRatingsQuery.get(queryAccessor, mappingManager, contentId, contentType));
    }

    /**
     * Deletes an average rating from the average_ratings table
     *
     * @param contentId java.util.UUID
     * @param contentType String
     * @return Observable<Void>
     */
    public Observable<Void> deleteAverageRating(UUID contentId, String contentType) {
        AverageRatingsQuery.delete(queryAccessor, mappingManager, contentId, contentType);
        return Observable.empty();
    }

    // ================================================================================================================
    // USER RATINGS
    // ================================================================================================================

    /**
     * Adds a user rating to the user_ratings table
     *
     * @param userRatingsDao com.kenzan.msl.ratings.client.dao.UserRatingsDao
     * @return Observable<Void>
     */
    public Observable<Void> addOrUpdateUserRatings(UserRatingsDao userRatingsDao) {
        UserRatingsQuery.add(queryAccessor, mappingManager, userRatingsDao);
        return Observable.empty();
    }

    /**
     * Retrieves a specific user query from the user_ratings table
     *
     * @param userUuid java.util.UUID
     * @param contentType String
     * @param contentUuid java.util.UUID
     * @return Observable<UserRatingsDao>
     */
    public Observable<UserRatingsDao> getUserRating(UUID userUuid, String contentType, UUID contentUuid) {
        return Observable.just(UserRatingsQuery.getRating(queryAccessor, mappingManager, userUuid, contentUuid,
                                                          contentType));
    }

    /**
     * Retrieve a set of user ratings from the user_ratings table
     *
     * @param userUuid java.util.UUID
     * @param contentType Optional<String>
     * @param limit Optional<Integer>
     * @return Observable<ResultSet>
     */
    public Observable<ResultSet> getUserRatings(UUID userUuid, Optional<String> contentType, Optional<Integer> limit) {
        return Observable.just(UserRatingsQuery.getRatings(queryAccessor, userUuid, contentType, limit));
    }

    /**
     * Maps a result set object into a userRatingsDao result set
     * 
     * @param object Observable<ResultSet>
     * @return Observable<Result<UserRatingsDao>>
     */
    public Observable<Result<UserRatingsDao>> mapUserRatings(Observable<ResultSet> object) {
        return Observable.just(mappingManager.mapper(UserRatingsDao.class).map(object.toBlocking().first()));
    }

    /**
     * Deletes a specific user rating from the user ratings table
     *
     * @param userUuid java.util.UUID
     * @param contentType String
     * @param contentUuid java.util.UUID
     * @return Observable<Void>
     */
    public Observable<Void> deleteUserRatings(UUID userUuid, String contentType, UUID contentUuid) {
        UserRatingsQuery.remove(queryAccessor, mappingManager, userUuid, contentUuid, contentType);
        return Observable.empty();
    }
}