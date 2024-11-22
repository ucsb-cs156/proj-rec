package edu.ucsb.cs156.rec.controllers;
import edu.ucsb.cs156.rec.entities.User;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "RecommendationRequests")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;



    /**
     * Any admin can delete a RecommendationRequest
     * 
     * @param id the id of the RecommendationRequest to delete
     * @return a message indicating that the RecommendationRequest was deleted
     */
    @Operation(summary = "An admin can delete a RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin")
    public Object deleteRecommendationRequestAsAdmin(@Parameter(name = "id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = 
        recommendationRequestRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(recommendationRequest);

        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    /**
     * The user who posted a RecommendationRequest can delete their RecommendationRequest
     * 
     * @param id the id of the RecommendationRequest to delete
     * @return a message indicating that the RecommendationRequest was deleted
     */
    @Operation(summary = "User can delete their RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public Object deleteRecommendationRequestAsUser(@Parameter(name = "id") @RequestParam Long id) {
        User currentUser = getCurrentUser().getUser(); 
        RecommendationRequest recommendationRequest = 
        recommendationRequestRepository
            .findByIdAndUser(id, currentUser)
            .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(recommendationRequest);

        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    /**
     * Any admin can update a single Recommendation Request
     * 
     * @param id       the id of the Recommendation Request to update
     * @param incoming the updated Recommendation Request
     * @return the updated Recommendation Request object
     */
    @Operation(summary = "An admin can update a single recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin")
    public RecommendationRequest updateRecommendationRequestAsAdmin(
        @Parameter(name = "id") @RequestParam Long id,
        @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendationRequest = 
            recommendationRequestRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequest.setProfessorName(incoming.getProfessorName());
        recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
        recommendationRequest.setRequesterName(incoming.getRequesterName());
        recommendationRequest.setRecommendationTypes(incoming.getRecommendationTypes());
        recommendationRequest.setDetails(incoming.getDetails());
        recommendationRequest.setStatus(incoming.getStatus());
        recommendationRequest.setSubmissionDate(incoming.getSubmissionDate());
        recommendationRequest.setCompletionDate(incoming.getCompletionDate());

        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest;
    }

    /**
     * The user who posted a RecommendationRequest can update their RecommendationRequest
     * 
     * @param id       the id of the Recommendation Request to update
     * @param incoming the updated Recommendation Request
     * @return the updated Recommendation Request object
     */
    @Operation(summary = "User can update their RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequestAsUser( 
        @Parameter(name = "id") @RequestParam Long id,
        @RequestBody @Valid RecommendationRequest incoming) {

        User currentUser = getCurrentUser().getUser(); 
        RecommendationRequest recommendationRequest = 
            recommendationRequestRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequest.setProfessorName(incoming.getProfessorName());
        recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
        recommendationRequest.setRequesterName(incoming.getRequesterName());
        recommendationRequest.setRecommendationTypes(incoming.getRecommendationTypes());
        recommendationRequest.setDetails(incoming.getDetails());
        recommendationRequest.setStatus(incoming.getStatus());
        recommendationRequest.setSubmissionDate(incoming.getSubmissionDate());
        recommendationRequest.setCompletionDate(incoming.getCompletionDate());

        recommendationRequestRepository.save(recommendationRequest);
           
        return recommendationRequest;    
    }
}