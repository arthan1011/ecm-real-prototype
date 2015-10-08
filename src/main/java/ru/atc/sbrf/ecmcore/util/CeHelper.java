package ru.atc.sbrf.ecmcore.util;


import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.*;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.util.UserContext;

import javax.security.auth.Subject;
import java.util.Iterator;


public class CeHelper {


  public static Connection createConnection(String uri, String login, String password) {
    Connection conn = Factory.Connection.getConnection(uri);
    Subject subject = UserContext.createSubject(conn, login, password, null);
    UserContext uc = UserContext.get();
    uc.pushSubject(subject);
    return conn;
  }

  public static Domain getDomain(Connection conn, String domainName) {
    return Factory.Domain.fetchInstance(conn, domainName, null);
  }

  public static ObjectStore getObjectStore(Domain domain, String osName) {
    ObjectStoreSet osset = domain.get_ObjectStores();
    Iterator<ObjectStore> osIterator = osset.iterator();
    while (osIterator.hasNext()) {
      ObjectStore curOs = (ObjectStore) osIterator.next();
      if (curOs.get_Name().equalsIgnoreCase(osName)) {
        return curOs;
      }
    }
    throw new RuntimeException("Object store " + osName + " not found!");
  }


  public static void fileInSameHierarchy(Document sourceDocument, Folder sourceFolderRoot, Folder publishingFolderRoot, Document pubDoc) {
    FolderSet sourceDocumentfoldersFiledIn = sourceDocument.get_FoldersFiledIn();
    Iterator<Folder> iterator = sourceDocumentfoldersFiledIn.iterator();
    String sourceRootPath = sourceFolderRoot.get_PathName();
    while (iterator.hasNext()) {
      Folder folder = iterator.next();
      String path = folder.get_PathName();
      if (path.contains(sourceRootPath)) {
        String creation_path = path.substring(sourceRootPath.length());
        Folder publishing_folder = createFolderPath(sourceDocument.getObjectStore(), publishingFolderRoot, creation_path);
        unfileFromAllFolders(pubDoc);
        ReferentialContainmentRelationship relationship = publishing_folder.file(pubDoc, AutoUniqueName.AUTO_UNIQUE, "NewModel", DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
        relationship.save(RefreshMode.REFRESH);

      } else {
      }
    }
  }


  private static void unfileFromAllFolders(Document pubDoc) {
    Iterator iterator = pubDoc.get_FoldersFiledIn().iterator();

    String folderName = "";
    while (iterator.hasNext()) {
      try {
        Folder folder = (Folder) iterator.next();
        folderName = folder.get_FolderName();
        ReferentialContainmentRelationship relationship = folder.unfile(pubDoc);
        relationship.save(RefreshMode.REFRESH);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static Folder createFolderPath(ObjectStore os, Folder publishingFolderRoot, String creation_path) {
    String[] path = creation_path.split("/");
    Folder parent = publishingFolderRoot;
    for (String folder_name : path) {
      folder_name = folder_name.trim();
      if (folder_name != null && !folder_name.isEmpty()) {
        String folder_path = parent.get_PathName() + "/" + folder_name;
        Folder newFolder = getFolder(os, folder_path);
        if (newFolder == null)
          newFolder = createFolder(os, parent, folder_name);
        parent = newFolder;
      }
    }
    return parent;
  }

  private static Folder createFolder(ObjectStore os, Folder parent, String folder_name) {
    Folder folder = null;
    folder = Factory.Folder.createInstance(os, null);
    folder.set_Parent(parent);
    folder.set_FolderName(folder_name);
    folder.save(RefreshMode.NO_REFRESH);
    return folder;
  }

  private static Folder getFolder(ObjectStore os, String folder_path) {
    Folder folder = null;
    try {
      folder = Factory.Folder.fetchInstance(os, folder_path, null);
    } catch (EngineRuntimeException ex) {
      if (ex.getExceptionCode() != ExceptionCode.E_OBJECT_NOT_FOUND) {
        throw ex;
      }
    }
    return folder;
  }

  private Folder instantiateFolder(ObjectStore os, Folder parent, String name) {
    Folder folder = null;
    try {
      folder = Factory.Folder.createInstance(os, null);
      folder.set_Parent(parent);
      folder.set_FolderName(name);
      folder.save(RefreshMode.NO_REFRESH);
    } catch (EngineRuntimeException ex) {
      // Create failed.  See if it's because the folder exists.
      ExceptionCode code = ex.getExceptionCode();
      if (code != ExceptionCode.E_NOT_UNIQUE) {
        throw ex;
      }

      //no R/T
      folder = Factory.Folder.getInstance(os, null, parent.get_PathName() + "/" + name);
    }
    return folder;
  }
}

