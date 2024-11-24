package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

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

/**
 * This is a REST controller for RequestType
 */

@Tag(name = "RequestType")
@RequestMapping("/api/requesttype")
@RestController
@Slf4j
public class RequestTypeController extends ApiController {

    @Autowired
    RequestTypeRepository requestTypeRepository;

    /**
     * List all Request Types
     * 
     * @return an iterable of RequestType
     */
    @Operation(summary= "List all request types")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RequestType> allRequestTypes() {
        Iterable<RequestType> requestTypes = requestTypeRepository.findAll();
        return requestTypes;
    }

        /**
     * Get a single request type by id
     * 
     * @param id the id of the request type
     * @return a RequestTYpe
     */
    @Operation(summary= "Get a single request type")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RequestType getById(
            @Parameter(name="id") @RequestParam Long id) {
        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RequestType.class, id));

        return requestType;
    }

    /**
     * Create a new request type
     * 
     * @param requestType          the type of the request
     * @return the saved requesttype
     */
    @Operation(summary= "Create a new request type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RequestType postRequestType(
            @Parameter(name="requestType") @RequestParam String requestType)
            throws JsonProcessingException {

        log.info("requestType={}", requestType);

        // Check for duplicates
        Iterable<RequestType> existingRequestTypes = requestTypeRepository.findAll();
        for (RequestType existing : existingRequestTypes) {
            if (existing.getRequestType().equals(requestType)) {
                throw new IllegalArgumentException("Duplicate request type: " + requestType);
        }
    }

        // Create new request type
        RequestType requestTypeNew = new RequestType();
        requestTypeNew.setRequestType(requestType);

        RequestType savedRequestType = requestTypeRepository.save(requestTypeNew);

        return savedRequestType;
    }
}