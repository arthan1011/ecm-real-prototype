package ru.atc.sbrf.ecmcore.webapi;


import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.codehaus.jackson.map.ObjectMapper;
import ru.atc.sbrf.ecmcore.domain.UserSession;
import ru.atc.sbrf.ecmcore.message.Response;
import ru.atc.sbrf.ecmcore.message.document.AddDocumentRequest;
import ru.atc.sbrf.ecmcore.message.ticket.AcquireTicketRequest;
import ru.atc.sbrf.ecmcore.message.ticket.AcquireTicketResponse;
import ru.atc.sbrf.ecmcore.repository.UserSessionRepositoryFactory;
import ru.atc.sbrf.ecmcore.service.DocumentService;
import ru.atc.sbrf.ecmcore.service.TicketService;
import ru.atc.sbrf.ecmcore.service.UiService;
import ru.atc.sbrf.ecmcore.service.UserSessionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by vkoba on 19.08.2015.
 */
@Path("/webapi")
public class WebApi {
  Logger LOGGER = Logger.getLogger(WebApi.class);
  UserSessionService userSessionService;
  TicketService ticketService;
  DocumentService documentService;
  UiService uiService;

  public WebApi(UserSessionService userSessionService, TicketService ticketService, DocumentService documentService, UiService uiService) {
    this.userSessionService = userSessionService;
    this.ticketService = ticketService;
    this.documentService = documentService;
    this.uiService = uiService;
  }

