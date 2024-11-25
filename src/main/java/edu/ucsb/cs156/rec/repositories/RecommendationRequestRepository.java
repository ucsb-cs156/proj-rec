package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;



/**
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities
 */
@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
    List<RecommendationRequest> findAllByProfessorId(Long id);

    
}