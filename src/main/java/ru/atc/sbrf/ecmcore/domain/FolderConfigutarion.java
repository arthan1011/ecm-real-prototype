package ru.atc.sbrf.ecmcore.domain;

import java.util.List;

/**
 * Created by vkoba on 12.08.2015.
 */
public class FolderConfigutarion {
  String folderPath;
  List<FolderPermission> permissions;
  List<FnClassNode> allowedClasses;

  public FolderConfigutarion() {

  }

  public FolderConfigutarion(String folderPath, List<FolderPermission> permissions, List<FnClassNode> allowedClasses) {
    this.folderPath = folderPath;
    this.permissions = permissions;
    this.allowedClasses = allowedClasses;
  }

  public String getFolderPath() {
    return folderPath;
  }

  public void setFolderPath(String folderPath) {
    this.folderPath = folderPath;
  }

  public List<FolderPermission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<FolderPermission> permissions) {
    this.permissions = permissions;
  }

  public List<FnClassNode> getAllowedClasses() {
    return allowedClasses;
  }

  public void setAllowedClasses(List<FnClassNode> allowedClasses) {
    this.allowedClasses = allowedClasses;
  }
}
