package ru.atc.sbrf.ecmcore.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkoba on 14.08.2015.
 */
public class FnFolderNode {
  List<FnProperty> properties;
  List<FnFolderNode> subfolders;
  List<FnDocument> documents;

  public FnFolderNode() {
    properties = new ArrayList<FnProperty>();
    subfolders = new ArrayList<FnFolderNode>();
    documents = new ArrayList<FnDocument>();
  }

  public List<FnProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<FnProperty> properties) {
    this.properties = properties;
  }

  public List<FnFolderNode> getSubfolders() {
    return subfolders;
  }

  public void setSubfolders(List<FnFolderNode> subfolders) {
    this.subfolders = subfolders;
  }

  public List<FnDocument> getDocuments() {
    return documents;
  }

  public void setDocuments(List<FnDocument> documents) {
    this.documents = documents;
  }
}
