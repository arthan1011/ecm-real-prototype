package ru.atc.sbrf.ecmcore.repository;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import ru.atc.sbrf.ecmcore.domain.UserSession;

/**
 * Created by vkoba on 12.08.2015.
 */
public class UserSessionRepository {
  private static Cache sessionCache;
  private static int TIME_TO_LIVE = 900;

  static {
    sessionCache = initSessionCache();
  }

  private static Cache initSessionCache() {
    CacheManager cm = CacheManager.getInstance();
    cm.addCache("sessionCache");
    return cm.getCache("sessionCache");
  }

  public void addSession(String ticket, UserSession userSession) {
    sessionCache.put(new Element(ticket, userSession, TIME_TO_LIVE, TIME_TO_LIVE));
    System.out.println(sessionCache.getKeys());
  }


  public UserSession getUserSession(String ticket) {
    if (sessionCache.isKeyInCache(ticket)) {
      return (UserSession) sessionCache.get(ticket).getObjectValue();
    }
    return null;
  }
}
