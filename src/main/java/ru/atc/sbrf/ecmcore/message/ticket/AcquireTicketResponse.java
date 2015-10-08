package ru.atc.sbrf.ecmcore.message.ticket;


import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by vkoba on 11.08.2015.
 */


@JsonSerialize
public class AcquireTicketResponse {
  private String ticket;

  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }
}
