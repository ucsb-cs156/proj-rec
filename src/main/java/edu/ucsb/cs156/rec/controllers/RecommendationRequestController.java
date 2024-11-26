package edu.ucsb.cs156.rec.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
public class RecommendationRequestController extends ApiController{
    
    @Autowired
    private RecommendationRequestRepository recommendationRequestRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * This method returns a list of recommendation requests with specified status for a professor.
     * @return a list of recommendation requests with specified status for a professor.
     */
    @Operation(summary = "Get all completed recommendation requests for a professor")
    @GetMapping("/professor/filtered")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public Iterable<RecommendationRequest> getRecommendationRequestByStatusForProfessor(
        @RequestParam String status) {
        User currentUser = getCurrentUser().getUser();

        // log.info("Current User Roles: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        return recommendationRequestRepository.findAllByProfessorIdAndStatus(
            currentUser.getId(), status);
    }
}
