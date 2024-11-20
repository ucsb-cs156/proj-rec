package edu.ucsb.cs156.rec.services;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing RequestType entities.
 */
@Service
public class RequestTypeService {

    private final RequestTypeRepository requestTypeRepository;

    /**
     * Constructor for RequestTypeService with dependency injection.
     * 
     * @param requestTypeRepository The repository for RequestType entities
     */
    public RequestTypeService(RequestTypeRepository requestTypeRepository) {
        this.requestTypeRepository = requestTypeRepository;
    }

    /**
     * Initializes the RequestType table with hardcoded values if they are not already present.
     */
    public void initializeRequestTypes() {
        List<String> hardcodedRequestTypes = List.of(
            "CS Department BS/MS program",
            "Scholarship or Fellowship",
            "MS program (other than CS Dept BS/MS)",
            "PhD program",
            "Other"
        );

        for (String type : hardcodedRequestTypes) {
            requestTypeRepository.findByRequestType(type)
                .orElseGet(() -> {
                    RequestType requestType = RequestType.builder()
                        .requestType(type)
                        .build();
                    return requestTypeRepository.save(requestType);
                });
        }
    }
}