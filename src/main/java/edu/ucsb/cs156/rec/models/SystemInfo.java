package edu.ucsb.cs156.rec.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a model class that represents system information.
 *
 * <p>This class is used to provide information about the system to the frontend.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SystemInfo {
  private Boolean springH2ConsoleEnabled;
  private Boolean showSwaggerUILink;
  private String oauthLogin;
  private String sourceRepo; // user configured URL of the source repository for footer
  private String commitMessage;
  private String commitId;
  private String githubUrl; // URL to the commit in the source repository
}
