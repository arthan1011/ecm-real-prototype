package ru.atc.sbrf.ecmcore.service;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.util.Id;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import ru.atc.sbrf.ecmcore.domain.*;
import ru.atc.sbrf.ecmcore.util.CeHelper;
import ru.atc.sbrf.ecmcore.util.EcmCoreFilenetHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Created by vkoba on 19.08.2015.
 */
public class DocumentService {
  public static final String REPORT_NAME = "Заключение";

  List<String> filteredPropertyName = Arrays.asList
      ("Метка привязки компонента",
          "Игнорировать переадресацию",
          "Имя хранилища объектов шаблона ввода",
          "Номер запущенного рабочего потока шаблона ввода",
          "ID шаблона ввода");
  Logger LOGGER = Logger.getLogger(DocumentService.class);


  public void addDocument(FnDocument docForCreation, String folderPath) {
    Document doc = Factory.Document.createInstance(EcmCoreFilenetHelper.getCurrentObjectStore(), docForCreation.getClassName(), null);
    for (FnProperty fnProp : docForCreation.getProperties()) {
      setValueForDoc(doc, fnProp);
    }
    EcmCoreFilenetHelper.setContentForDocument(doc, docForCreation.getContent());
    saveDocument(doc);
    EcmCoreFilenetHelper.fileDocumentIntoFolder(doc, folderPath);

  }


  public String getBase64Content(String docId) {
    try {
      Document doc = Factory.Document.fetchInstance(EcmCoreFilenetHelper.getCurrentObjectStore(), new Id(docId), null);
      ContentElementList cel = doc.get_ContentElements();
      if (cel.isEmpty()) {
        return null;
      }
      ContentTransfer ct = (ContentTransfer) cel.get(0);
      return Base64.encodeBase64String(IOUtils.toByteArray(ct.accessContentStream()));
    } catch (IOException e) {
      LOGGER.error("Cannot get content for " + docId + " Details:", e);
    }
    return null;
  }

  public List<FnClassNode> getClasses(UserSession userSession, String folderPath) {
    for (FolderConfigutarion fc : userSession.getAccessTemplate().getFoldersConfiguration()) {
      if ((userSession.getRootFolderPath() + "/" + fc.getFolderPath()).toLowerCase().contains(folderPath.toLowerCase())) {
        return fc.getAllowedClasses();
      }
    }
    return new ArrayList<FnClassNode>();

  }

  public List<FnProperty> getClassProperties(String className) {
    List<FnProperty> result = new ArrayList<FnProperty>();

    PropertyDescriptionList props = Factory.ClassDescription.fetchInstance(EcmCoreFilenetHelper.getCurrentObjectStore(), className, null).get_PropertyDescriptions();
    Iterator<PropertyDescription> propIt = props.iterator();
    while (propIt.hasNext()) {
      PropertyDescription p = propIt.next();
      if (p.get_IsSystemOwned() || filteredPropertyName.contains(p.get_Name())) {
        continue;
      }
      FnProperty prop = new FnProperty();
      prop.setName(p.get_SymbolicName());
      prop.setDisplayName(p.get_DisplayName());
      prop.setDescription(p.get_DescriptiveText());
      prop.setHidden(p.get_IsHidden());
      prop.setReadOnly(p.get_IsReadOnly());
      prop.setRequired(p.get_IsValueRequired());
      prop.setType(p.get_DataType().toString());
      result.add(prop);
    }
    return result;
  }

  public List<FnProperty> getDocumentProperties(String docId) {
    List<FnProperty> result = new ArrayList<FnProperty>();
    Document doc = Factory.Document.fetchInstance(EcmCoreFilenetHelper.getCurrentObjectStore(), new Id(docId), null);
    PropertyDescriptionList props = doc.get_ClassDescription().get_PropertyDescriptions();
    Iterator<PropertyDescription> propIt = props.iterator();
    while (propIt.hasNext()) {
      PropertyDescription p = propIt.next();
      if (p.get_IsSystemOwned() || filteredPropertyName.contains(p.get_Name())) {
        continue;
      }
      FnProperty prop = new FnProperty();
      prop.setName(p.get_SymbolicName());
      prop.setDisplayName(p.get_DisplayName());
      prop.setDescription(p.get_DescriptiveText());
      prop.setHidden(p.get_IsHidden());
      prop.setReadOnly(p.get_IsReadOnly());
      prop.setRequired(p.get_IsValueRequired());
      prop.setType(p.get_DataType().toString());
      setValueForFnProperty(doc, prop);
      result.add(prop);
    }
    return result;

  }

  private void setValueForFnProperty(Document doc, FnProperty fnProperty) {
    if (fnProperty.getType().toString().equalsIgnoreCase(TypeID.STRING.toString())) {
      fnProperty.setValue(doc.getProperties().getStringValue(fnProperty.getName()));
    }
    if (fnProperty.getType().toString().equals(TypeID.DATE.toString())) {
      Date d = doc.getProperties().getDateTimeValue(fnProperty.getName());
      if (d == null) {
        fnProperty.setValue(null);
        return;
      }
      fnProperty.setValue(d.getTime() + "");
    }
    if (fnProperty.getType().toString().equalsIgnoreCase(TypeID.LONG.toString())) {
      Integer a = doc.getProperties().getInteger32Value(fnProperty.getName());
      if (a == null) {
        fnProperty.setValue(null);
        return;
      }
      fnProperty.setValue(Integer.toString(a));
    }
    if (fnProperty.getType().toString().equalsIgnoreCase(TypeID.BOOLEAN.toString())) {
      Boolean b = doc.getProperties().getBooleanValue(fnProperty.getName());
      if (b == null) {
        fnProperty.setValue(null);
        return;
      }
      fnProperty.setValue(Boolean.toString(b));
    }
  }

