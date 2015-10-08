package ru.atc.sbrf.ecmcore.util;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.*;
import com.sun.jersey.core.util.Base64;
import ru.atc.sbrf.ecmcore.domain.FnContent;

import java.io.ByteArrayInputStream;


/**
 * Created by vkoba on 13.08.2015.
 */
public class EcmCoreFilenetHelper {

  public static ObjectStore getCurrentObjectStore() {
    ObjectStore os = CeHelper.getObjectStore(CeHelper.getDomain(CeHelper.createConnection(ConfigurationManager.getProperty("fn.wsi"), ConfigurationManager.getProperty("fn.login"),
        ConfigurationManager.getProperty("fn.password")), ConfigurationManager.getProperty("fn.domain")), ConfigurationManager.getProperty("fn.os"));
    return os;
  }

  public static void setContentForDocument(Document doc, FnContent content) {

    ContentTransfer ct = Factory.ContentTransfer.createInstance();
    try {
      ct.setCaptureSource(new ByteArrayInputStream(Base64.decode(content.getBase64Content())));
    } catch (Exception e) {
      throw new RuntimeException("Cannot get content!", e);
    }
    ct.set_RetrievalName(content.getContentName());
    ct.set_ContentType(content.getMimeType());
    ContentElementList cel = Factory.ContentElement.createList();
    cel.add(ct);
    doc.set_ContentElements(cel);
  }

  public static void fileDocumentIntoFolder(Document doc, String folderPath) {
    Folder folder = Factory.Folder.fetchInstance(doc.getObjectStore(), folderPath, null);
    ReferentialContainmentRelationship rcr = folder.file(doc, AutoUniqueName.AUTO_UNIQUE, null, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
    rcr.save(RefreshMode.NO_REFRESH);
  }
}
