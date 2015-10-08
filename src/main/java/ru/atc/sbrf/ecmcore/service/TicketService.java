package ru.atc.sbrf.ecmcore.service;

import javax.ws.rs.Path;
import java.util.Date;

/**
 * Created by vkoba on 12.08.2015.
 */


@Path("/acquireTicket")
public class TicketService {


  public String generateTicket() {
    return String.valueOf(new Date().getTime());
  }
}
