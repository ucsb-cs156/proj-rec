package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * This is a REST controller for Request Types.
 */

@Tag(name = "Request Types")
@RequestMapping("/api/request-types")
@RestController
@Slf4j
public class RequestTypeController extends ApiController {

    @Autowired
    RequestTypeRepository requestTypeRepository;

    /**
     * This method returns a list of all request types.
     * @return a list of all request types
     */
    @Operation(summary = "List all request types")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RequestType> allRequestTypes() {
        return requestTypeRepository.findAll();
    }

    /**
     * This method returns a single request type.
     * @param id id of the request type to get
     * @return a single request type
     */
    @Operation(summary = "Get a single request type")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RequestType getById(
            @Parameter(name = "id") @RequestParam Long id) {
        return requestTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RequestType.class, id));
    }

    /**
     * This method creates a new request type. Accessible only to users with the role "ROLE_ADMIN".
     * @param description description of the request type
     * @return the saved request type (with its id field set by the database)
     */
    @Operation(summary = "Create a new request type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public ResponseEntity<Object> postRequestType(
            @Parameter(name = "description") @RequestParam String description) {
        if (requestTypeRepository.findByRequestType(description).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(
                "type", "IllegalArgumentException",
                "message", "Request Type with this description already exists"
            ));
        }

        RequestType requestType = RequestType.builder()
                .requestType(description)
                .build();

        RequestType savedRequestType = requestTypeRepository.save(requestType);
        return ResponseEntity.ok(savedRequestType);
    }


    /**
     * Deletes a request type. Accessible only to users with the role "ROLE_ADMIN".
     * @param id id of the request type to delete
     * @return a message indicating that the request type was deleted
     */
    @Operation(summary = "Delete a Request Type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRequestType(
            @Parameter(name = "id") @RequestParam Long id) {
        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RequestType.class, id));

        requestTypeRepository.delete(requestType);
        return genericMessage("Request Type with id %s deleted".formatted(id));
    }

    /**
     * Update a single request type. Accessible only to users with the role "ROLE_ADMIN".
     * @param id id of the request type to update
     * @param description the new description for the request type
     * @return the updated request type object
     */
    @Operation(summary = "Update a single request type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public ResponseEntity<Object> updateRequestType(
            @Parameter(name = "id") @RequestParam Long id,
            @Parameter(name = "description") @RequestParam String description) {

        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RequestType.class, id));

        if (requestTypeRepository.findByRequestType(description).isPresent() && 
            !requestType.getRequestType().equals(description)) {
            return ResponseEntity.badRequest().body(Map.of(
                "type", "IllegalArgumentException",
                "message", "Request Type with this description already exists"
            ));
        }

        requestType.setRequestType(description);

        RequestType updatedRequestType = requestTypeRepository.save(requestType);
        return ResponseEntity.ok(updatedRequestType);
    }

}