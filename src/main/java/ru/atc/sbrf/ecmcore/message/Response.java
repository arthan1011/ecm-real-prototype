package ru.atc.sbrf.ecmcore.message;

/**
 * Created by vkoba on 26.08.2015.
 */
public class Response {
  private Object result;
  private String error;


  public Response(Object result, String error) {
    this.result = result;
    this.error = error;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
