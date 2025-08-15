package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.models.SystemInfo;
import edu.ucsb.cs156.rec.services.SystemInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a REST controller for getting information about the system.
 *
 * <p>It allows frontend access to some of the global values set in the backend of the application,
 * some of which are set by environment variables.
 *
 * <p>For more information see the SystemInfoService and SystemInfo classes.
 *
 * @see edu.ucsb.cs156.rec.services.SystemInfoService
 * @see edu.ucsb.cs156.rec.models.SystemInfo
 */
@Tag(name = "System Information")
@RequestMapping("/api/systemInfo")
@RestController
public class SystemInfoController extends ApiController {

  @Autowired private SystemInfoService systemInfoService;

  /**
   * This method returns the system information.
   *
   * @return the system information
   */
  @Operation(summary = "Get global information about the application")
  @GetMapping("")
  public SystemInfo getSystemInfo() {
    return systemInfoService.getSystemInfo();
  }
}
