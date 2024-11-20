package edu.ucsb.cs156.rec.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

class RequestTypeServiceTests {

  @Test
  void test_initializeRequestTypes_adds_missing_request_types() {
    // Arrange
    RequestTypeRepository mockRepository = mock(RequestTypeRepository.class);
    RequestTypeService requestTypeService = new RequestTypeService(mockRepository);

    List<String> hardcodedTypes = List.of(
        "CS Department BS/MS program",
        "Scholarship or Fellowship",
        "MS program (other than CS Dept BS/MS)",
        "PhD program",
        "Other"
    );

    // Mock behavior: Return empty when checking for existing types
    when(mockRepository.findByRequestType(anyString())).thenReturn(Optional.empty());

    // Act
    requestTypeService.initializeRequestTypes();

    // Assert
    for (String type : hardcodedTypes) {
      verify(mockRepository, times(1)).findByRequestType(type);
      verify(mockRepository, times(1)).save(argThat(requestType -> requestType.getRequestType().equals(type)));
    }
  }

  @Test
  void test_initializeRequestTypes_skips_existing_request_types() {
    // Arrange
    RequestTypeRepository mockRepository = mock(RequestTypeRepository.class);
    RequestTypeService requestTypeService = new RequestTypeService(mockRepository);

    // Mock behavior: Some request types already exist
    when(mockRepository.findByRequestType("CS Department BS/MS program"))
        .thenReturn(Optional.of(RequestType.builder().requestType("CS Department BS/MS program").build()));
    when(mockRepository.findByRequestType("Scholarship or Fellowship"))
        .thenReturn(Optional.of(RequestType.builder().requestType("Scholarship or Fellowship").build()));
    when(mockRepository.findByRequestType("MS program (other than CS Dept BS/MS)"))
        .thenReturn(Optional.empty());
    when(mockRepository.findByRequestType("PhD program"))
        .thenReturn(Optional.empty());
    when(mockRepository.findByRequestType("Other"))
        .thenReturn(Optional.empty());

    // Act
    requestTypeService.initializeRequestTypes();

    // Assert
    verify(mockRepository, times(1)).findByRequestType("CS Department BS/MS program");
    verify(mockRepository, times(1)).findByRequestType("Scholarship or Fellowship");
    verify(mockRepository, times(1)).findByRequestType("MS program (other than CS Dept BS/MS)");
    verify(mockRepository, times(1)).findByRequestType("PhD program");
    verify(mockRepository, times(1)).findByRequestType("Other");

    verify(mockRepository, never()).save(argThat(requestType -> 
        requestType.getRequestType().equals("CS Department BS/MS program") ||
        requestType.getRequestType().equals("Scholarship or Fellowship")));
    verify(mockRepository, times(1)).save(argThat(requestType -> requestType.getRequestType().equals("MS program (other than CS Dept BS/MS)")));
    verify(mockRepository, times(1)).save(argThat(requestType -> requestType.getRequestType().equals("PhD program")));
    verify(mockRepository, times(1)).save(argThat(requestType -> requestType.getRequestType().equals("Other")));
  }
}