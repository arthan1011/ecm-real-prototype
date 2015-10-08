package ru.atc.sbrf.ecmcore.service;

import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import ru.atc.sbrf.ecmcore.domain.*;
import ru.atc.sbrf.ecmcore.util.EcmCoreFilenetHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vkoba on 18.08.2015.
 */

public class UiService {


  public FnFolderNode getHierarchy(UserSession userSession) {
    Folder root = Factory.Folder.fetchInstance(EcmCoreFilenetHelper.getCurrentObjectStore(), userSession.getRootFolderPath(), null);
    FnFolderNode node = new FnFolderNode();
    recursiveFillFolder(node, root);
    return node;
  }


  public List<FunctionButton> getFunctionButtons(UserSession userSession) {
    return userSession.getAccessTemplate().getFunctionButtons();
  }

  public List<GridColumn> getGridColumns(UserSession userSession) {
    return userSession.getAccessTemplate().getGridColumns();
  }

  public List<FolderPermission> getFolderPermissions(UserSession userSession, String folderPath) {
    FolderConfigutarion folderConfiguration = findFolderConfiguration(userSession.getAccessTemplate().getFoldersConfiguration(), folderPath);
    if (folderConfiguration == null) {
      return new ArrayList<FolderPermission>();
    }
    return folderConfiguration.getPermissions();
  }

  private FolderConfigutarion findFolderConfiguration(List<FolderConfigutarion> foldersConfiguration, String folderPath) {
    for (FolderConfigutarion fn : foldersConfiguration) {
      if (folderPath.toLowerCase().contains(fn.getFolderPath().toLowerCase())) {
        return fn;
      }
    }
    return null;
  }

  private void recursiveFillFolder(FnFolderNode node, Folder root) {
    setFolderAttributes(node, root);
    fillDocuments(node, root);
    processSubfolders(node, root);
  }

  private void processSubfolders(FnFolderNode node, Folder root) {
    FolderSet fset = root.get_SubFolders();
    Iterator<Folder> fit = fset.iterator();
    while (fit.hasNext()) {
      Folder f = fit.next();
      FnFolderNode fn = new FnFolderNode();
      recursiveFillFolder(fn, f);
      node.getSubfolders().add(fn);
    }
  }

  private void setFolderAttributes(FnFolderNode node, Folder root) {
    node.getProperties().add(new FnProperty(FnFolderConstName.ID, TypeID.STRING.toString(), root.get_Id().toString()));
    node.getProperties().add(new FnProperty(FnFolderConstName.NAME, TypeID.STRING.toString(), root.get_FolderName()));
    node.getProperties().add(new FnProperty(FnFolderConstName.TYPE, TypeID.STRING.toString(), root.get_ClassDescription().get_DisplayName()));
    node.getProperties().add(new FnProperty(FnFolderConstName.PATH, TypeID.STRING.toString(), root.get_PathName()));
  }

  private void fillDocuments(FnFolderNode node, Folder root) {
    DocumentSet dset = root.get_ContainedDocuments();
    Iterator<Document> dit = dset.iterator();
    while (dit.hasNext()) {
      Document doc = dit.next();
      FnDocument fndoc = new FnDocument();
      fndoc.getProperties().add(new FnProperty(FnDocumentConstName.ID, TypeID.STRING.toString(), doc.get_Id().toString()));
      fndoc.getProperties().add(new FnProperty(FnDocumentConstName.NAME, TypeID.STRING.toString(), doc.get_Name()));
      fndoc.getProperties().add(new FnProperty(FnDocumentConstName.MIME, TypeID.STRING.toString(), doc.get_MimeType()));
      fndoc.getProperties().add(new FnProperty(FnDocumentConstName.CLASS, TypeID.STRING.toString(), doc.get_ClassDescription().get_DisplayName()));
      fndoc.getProperties().add(new FnProperty(FnDocumentConstName.CHECKED, TypeID.BOOLEAN.toString(), String.valueOf((doc.getProperties().isPropertyPresent(FnDocumentConstName.CHECKED)) ?
              doc.getProperties().getBooleanValue(FnDocumentConstName.CHECKED) : "")
      ));
      node.getDocuments().add(fndoc);
    }
  }
}
