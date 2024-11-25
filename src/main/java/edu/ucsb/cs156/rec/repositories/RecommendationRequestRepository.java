package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities.
 */

@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
 /**
   * This method returns an iterable of recommendation requests with given requester_id.
   * @param requester_id requester_id within RecommendationRequest that maps to id in User table
   * @return iterable of RecommendationRequests with requester_id == requester_id
   */
  Iterable<RecommendationRequest> findAllByRequesterId(Long requester_id);

  /**
   * This method returns an iterable of recommendation requests with given professor_id.
   * @param professor_id professor_id within RecommendationRequest that maps to id in User table
   * @return iterable of RecommendationRequests with professor_id == professor_id
   */
  Iterable<RecommendationRequest> findAllByProfessorId(Long professor_id);

    /**
   * This method returns an Optional recommendation request where the current user is either the requester or the professor in the request.
   * @param id id within RecommendationRequest
   * @param professor current user
   * @param requester current user
   * @return Recommendation Request with matching id and same requester or same professor
   */
  Optional<RecommendationRequest> findByIdAndProfessorOrRequester(Long id, User professor, User requester);
}