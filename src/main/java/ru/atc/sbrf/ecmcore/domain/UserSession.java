package ru.atc.sbrf.ecmcore.domain;

/**
 * Created by vkoba on 12.08.2015.
 */
public class UserSession {
  private AccessTemplate accessTemplate;
  private String rootFolderPath;
  private String userName;

  public AccessTemplate getAccessTemplate() {
    return accessTemplate;
  }

  public void setAccessTemplate(AccessTemplate accessTemplate) {
    this.accessTemplate = accessTemplate;
  }

  public String getRootFolderPath() {
    return rootFolderPath;
  }

  public void setRootFolderPath(String rootFolderPath) {
    this.rootFolderPath = rootFolderPath;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
