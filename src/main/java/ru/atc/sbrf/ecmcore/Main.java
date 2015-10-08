package ru.atc.sbrf.ecmcore;


import javafx.util.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import ru.atc.sbrf.ecmcore.domain.*;
import ru.atc.sbrf.ecmcore.message.Response;
import ru.atc.sbrf.ecmcore.message.document.AddDocumentRequest;
import ru.atc.sbrf.ecmcore.message.ticket.AcquireTicketRequest;
import ru.atc.sbrf.ecmcore.message.ticket.AcquireTicketResponse;
import ru.atc.sbrf.ecmcore.service.DocumentService;
import ru.atc.sbrf.ecmcore.service.UiService;
import ru.atc.sbrf.ecmcore.util.FolderPermissionsGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vkoba on 11.08.2015.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        AccessTemplate accessTemplate = new AccessTemplate();

        accessTemplate.setAvailableReports(new ArrayList<String>());


        //accessTemplate.setSearchPaths(Arrays.asList("SearchPath1", "SearchPath2", "SearchPath3"));

        accessTemplate.setGridColumns(Arrays.asList(new GridColumn("DocumentTitle", "Имя", "60"), new GridColumn("size", "Размер", "20"), new GridColumn("checked", "Проверен?", "20")));
        List<Pair<String, String>> folders = Arrays.asList(
                new Pair<String, String>("ГКХ/Действующий/ПРОМОТХОДЫ.ООО.7722737438.772201001/ЮД/Общие/Свидетельство или Уведомление о постановке на учет в налоговом органе/Свидетельство или Уведомление о постановке на учет в налоговом органе.2015.03.14", "CertificateOfRegistrationWithTheTaxAuthority"),
                new Pair<String, String>("ГКХ/Действующий/ПРОМОТХОДЫ.ООО.7722737438.772201001/ЮД/Общие/Свидетельство о государственной регистрации/Свидетельство о государственной регистрации.2015.03.14", "CertificateOfTheStageRegistration"),
                new Pair<String, String>("ГКХ/Действующий/ПРОМОТХОДЫ.ООО.7722737438.772201001/ЮД/Общие/Устав/Устав.2015.03.14", "Charter"),
                new Pair<String, String>("ГКХ/Действующий/ПРОМОТХОДЫ.ООО.7722737438.772201001/ЮД/Общие/Учредительный договор/Учредительный договор.2015.03.14", "Memorandum"),
                new Pair<String, String>("ГКХ/Действующий/ПРОМОТХОДЫ.ООО.7722737438.772201001/ЮД/Счета/Счет № 40702810102100141398/Заявление об открытии банковского счета/Заявление об открытии банковского счета.2015.03.14", "ApplicationForOpeningABankAccount"),
                new Pair<String, String>("ГКХ/Действующий/ПРОМОТХОДЫ.ООО.7722737438.772201001/ЮД/Счета/Счет № 40702810102100141398/Карточка образцов подписей и оттисков печатей/Карточка образцов подписей и оттисков печатей.2015.03.14", "cardOfSpecimenSignaturesAndStamps"));
        accessTemplate.setFoldersConfiguration(FolderPermissionsGenerator.generate(folders));
        accessTemplate.setFunctionButtons(Arrays.asList(
                FunctionButton.ADD,
                FunctionButton.EDIT,
                FunctionButton.PROP,
                FunctionButton.SIGNATURE,
                FunctionButton.SCANADD,
                FunctionButton.RESCAN,
                FunctionButton.CHECKOUT,
                FunctionButton.CANCEL_CHECKOUT,
                FunctionButton.CHECKIN,
                FunctionButton.ADDFOLDER,
                FunctionButton.ADDLINK,
                FunctionButton.DELETE,
                FunctionButton.PRINT,
                FunctionButton.SEARCH,
                FunctionButton.REPORTS));
        accessTemplate.setSearchTypes(Arrays.asList("BankAccountContract",
                "CertificateOfTheStageRegistration",
                "OrganizationChart"));
        accessTemplate.setSearchPaths(Arrays.asList("/ББМО"));
        accessTemplate.setGridColumns(Arrays.asList(new GridColumn("DocumentTitle", "Имя", "60"), new GridColumn("size", "Размер", "20"), new GridColumn("checked", "Проверен?", "20")));
        //accessTemplate.setFoldersConfiguration(Arrays.asList(new FolderConfigutarion("folder/path1", Arrays.asList(FolderPermission.ADD_DOCUMENT,
        //                FolderPermission.READ), Arrays.asList(new FnClassNode("BankAccountContract", "Договор банковского счета"), new FnClassNode("CertificateOfTheStageRegistration", "Свидетельство о государственной регистрации"))),
        //        new FolderConfigutarion("folder/path1", Arrays.asList(FolderPermission.ADD_DOCUMENT), Arrays.asList(new FnClassNode("BankAccountContract", "Договор банковского счета")))));
        UserSession userSession = new UserSession();
        userSession.setAccessTemplate(accessTemplate);
        userSession.setUserName("p8admin");
        userSession.setRootFolderPath("/ББМО/Документы");

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ObjectMapper om = new ObjectMapper();
        UiService uiService = new UiService();
        DocumentService documentService = new DocumentService();
        System.out.println(om.writeValueAsString(accessTemplate));
        FnDocument fnd = new FnDocument();
        fnd.setContent(new FnContent("base64content", "application/pdf", "C2070-981.pdf"));
        fnd.setClassName("EGRUL");
        fnd.setProperties(Arrays.asList(new FnProperty("Pname1", "Ptype1", "value1"), new FnProperty("Pname2", "Ptype2", "value2")));
        AddDocumentRequest addDocumentRequest = new AddDocumentRequest();
        addDocumentRequest.setDocument(fnd);
        addDocumentRequest.setFolderPath("/BBMO/Folder1");
//        System.out.println(om.writeValueAsString(uiService.getGridColumns(userSession)));
//        System.out.println(om.writeValueAsString(uiService.getHierarchy(userSession)));
//        System.out.println(om.writeValueAsString(uiService.getFunctionButtons(userSession)));
//        System.out.println(om.writeValueAsString(documentService.getBase64Content("{0E84CE52-58E2-4325-9334-C9212610AAAA}")));
//        System.out.println(om.writeValueAsString(documentService.getDocumentProperties("{0E84CE52-58E2-4325-9334-C9212610AAAA}")));
//        System.out.println(om.writeValueAsString(new Response(null, "Ошибканах")));
//        System.out.println(om.writeValueAsString(new Response(fnd.getProperties(), null)));
//        System.out.println(Base64.encodeBase64String(IOUtils.toByteArray(new FileInputStream("C:\\Users\\vkoba\\Desktop\\Мусор\\C2070-981 V12.35.pdf"))));
        AcquireTicketRequest ticket = new AcquireTicketRequest("extSystenUd", "user", "/BBMO", "editor");
        AcquireTicketResponse atr = new AcquireTicketResponse();
        atr.setTicket("14989235809880983");
        Response r = new Response(uiService.getFolderPermissions(userSession, "/folder/path1"), null);
//        System.out.println(om.writeValueAsString(addDocumentRequest));
//        System.out.println(om.writeValueAsString(r));


//        System.out.println(new String(Base64.encode(IOUtils.toByteArray(new FileInputStream("C:\\Users\\akhovanskii\\Downloads\\pdf_manual.pdf"))), "UTF-8"));

        System.out.println(Boolean.valueOf("true"));


    }


}