  private void setValueForDoc(Document doc, FnProperty fnProperty) {
    if (fnProperty.isReadOnly()) {
      return;
    }
    if (fnProperty.getType().toString().equalsIgnoreCase(TypeID.STRING.toString())) {
      doc.getProperties().putValue(fnProperty.getName(), fnProperty.getValue());
      return;
    }
    if (fnProperty.getType().toString().equals(TypeID.DATE.toString())) {
      Date date = new Date(Long.getLong(fnProperty.getValue()));
      if (date != null) {
        doc.getProperties().putValue(fnProperty.getName(), date);
      }
      return;
    }
    if (fnProperty.getType().toString().equalsIgnoreCase(TypeID.LONG.toString())) {

      Integer intV = Integer.getInteger(fnProperty.getValue());
      if (intV != null) {
        doc.getProperties().putValue(fnProperty.getName(), intV);
      }
      return;
    }
    if (fnProperty.getType().toString().equalsIgnoreCase(TypeID.BOOLEAN.toString())) {
      Boolean b = Boolean.valueOf(fnProperty.getValue());
      if (b != null) {
        doc.getProperties().putValue(fnProperty.getName(), b);
      }
      return;
    }
  }

  private void saveDocument(Document doc) {
    doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
    doc.save(RefreshMode.NO_REFRESH);
  }

  public void updateDocument(String docId, FnDocument docForUpdate) {
    Document doc = Factory.Document.fetchInstance(EcmCoreFilenetHelper.getCurrentObjectStore(), docId, null);
    for (FnProperty fnProp : docForUpdate.getProperties()) {
      setValueForDoc(doc, fnProp);
    }
    doc.save(RefreshMode.NO_REFRESH);
  }

  public void deleteDocument(String docId) {
    Document document = Factory.Document.fetchInstance(EcmCoreFilenetHelper.getCurrentObjectStore(), docId, null);
    document.delete();
    document.save(RefreshMode.NO_REFRESH);
  }

  public String createReportFromTemplate(Map<String, String> parameters) throws IOException {

    Folder ordersFolder = CeHelper.getFolderOrCreateIfNotExists(
            EcmCoreFilenetHelper.getCurrentObjectStore(),
            parameters.get("folderPath"),
            REPORT_NAME
    );
    ordersFolder.save(RefreshMode.REFRESH);

    String reportContent = createReportContent(parameters);
    FnDocument fnDocument = buildDocumentForCreation(reportContent, REPORT_NAME);
    addDocument(fnDocument, ordersFolder.get_PathName());

    return reportContent;
  }

  private FnDocument buildDocumentForCreation(String content, String title) {
    FnContent fnContent = new FnContent(
            content,
            "application/pdf",
            "Отчет1"
    );

    FnDocument fnDocument = new FnDocument();
    fnDocument.setClassName("Document");
    fnDocument.setContent(fnContent);
    fnDocument.getProperties().add(new FnProperty("DocumentTitle", "STRING", title));
    return fnDocument;
  }

  private String createReportContent(Map<String, String> parameters) throws IOException {
    PDDocument pdDocument = new PDDocument();
    PDPage page = new PDPage(PDRectangle.A4);
    PDRectangle rect = page.getMediaBox();
    pdDocument.addPage(page);

    InputStream input = DocumentService.class.getResourceAsStream("/ArialRegular.ttf");
    ByteArrayOutputStream out;
    PDFont font = PDType0Font.load(pdDocument, input);

    PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page);

    int line = 1;

    contentStream.beginText();
    contentStream.setFont(font, 32);
    contentStream.newLineAtOffset(100, rect.getHeight() - 50 * line++);
    contentStream.showText("О Т Ч Е Т");
    contentStream.endText();

    contentStream.beginText();
    contentStream.setFont(font, 12);
    contentStream.newLineAtOffset(100, rect.getHeight() - 50 * line++);
    contentStream.showText("Результат проверки:");
    contentStream.endText();

    contentStream.beginText();
    contentStream.setFont(font, 12);
    contentStream.newLineAtOffset(150, rect.getHeight() - 50 * line++);
    contentStream.showText(parameters.get("checkResult"));
    contentStream.endText();

    contentStream.beginText();
    contentStream.setFont(font, 12);
    contentStream.newLineAtOffset(100, rect.getHeight() - 50 * line++);
    contentStream.showText("Ваш комментарий:");
    contentStream.endText();

    contentStream.beginText();
    contentStream.setFont(font, 12);
    contentStream.newLineAtOffset(150, rect.getHeight() - 50 * line++);
    contentStream.showText(parameters.get("comment"));
    contentStream.endText();

    contentStream.close();

    out = new ByteArrayOutputStream();
    pdDocument.save(out);
    pdDocument.close();

    return Base64.encodeBase64String(out.toByteArray());
  }

}
