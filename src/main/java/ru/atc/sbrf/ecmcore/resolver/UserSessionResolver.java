package ru.atc.sbrf.ecmcore.resolver;

import ru.atc.sbrf.ecmcore.domain.UserSession;
import ru.atc.sbrf.ecmcore.message.ticket.AcquireTicketRequest;

/**
 * Created by vkoba on 12.08.2015.
 */
public interface UserSessionResolver {
  UserSession resolve(AcquireTicketRequest acquireTicketRequest);
}
