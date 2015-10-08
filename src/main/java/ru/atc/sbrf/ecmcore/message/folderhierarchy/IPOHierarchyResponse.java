package ru.atc.sbrf.ecmcore.message.folderhierarchy;

import ru.atc.sbrf.ecmcore.domain.FnFolderNode;

/**
 * Created by vkoba on 14.08.2015.
 */
public class IPOHierarchyResponse {
  FnFolderNode rootFolder;

  public FnFolderNode getRootFolder() {
    return rootFolder;
  }

  public void setRootFolder(FnFolderNode rootFolder) {
    this.rootFolder = rootFolder;
  }
}