  public WebApi() {
    this.userSessionService = new UserSessionService();
    this.ticketService = new TicketService();
    this.documentService = new DocumentService();
    this.uiService = new UiService();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/acquireTicket")
  public Response acquireTicket(AcquireTicketRequest ticketRequest) {
    String params = "";
    try {
      params = parseParams(ticketRequest);
      AcquireTicketResponse ticketResponse = new AcquireTicketResponse();
      String ticket = ticketService.generateTicket();
      ticketResponse.setTicket(ticket);
      userSessionService.addSession(ticket, ticketRequest);
      return doResponse("acquireTicket", params, ticketResponse, null, null);
    } catch (Exception e) {
      return doResponse("acquireTicket", params, null, "Ошибка получения тикета", e);
    }
  }


  @GET
  @Path("/ui/gridcolumns")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getGridColumnsHierarchy(@HeaderParam("ECM-Ticket") String ticket) {
    try {
      UserSession userSession = UserSessionRepositoryFactory.getUserSessionRepository().getUserSession(ticket);
      return doResponse("getGridColumnsHierarchy", String.format("ticket:%s", ticket), uiService.getGridColumns(userSession), null, null);
    } catch (Exception e) {
      return doResponse("getGridColumnsHierarchy", String.format("ticket:%s", ticket), null, "Ошибка получения названий колонок для таблицы", e);
    }
  }

  @GET
  @Path("/ui/hierarchy")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getHierarchy(@HeaderParam("ECM-Ticket") String ticket) {
    try {
      UserSession userSession = UserSessionRepositoryFactory.getUserSessionRepository().getUserSession(ticket);
      return doResponse("getHierarchy", String.format("ticket:%s", ticket), uiService.getHierarchy(userSession), null, null);
    } catch (Exception e) {
      return doResponse("getHierarchy", String.format("ticket:%s", ticket), null, "Ошибка получения иерархии папок", e);
    }
  }

  @GET
  @Path("/ui/buttons")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getFunctionButtons(@HeaderParam("ECM-Ticket") String ticket) {
    try {
      UserSession userSession = UserSessionRepositoryFactory.getUserSessionRepository().getUserSession(ticket);
      return doResponse("getFunctionButtons", String.format("ticket:%s", ticket), uiService.getFunctionButtons(userSession), null, null);
    } catch (Exception e) {
      return doResponse("getFunctionButtons", String.format("ticket:%s", ticket), null, "Ошибка получения функциональных кнопок", e);
    }
  }

  @GET
  @Path("/permissions")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getFolderPermissions(@HeaderParam("ECM-Ticket") String ticket, @QueryParam("folderPath") String folderPath) {
    try {
      UserSession userSession = UserSessionRepositoryFactory.getUserSessionRepository().getUserSession(ticket);
      return doResponse("getFolderPermissions", String.format("ticket:%s, folderPath:%s", ticket, folderPath), uiService.getFolderPermissions(userSession, folderPath), null, null);
    } catch (Exception e) {
      return doResponse("getFolderPermissions", String.format("ticket:%s, folderPath:%s", ticket, folderPath), null, "Ошибка получения прав на папку", e);
    }
  }


  @POST
  @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  @Path("/documents")
  public Response addDocument(AddDocumentRequest documentRequest) {
    String params = "";
    try {
      params = parseParams(documentRequest);
      documentService.addDocument(documentRequest.getDocument(), documentRequest.getFolderPath());
      return doResponse("addDocument", params, "created", null, null);
    } catch (Exception e) {
      return doResponse("addDocument", params, null, "Ошибка создания документа!", e);
    }
  }

  @GET
  @Path("/documents/{docId}/content")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getBase64DocumentContent(@PathParam("docId") String docId) {
    try {
      return doResponse("getBase64DocumentContent", String.format("docId:%s", docId), documentService.getBase64Content(docId), null, null);
    } catch (Exception e) {
      return doResponse("getBase64DocumentContent", String.format("docId:%s", docId), null, "Ошибка получения контента документа", e);
    }
  }

  @GET
  @Path("/documents/{docId}/properties")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getDocumentProperties(@PathParam("docId") String docId) {
    try {
      return doResponse("getDocumentProperties", String.format("docId:%s", docId), documentService.getDocumentProperties(docId), null, null);
    } catch (Exception e) {
      return doResponse("getDocumentProperties", String.format("docId:%s", docId), null, "Ошибка получения свойств документа", e);
    }
  }

  @PUT
  @Path("/documents/{docId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response updateDocumentProperties(@PathParam("docId") String docId, AddDocumentRequest documentRequest) {
    String params = "";
    try {
      params = parseParams(documentRequest);
      documentService.updateDocument(docId, documentRequest.getDocument());
      return doResponse("updateDocumentProperties", params, "updated", null, null);
    } catch (Exception e) {
      return doResponse("updateDocumentProperties", params, null, "Ошибка обновления свойств документа!", e);
    }
  }

  @DELETE
  @Path("/documents/{docId}")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response deleteDocument(@PathParam("docId") String docId) {
    try {
      documentService.deleteDocument(docId);
      return doResponse("deleteDocument", "docId: " + docId, "deleted", null, null);
    } catch (Exception e) {
      return doResponse("deleteDocument", "docId: " + docId, null, "Ошибка удаления документа!", e);
    }
  }

  @GET
  @Path("/classes")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getClassesByFolder(@HeaderParam("ECM-Ticket") String ticket, @QueryParam("folderId") String folderPath) {
    try {
      UserSession userSession = UserSessionRepositoryFactory.getUserSessionRepository().getUserSession(ticket);
      return doResponse("getClassesByFolder", String.format("folderPath:%s", folderPath), documentService.getClasses(userSession, folderPath), null, null);
    } catch (Exception e) {
      return doResponse("getClassesByFolder", String.format("folderPath:%s", folderPath), null, "Ошибка получения классов из папки", e);
    }
  }

  @GET
  @Path("/classes/{className}/properties")
  @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
  public Response getClassProperties(@PathParam("className") String className) {
    try {
      return doResponse("getClassProperties", String.format("className:%s", className), documentService.getClassProperties(className), null, null);
    } catch (Exception e) {
      return doResponse("getClassProperties", String.format("className:%s", className), null, "Ошибка получения классов из папки", e);
    }
  }

  @GET
  @Path("/download")
  public javax.ws.rs.core.Response getPDF() throws IOException, COSVisitorException {

    PDDocument pdDocument = new PDDocument();
    PDPage page = new PDPage();
    pdDocument.addPage(page);

    PDFont font = PDType1Font.HELVETICA_BOLD;

    PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page);
    contentStream.beginText();
    contentStream.setFont( font, 12 );
    contentStream.moveTextPositionByAmount( 100, 700 );
    contentStream.drawString( "Hello World" );
    contentStream.endText();
    contentStream.close();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    pdDocument.save("new.pdf");
    pdDocument.save(out);
    pdDocument.close();

    return javax.ws.rs.core.Response.ok(Base64.encodeBase64String(out.toByteArray()))
            .build();
  }


  public TicketService getTicketService() {
    return ticketService;
  }

  public void setTicketService(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  public UiService getUiService() {
    return uiService;
  }

  public void setUiService(UiService uiService) {
    this.uiService = uiService;
  }

  public UserSessionService getUserSessionService() {
    return userSessionService;
  }

  public void setUserSessionService(UserSessionService userSessionService) {
    this.userSessionService = userSessionService;
  }


  private String parseParams(Object ticketRequest) throws IOException {
    ObjectMapper om = new ObjectMapper();
    String params = om.writeValueAsString(ticketRequest);
    return params;
  }

  private Response doResponse(String methodName, String params, Object result, String humanError, Exception e) {
    try {
      LOGGER.trace("-> " + methodName);
      LOGGER.trace("Params:" + params);
      if (humanError == null) {
        return new Response(result, null);
      } else {
        LOGGER.trace(methodName + " failed. Details:", e);
        return new Response(null, humanError);
      }
    } finally {
      LOGGER.trace("<- " + methodName);
    }
  }
}
