package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

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

import jakarta.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * List all recommendation requests
     * 
     * @return an iterable of RecommendationRequest
     */
    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/alladmin")
    public Iterable<RecommendationRequest> allRecommendationRequests() {
        Iterable<RecommendationRequest> requests = recommendationRequestRepository.findAll();
        return requests;
    }
	
	/**
     * List all recommendation requests created by a user with requesterId
     * 
     * @return an iterable of RecommendationRequest
     */
    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER') && #requesterId == authentication.principal.id")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> getAllByRequesterId(
            @Parameter(name="requesterId") @RequestParam Long requesterId
	) {
        Iterable<RecommendationRequest> requests = recommendationRequestRepository.findAllByRequesterId(requesterId);
        return requests;
    }

    /**
     * Create a new request
     * 
     * @param requesterId   
     * @param professorEmail     
     * @param requestType      
     * @param details          
     * @param neededByDate 
     * @param submissionDate
     * @param completionDate
     * @param status
     * @return                  
     */
    @Operation(summary= "Create a new request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")

    // No. You should get the currentUser.getId() and then set the requesterId inside the controller method.
    // (there should NOT be a database field for requesterEmail or requesterName, only requestId).
    public RecommendationRequest postRecommendationRequest(
            @Parameter(name="requesterId") @RequestParam Long requesterId,
            @Parameter(name="professorId") @RequestParam Long professorId,
            @Parameter(name="requestType") @RequestParam String requestType,
            @Parameter(name="details") @RequestParam String details,
            @Parameter(name="neededByDate", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("neededByDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime neededByDate
            )
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequesterId(requesterId);
        recommendationRequest.setProfessorId(professorId);
        recommendationRequest.setRequestType(requestType);
        recommendationRequest.setDetails(details);

        // completionDate is unassigned until completed, so we set that ass null
        LocalDateTime completionDate = null;
        LocalDateTime submissionDate = LocalDateTime.now();
        String status = "Pending";

        recommendationRequest.setNeededByDate(neededByDate);
        recommendationRequest.setSubmissionDate(submissionDate);
        recommendationRequest.setCompletionDate(completionDate);
        recommendationRequest.setStatus(status);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return savedRecommendationRequest;
    }

    /**
     * Get a single request by id
     * 
     * CHECK if recommendation request belongs to user
     * 
     * @param id the id of the request
     * @return a RecommendationRequest
     */
    @Operation(summary= "Get a single request")
    @PreAuthorize("(hasRole('ROLE_USER') && #requesterId == authentication.principal.id) || hasRole('ROLE_ADMIN)")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id,
			@Parameter(name="requesterId") @RequestParam Long requesterId) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

		if (requesterId != recommendationRequest.getRequesterId()) {
			// throw entity not found to reveal less information to any malicious user
			throw new EntityNotFoundException(RecommendationRequest.class, id);
		}

        return recommendationRequest;
    }
}