package ru.atc.sbrf.ecmcore.domain;

/**
 * Created by vkoba on 13.08.2015.
 */
public class FnClassNode {
  String name;
  String displayName;

  public FnClassNode(String name, String displayName) {
    this.name = name;
    this.displayName = displayName;
  }

  public FnClassNode() {
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }


}
