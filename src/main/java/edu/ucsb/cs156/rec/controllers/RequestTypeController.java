package edu.ucsb.cs156.rec.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

@Tag(name = "RequestType")
@RequestMapping("/api/requesttype")
@RestController
public class RequestTypeController  extends ApiController {
    @Autowired
    RequestTypeRepository requestTypeRepository;

     /**
     * This method returns a list of all Request Types.
     * @return a list of all Request Types.
     */
    @Operation(summary = "List all request types")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RequestType> allRequestTypes(
    ) {
        Iterable<RequestType> requestTypes = requestTypeRepository.findAll();
        return requestTypes;
    }
}
