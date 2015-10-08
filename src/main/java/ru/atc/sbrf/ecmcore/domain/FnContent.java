package ru.atc.sbrf.ecmcore.domain;

/**
 * Created by vkoba on 25.08.2015.
 */
public class FnContent {
  private String base64Content;
  private String mimeType;
  private String contentName;

  public FnContent(String base64Content, String mimeType, String contentName) {
    this.base64Content = base64Content;
    this.mimeType = mimeType;
    this.contentName = contentName;
  }

  public FnContent() {
  }

  public String getBase64Content() {
    return base64Content;
  }

  public void setBase64Content(String base64Content) {
    this.base64Content = base64Content;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getContentName() {
    return contentName;
  }

  public void setContentName(String contentName) {
    this.contentName = contentName;
  }
}
