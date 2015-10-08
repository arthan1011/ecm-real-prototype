package ru.atc.sbrf.ecmcore.service;

import ru.atc.sbrf.ecmcore.domain.UserSession;
import ru.atc.sbrf.ecmcore.message.ticket.AcquireTicketRequest;
import ru.atc.sbrf.ecmcore.repository.UserSessionRepository;
import ru.atc.sbrf.ecmcore.repository.UserSessionRepositoryFactory;
import ru.atc.sbrf.ecmcore.resolver.SimpleUserSessionResolver;
import ru.atc.sbrf.ecmcore.resolver.UserSessionResolver;

/**
 * Created by vkoba on 19.08.2015.
 */
public class UserSessionService {
  UserSessionRepository userSessionRepository;
  UserSessionResolver userSessionResolver;

  public UserSessionService() {
    userSessionRepository = UserSessionRepositoryFactory.getUserSessionRepository();
    userSessionResolver = new SimpleUserSessionResolver();
  }

  public void addSession(String ticket, AcquireTicketRequest ticketRequest) {
    userSessionRepository.addSession(ticket, userSessionResolver.resolve(ticketRequest));
  }

  public UserSession getUserSession(String ticket) {
    return userSessionRepository.getUserSession(ticket);
  }

  public UserSessionRepository getUserSessionRepository() {
    return userSessionRepository;
  }

  public void setUserSessionRepository(UserSessionRepository userSessionRepository) {
    this.userSessionRepository = userSessionRepository;
  }

  public UserSessionResolver getUserSessionResolver() {
    return userSessionResolver;
  }

  public void setUserSessionResolver(UserSessionResolver userSessionResolver) {
    this.userSessionResolver = userSessionResolver;
  }
}
