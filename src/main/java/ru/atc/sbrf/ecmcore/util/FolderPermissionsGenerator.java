package ru.atc.sbrf.ecmcore.util;

import javafx.util.Pair;
import ru.atc.sbrf.ecmcore.domain.FnClassNode;
import ru.atc.sbrf.ecmcore.domain.FolderConfigutarion;
import ru.atc.sbrf.ecmcore.domain.FolderPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ataranko on 01.10.2015.
 */
public class FolderPermissionsGenerator {
    public static List<FolderConfigutarion> generate(List<Pair<String, String>> folderNames) {
        List<FolderConfigutarion> permissions = new ArrayList<FolderConfigutarion>();
        for (Pair<String, String> folderName : folderNames) {
            permissions.add(new FolderConfigutarion(folderName.getKey(), Arrays.asList(
                    FolderPermission.ADD_DOCUMENT,
                    FolderPermission.READ,
                    FolderPermission.EDIT_DOCUMENT_PROP,
                    FolderPermission.EDIT_CONTENT_VERSION,
                    FolderPermission.EDIT_CONTENT,
                    FolderPermission.DELETE_DOCUMENT,
                    FolderPermission.ADD_LINK,
                    FolderPermission.DELETE_LINK,
                    FolderPermission.SIGN_DOCUMENT),
                    Arrays.asList(new FnClassNode(folderName.getValue(), folderName.getKey().substring(folderName.getKey().lastIndexOf("/") + 1)))));
        }
        return permissions;
    }
}
