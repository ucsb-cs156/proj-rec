package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.checkerframework.checker.units.qual.g;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

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

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequestTypeRepository requestTypeRepository;

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
     * @throws EntityNotFoundException if the recommendation request is not found
     * @return a recommendation request
     */
    @Operation(summary= "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendationRequest;
    }

    /**
     * Create a new recommendation request
     * @param professorEmail the email of the professor
     * @param recommendationType the type of recommendations
     * @param details the other details of the request
     * @throws EntityNotFoundException if the professor is not found or is hte user does not have the professor role
     * @return the created RecommendationRequest
    */
    
    @Operation(summary= "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
        @Parameter(name="professorEmail") @RequestParam String professorEmail,
        @Parameter(name="recommendationType") @RequestParam String recommendationType,
        @Parameter(name="details") @RequestParam String details)
        throws JsonProcessingException {

            User prof = userRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new EntityNotFoundException(User.class, professorEmail));
            
            if (!prof.getProfessor()) {
                throw new EntityNotFoundException(User.class, professorEmail);
            }

            RequestType type = requestTypeRepository.findByRequestType(recommendationType)
                .orElseThrow(() -> new EntityNotFoundException(RequestType.class, recommendationType));

            LocalDate currDate = LocalDate.now();

            RecommendationRequest recommendationRequest = new RecommendationRequest();
            recommendationRequest.setProfessor(prof);
            recommendationRequest.setRequester(getCurrentUser().getUser());
            recommendationRequest.setRecommendationType(type);
            recommendationRequest.setDetails(details);
            recommendationRequest.setSubmissionDate(currDate);
            recommendationRequest.setStatus("pending");

            RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

            return savedRecommendationRequest;
    }

    /**
     * Delete a recommendation request
     * 
     * @param id the id of the recommendation request to delete
     * @throws EntityNotFoundException if the recommendation request is not found
     * @return a message indicating the recommedation request was deleted
     */
    @Operation(summary= "Delete a recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        if( recommendationRequest.getRequester().getId() != getCurrentUser().getUser().getId()) {
            throw new AccessDeniedException(null);
        }

        recommendationRequestRepository.delete(recommendationRequest);
        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    /**
     * Update a single recommendation request
     * 
     * @param id       id of the recommendation request to update
     * @param incoming the new recommendation request
     * @throws EntityNotFoundException if the recommendation request is not found
     * @return the updated recommendation request object
     */
    @Operation(summary= "Update a single recommendation request")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        LocalDate currDate = LocalDate.now();

        recommendationRequest.setRecommendationType(incoming.getRecommendationType());
        recommendationRequest.setDetails(incoming.getDetails());
        recommendationRequest.setCompletionDate(currDate);
        recommendationRequest.setStatus(incoming.getStatus());

        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest;
    }

    /**
     * Get all recommendation requests by Professor id
     * 
     * @param userId the user Id of the professor
     * @throws EntityNotFoundException if the professor is not found
     * @return a list of all rec reqs directed towards the professor with the given userId
     */
    @Operation(summary= "Get all recommendation requests by Professor's user id")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')") 
    @GetMapping("/professor/{userId}")
    public List<RecommendationRequest> getAllRecommendationRequestsByProfessor(
        @PathVariable(value="userId") long userId) {

        if( !userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, userId);
        }
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAllByProfessorId(userId);
        return recommendationRequests;
    }

    /** 
     * Get all recommendation requests by Requester id
     * @param userId the user Id of the requester
     * @throws EntityNotFoundException if the requester is not found
     * @return a list of all rec reqs made by the requester with the given userId
     */
    @Operation(summary = "Get all recommendation requests by Requester's user id")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT')")
    @GetMapping("/requester/{userId}")
    public List<RecommendationRequest> getAllRecommendationRequestsByRequester(
        @PathVariable(value="userId") long userId){

        if( !userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, userId);
        }
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAllByRequesterId(userId);
        return recommendationRequests;

    }
}
