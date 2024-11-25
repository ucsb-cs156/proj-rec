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
     * Find a recommendation request by the professor's user id
     * 
     * @param id the id of the professor user
     * @return an Optional of the recommendation request
     */
    List<RecommendationRequest> findAllByProfessorId(Long id);

    
}