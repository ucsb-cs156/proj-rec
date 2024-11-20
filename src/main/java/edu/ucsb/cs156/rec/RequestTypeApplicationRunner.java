package edu.ucsb.cs156.rec;

import edu.ucsb.cs156.rec.services.RequestTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

/**
 * This class contains a `run` method that initializes RequestType entries at application startup.
 */
@Slf4j
@Configuration
public class RequestTypeApplicationRunner implements ApplicationRunner {

  @Autowired
  private RequestTypeService requestTypeService;

  /**
   * Called once at application startup time. Delegates the initialization of RequestType table to the service.
   */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("RequestTypeApplicationRunner.run called");
    requestTypeService.initializeRequestTypes();
  }
}