package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.Restaurant;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.models.CurrentUser;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
public class RecommendationRequestController extends ApiController {
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * This method returns a list of all restaurants.
     * @return a list of all restaurants
     */
    @Operation(summary = "List all recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRestaurants() {
        Iterable<RecommendationRequest> restaurants = recommendationRequestRepository.findAll();
        return restaurants;
    }

    /**
     * This method returns a single restaurant.
     * @param id id of the restaurant to get
     * @return a single restaurant
     */
    @Operation(summary = "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name = "id") @RequestParam Long id) {
            RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendationRequest;
    }

    /**
     * This method creates a new restaurant. Accessible only to users with the role "ROLE_ADMIN".
     * @param professorName professor name of request
     * @param professorEmail professor email of request
     * @param recommendationTypes recommendation types of request
     * @param details details of request
     * @return the save restaurant (with it's id field set by the database)
     */
    @Operation(summary = "Create a new restaurant")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRestaurant(
            @Parameter(name = "professorName") @RequestParam String professorName,
            @Parameter(name = "professorEmail") @RequestParam String professorEmail,
            @Parameter(name = "recommendationTypes") @RequestParam String recommendationTypes,
            @Parameter(name = "details") @RequestParam String description)
            {
        //get current date right now and set status to pending
        CurrentUser currentUser = getCurrentUser();
        RecommendationRequest recommendationRequest = new RecommendationRequest();

        recommendationRequest.setProfessorName(professorName);
        recommendationRequest.setProfessorEmail(professorEmail);
        recommendationRequest.setRequesterName(currentUser.getUser().getFullName());
        recommendationRequest.setUser(currentUser.getUser());
        recommendationRequest.setRecommendationTypes(recommendationTypes);
        recommendationRequest.setDetails(description);
        recommendationRequest.setStatus("PENDING");
        recommendationRequest.setSubmissionDate(LocalDateTime.now());

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        return savedRecommendationRequest;
    }
}
