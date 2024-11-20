package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.checkerframework.checker.units.qual.g;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;
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

import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * This is a REST controller for RecommendationRequest
 */

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController{
    
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * List all recommendation requests
     * 
     * @return an iterable of RecommendationRequest
     */
    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequests() {
        Iterable<RecommendationRequest> requests = recommendationRequestRepository.findAll();
        return requests;
    }

    /**
     * Get a single recommendation request by id
     * 
     * @param id the id of the recommendation request
     * @return a recommendation request
     */
    @Operation(summary= "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendationRequest;
    }

    /**
     * Create a new recommendation request
     * 
     * @param professorName the name of the professor
     * @param professorEmail the email of the professor
     * @param requesterName the name of the requester
     * @param recommendationType the type of recommendations
     * @param details the other details of the request
     * @param submissionDate the date the request was submitted
     * @param completionDate the date the request was completed
     * @return the created RecommendationRequest
    */
    
    @Operation(summary= "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
        @Parameter(name="professorNamel") @RequestParam String professorName,
        @Parameter(name="professorEmail") @RequestParam String professorEmail,
        @Parameter(name="requesterName") @RequestParam String requesterName,
        @Parameter(name="recommendationType") @RequestParam String recommendationType,
        @Parameter(name="details") @RequestParam String details,
        @Parameter(name="submissionDate") @RequestParam("submissionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submissionDate,
        @Parameter(name="completionDate") @RequestParam("completionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completionDate)
        throws JsonProcessingException {

            log.info("submissionDate{}", submissionDate);

            RecommendationRequest recommendationRequest = new RecommendationRequest();
            recommendationRequest.setProfessorName(professorName);
            recommendationRequest.setProfessorEmail(professorEmail);
            recommendationRequest.setRequesterName(requesterName);
            recommendationRequest.setRecommendationType(recommendationType);
            recommendationRequest.setDetails(details);
            recommendationRequest.setSubmissionDate(submissionDate);
            recommendationRequest.setCompletionDate(completionDate);
            recommendationRequest.setStatus("pending");

            RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

            return savedRecommendationRequest;
    }

    /**
     * Delete a recommendation request
     * 
     * @param id the id of the recommendation request to delete
     * @return a message indicating the recommedation request was deleted
     */
    @Operation(summary= "Delete a recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(recommendationRequest);
        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    /**
     * Update a single recommendation request
     * 
     * @param id       id of the recommendation request to update
     * @param incoming the new recommendation request
     * @return the updated recommendation request object
     */
    @Operation(summary= "Update a single recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequest.setProfessorName(incoming.getProfessorName());
        recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
        recommendationRequest.setRequesterName(incoming.getRequesterName());
        recommendationRequest.setRecommendationType(incoming.getRecommendationType());
        recommendationRequest.setDetails(incoming.getDetails());
        recommendationRequest.setSubmissionDate(incoming.getSubmissionDate());
        recommendationRequest.setCompletionDate(incoming.getCompletionDate());
        recommendationRequest.setStatus(incoming.getStatus());

        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest;
    }

    /**
     * Get all recommendation requests by Professor Name
     * 
     * @param professorName the name of the professor
     * @return 
     */
    @Operation(summary= "Get all recommendation requests by Professor Name")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Change to ROLE_PROFESSOR LATER
    @GetMapping("")
}
