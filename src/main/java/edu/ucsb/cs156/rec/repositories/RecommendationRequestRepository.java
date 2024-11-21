package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



/**
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities
 */
@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
    /**
     * This method returns all RecommendationRequest entities with a given professor name
     * @param professorName the name of the professor
     * @return  an iterable of RecommendationRequest entities
     */
    Iterable<RecommendationRequest> findAllByProfessorName(String professorName);
    /**
     * This method returns all RecommendationRequest entities with a given requester name
     * @param requesterName the name of the requester
     * @return  an iterable of RecommendationRequest entities
     */
    Iterable<RecommendationRequest> findAllByRequesterName(String requesterName);
    
}