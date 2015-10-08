package ru.atc.sbrf.ecmcore.domain;

/**
 * Created by vkoba on 18.08.2015.
 */
public class FnProperty {
  String name;
  String displayName;
  String type;
  String description;
  String defaultValue;
  String value;
  boolean readOnly;
  boolean required;
  boolean hidden;


  public FnProperty(String name, String type, String value) {
    this.name = name;
    this.type = type;
    this.value = value;
    readOnly = false;
    required = false;
    hidden = false;

  }

  public FnProperty() {
    readOnly = false;
    required = false;
    hidden = false;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
}
