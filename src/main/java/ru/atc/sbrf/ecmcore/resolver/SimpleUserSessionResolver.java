package ru.atc.sbrf.ecmcore.resolver;

import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import ru.atc.sbrf.ecmcore.domain.AccessTemplate;
import ru.atc.sbrf.ecmcore.domain.UserSession;
import ru.atc.sbrf.ecmcore.message.ticket.AcquireTicketRequest;
import ru.atc.sbrf.ecmcore.util.ConfigurationManager;
import ru.atc.sbrf.ecmcore.util.EcmCoreFilenetHelper;

import java.io.IOException;

/**
 * Created by vkoba on 12.08.2015.
 */
public class SimpleUserSessionResolver implements UserSessionResolver {
  Logger LOGGER = Logger.getLogger(SimpleUserSessionResolver.class);

  public UserSession resolve(AcquireTicketRequest acquireTicketRequest) {
    UserSession userSession = new UserSession();
    userSession.setUserName(acquireTicketRequest.getUsername());
    userSession.setRootFolderPath(acquireTicketRequest.getRootFolderPath());
    userSession.setAccessTemplate(getAccessTemplateByName(acquireTicketRequest.getAccessTemplateName()));
    return userSession;
  }

  private AccessTemplate getAccessTemplateByName(String accessTemplateName) {
    try {
      ObjectStore os = EcmCoreFilenetHelper.getCurrentObjectStore();
      Document accessTemplateDoc = Factory.Document.fetchInstance(os, getPathForAccessTemplate(accessTemplateName), null);
      ContentTransfer ct = (ContentTransfer) accessTemplateDoc.get_ContentElements().get(0);
      ObjectMapper om = new ObjectMapper();
      AccessTemplate ac = om.readValue(ct.accessContentStream(), AccessTemplate.class);
      return ac;
    } catch (IOException e) {
      LOGGER.error("Get access template error! Details", e);
    }
    throw new RuntimeException("Cannot read AccessTemplate");
  }

  private String getPathForAccessTemplate(String accessTemplateName) {
    String accessTemplateFolderRoot = ConfigurationManager.getProperty("fn.accesstemplaterootfolder");
    if (accessTemplateFolderRoot.charAt(accessTemplateFolderRoot.length() - 1) == '/') {
      return accessTemplateFolderRoot + accessTemplateName;
    }
    return accessTemplateFolderRoot + "/" + accessTemplateName;
  }
}
