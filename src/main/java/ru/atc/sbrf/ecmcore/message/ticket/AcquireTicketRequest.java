package ru.atc.sbrf.ecmcore.message.ticket;

/**
 * Created by vkoba on 11.08.2015.
 */
public class AcquireTicketRequest {
  private String extSystemId;
  private String username;
  private String rootFolderPath;
  private String accessTemplateName;

  public AcquireTicketRequest() {
  }

  public AcquireTicketRequest(String extSystemId, String username, String rootFolderPath, String accessTemplateName) {
    this.extSystemId = extSystemId;
    this.username = username;
    this.rootFolderPath = rootFolderPath;
    this.accessTemplateName = accessTemplateName;
  }

  public String getExtSystemId() {
    return extSystemId;
  }

  public void setExtSystemId(String extSystemId) {
    this.extSystemId = extSystemId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRootFolderPath() {
    return rootFolderPath;
  }

  public void setRootFolderPath(String rootFolderPath) {
    this.rootFolderPath = rootFolderPath;
  }

  public String getAccessTemplateName() {
    return accessTemplateName;
  }

  public void setAccessTemplateName(String accessTemplateName) {
    this.accessTemplateName = accessTemplateName;
  }
}
