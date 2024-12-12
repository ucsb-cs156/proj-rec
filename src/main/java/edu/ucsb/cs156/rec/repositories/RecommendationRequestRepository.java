package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



/**
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities
 */
@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
    /**
     * Find all recommendation request by the professor's user id
     * 
     * @param id the id of the professor user
     * @return a List of the recommendation request
     */
    List<RecommendationRequest> findAllByProfessorId(Long id);

    /*
     * Find all recommendation requests by the requester's user id
     * @param id the id of the professor user
     * @return a List of recommendaiton requests
     */
    List<RecommendationRequest> findAllByRequesterId(Long id);

    
}