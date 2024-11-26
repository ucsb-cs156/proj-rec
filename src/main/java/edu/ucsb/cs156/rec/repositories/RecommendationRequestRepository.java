package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities.
 */

@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
    /**
     * Find all recommendation requests by professor ID and status.
     *
     * @param professor_id professor_id within RecommendationRequest that maps to id in User table
     * @param status the status of recommendation request
     * @return a list of recommendation requests matching the criteria
     */
    Iterable<RecommendationRequest> findAllByProfessorIdAndStatus(Long professor_id, String status);
}