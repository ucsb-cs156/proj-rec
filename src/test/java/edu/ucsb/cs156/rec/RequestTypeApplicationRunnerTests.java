package edu.ucsb.cs156.rec;

import edu.ucsb.cs156.rec.services.RequestTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.Mockito.*;

class RequestTypeApplicationRunnerTests {

    @Test
    void test_run_calls_initializeRequestTypes() throws Exception {
        // Arrange
        RequestTypeService mockRequestTypeService = mock(RequestTypeService.class);
        RequestTypeApplicationRunner applicationRunner = new RequestTypeApplicationRunner(mockRequestTypeService);
        ApplicationArguments mockArgs = mock(ApplicationArguments.class);

        // Act
        applicationRunner.run(mockArgs);

        // Assert
        verify(mockRequestTypeService, times(1)).initializeRequestTypes();
    }
}
