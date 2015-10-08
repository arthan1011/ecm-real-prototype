package ru.atc.sbrf.ecmcore.domain;

/**
 * Created by vkoba on 12.08.2015.
 */
public class GridColumn {
  String propertyName;
  String labelText;
  String widthPct;

  public GridColumn() {
  }

  public GridColumn(String propertyName, String labelText, String widthPct) {
    this.propertyName = propertyName;
    this.labelText = labelText;
    this.widthPct = widthPct;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getLabelText() {
    return labelText;
  }

  public void setLabelText(String labelText) {
    this.labelText = labelText;
  }

  public String getWidthPct() {
    return widthPct;
  }

  public void setWidthPct(String widthPct) {
    this.widthPct = widthPct;
  }
}
