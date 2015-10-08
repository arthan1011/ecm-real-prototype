package ru.atc.sbrf.ecmcore.repository;

/**
 * Created by vkoba on 12.08.2015.
 */
public class UserSessionRepositoryFactory {
  public static UserSessionRepository getUserSessionRepository() {
    return new UserSessionRepository();
  }
}
