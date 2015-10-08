package ru.atc.sbrf.ecmcore.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkoba on 14.08.2015.
 */
public class FnDocument {
  String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  String className;
  FnContent content;
  List<FnProperty> properties;

  public FnDocument() {
    properties = new ArrayList<FnProperty>();
  }


  public List<FnProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<FnProperty> properties) {
    this.properties = properties;
  }

  public FnContent getContent() {
    return content;
  }

  public void setContent(FnContent content) {
    this.content = content;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

}
