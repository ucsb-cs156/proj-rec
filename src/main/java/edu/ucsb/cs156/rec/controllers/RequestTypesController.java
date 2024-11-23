package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.errors.DuplicateArgumentException;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import edu.ucsb.cs156.rec.entities.RequestType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;


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


/**
 * This is a REST controller for RequestTypes
 */

@Tag(name = "RequestTypes")
@RequestMapping("/api/requesttypes")
@RestController
@Slf4j
public class RequestTypesController extends ApiController {

    @Autowired
    RequestTypeRepository requestTypeRepository;


    /**
     * Create a new request type
     * 
     * @param reqType  the name of the request type 
     * @return the saved requesttype
     * @throws DuplicateArgumentException if the request type already exists.
     */
    @Operation(summary= "Create a new request type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RequestType postRequestType(
            @Parameter(name="requestType") @RequestParam String requestType){

        // Checks to see if the requestType is a duplicate or not 
        Optional<RequestType> allElements = requestTypeRepository.findByRequestType(requestType);

        if (allElements.isPresent()){
            throw new DuplicateArgumentException(requestType);
        }


        // Creates the new request type and returns it 
        RequestType requestType1 = new RequestType();
        requestType1.setRequestType(requestType);
        RequestType savedRequestType = requestTypeRepository.save(requestType1);
        return savedRequestType;

    }

}