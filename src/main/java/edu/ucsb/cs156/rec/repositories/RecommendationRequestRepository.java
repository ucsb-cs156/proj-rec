package edu.ucsb.cs156.rec.repositories; 

import edu.ucsb.cs156.rec.entities.RecommendationRequest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/*
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities.
 */

@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
    /**
   * This method returns all the RecommendationRequests with a given requesterId.
   * @param requesterId id of the requester
   * @return an iterable of RecommendationRequests
   */
  Iterable<RecommendationRequest> findAllByRequesterId(long requesterId);
}
