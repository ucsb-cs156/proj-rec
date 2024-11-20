package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.UCSBDate;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import edu.ucsb.cs156.rec.entities.RequestType;

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
     */
    @Operation(summary= "Create a new request type")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RequestType postRequestType(
            @Parameter(name="reqType") @RequestParam String reqType)
            throws JsonProcessingException {


        RequestType requestType = new RequestType();
        requestType.setRequestType(reqType);
        System.out.println(requestType.getRequestType());

        // Checks to see if the requestType is a duplicate or not 
        boolean duplicate = false; 
        // Iterable<RequestType> allElements = requestTypeRepository.findAll();
        System.out.println("Made it here");
        // for (RequestType elem : allElements){
        //     if (elem.getRequestType() == reqType){
        //         duplicate = true;
        //         break;
        //     }
        // }


        System.out.println("made it here 2");

        // If the requesttype is not a duplicate, it will add it to the requestType repository
        if (!duplicate){
            RequestType savedRequestType = requestTypeRepository.save(requestType);

            System.out.println("Made it here 3");
            return savedRequestType;

        }

        return requestType;

    }

}
