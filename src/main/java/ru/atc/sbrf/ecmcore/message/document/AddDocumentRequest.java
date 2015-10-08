package ru.atc.sbrf.ecmcore.message.document;

import ru.atc.sbrf.ecmcore.domain.FnDocument;

/**
 * Created by vkoba on 25.08.2015.
 */
public class AddDocumentRequest {
  FnDocument document;
  String folderPath;

  public FnDocument getDocument() {
    return document;
  }

  public void setDocument(FnDocument document) {
    this.document = document;
  }

  public String getFolderPath() {
    return folderPath;
  }

  public void setFolderPath(String folderPath) {
    this.folderPath = folderPath;
  }
}
