package ru.atc.sbrf.ecmcore.domain;

import java.util.List;

/**
 * Created by vkoba on 12.08.2015.
 */
public class AccessTemplate {
  private List<FunctionButton> functionButtons;
  private List<String> searchTypes;
  private List<String> searchPaths;
  private List<String> availableReports;
  private List<FolderConfigutarion> foldersConfiguration;
  private List<GridColumn> gridColumns;

  public List<FunctionButton> getFunctionButtons() {
    return functionButtons;
  }

  public void setFunctionButtons(List<FunctionButton> functionButtons) {
    this.functionButtons = functionButtons;
  }

  public List<String> getSearchTypes() {
    return searchTypes;
  }

  public void setSearchTypes(List<String> searchTypes) {
    this.searchTypes = searchTypes;
  }

  public List<String> getSearchPaths() {
    return searchPaths;
  }

  public void setSearchPaths(List<String> searchPaths) {
    this.searchPaths = searchPaths;
  }

  public List<String> getAvailableReports() {
    return availableReports;
  }

  public void setAvailableReports(List<String> availableReports) {
    this.availableReports = availableReports;
  }

  public List<FolderConfigutarion> getFoldersConfiguration() {
    return foldersConfiguration;
  }

  public void setFoldersConfiguration(List<FolderConfigutarion> foldersConfiguration) {
    this.foldersConfiguration = foldersConfiguration;
  }

  public List<GridColumn> getGridColumns() {
    return gridColumns;
  }

  public void setGridColumns(List<GridColumn> gridColumns) {
    this.gridColumns = gridColumns;
  }
}
