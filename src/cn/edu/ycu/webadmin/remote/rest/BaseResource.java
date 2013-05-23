package cn.edu.ycu.webadmin.remote.rest;

import java.util.logging.Handler;
import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;



public class BaseResource extends ServerResource
{
   private static final Logger logger = Logger.getLogger(BaseResource.class);

   private static TargetClass targetClass = null;

   private static URIResource uriResource = null;

   private Long itemId = null;

   public BaseResource()
   {
   }
   
/*   static{
	   java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
	      Handler[] handlers = rootLogger.getHandlers();
	      rootLogger.removeHandler(handlers[0]);
	      SLF4JBridgeHandler.install();
	      PropertyConfigurator.configure("src/log4j.properties");
   }*/
  @Override 
 public void init(Context context, Request request, Response response)  {
      logger.debug("Entering init method...");
      if (uriResource != null){
    	  System.out.println("enter into if-case");
    	   try
         {
            targetClass = new TargetClass(uriResource);
         }
         catch (Exception e)
         {
            throw new java.lang.IllegalArgumentException(e.getMessage());
         }
      }
        
      logger.debug("Leaving init method...");
   }

   public URIResource getUriResource()
   {
      return uriResource;
   }

   public void setUriResource(URIResource uriResource)
   {
      this.uriResource = uriResource;
   }

   /**
    * Process HTTP GET request.
    */
   @Override
   protected Representation get()
      throws ResourceException
   {
      logger.debug("Entering get method.");
      parseItemId();
      StringRepresentation rep = null;
      if (targetClass != null)
      {
         try
         {
            if (itemId != null)
               rep = new StringRepresentation(targetClass.get(itemId));
            else
               rep = new StringRepresentation(targetClass.get());
         }
         catch (Exception e)
         {
            RESTUtil.logErrorStack(logger, e);
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
            rep = new StringRepresentation(e.getMessage());
         }
      }
      else
      {
         logger.warn("No uri resource found for the request.");
         this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
         rep = new StringRepresentation("No uri resource found for the request.");
      }

      logger.debug("Leaving get method with return: " + rep);
      return rep;
   }

   /**
    * Process HTTP POST request
    */
   @Override
   protected Representation post(Representation entity)
      throws ResourceException
   {
      logger.debug("Entering post method with param: " + entity);

      StringRepresentation rep = null;

      if (targetClass != null)
      {
         try
         {
            rep = new StringRepresentation(targetClass.post(entity.getText()));
         }
         catch (Exception e)
         {
            RESTUtil.logErrorStack(logger, e);
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
            rep = new StringRepresentation(e.getMessage());
         }
      }
      else
      {
         logger.warn("No uri resource found for the request.");
         this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
         rep = new StringRepresentation("No uri resource found for the request.");
      }

      logger.debug("Leaving post method with return value: " + rep);
      return rep;
   }

   /**
    * Process HTTP PUT request
    */
   @Override
   protected Representation put(Representation entity)
      throws ResourceException
   {
      logger.debug("Entering put method with param: " + entity);
      parseItemId();
      StringRepresentation rep = null;
      if (targetClass != null)
      {
         try
         {
            if (itemId != null)
               rep = new StringRepresentation(targetClass.put(itemId));
            else
               rep = new StringRepresentation(targetClass.put(entity.getText()));
         }
         catch (Exception e)
         {
            RESTUtil.logErrorStack(logger, e);
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
            rep = new StringRepresentation(e.getMessage());
         }
      }
      else
      {
         logger.warn("No uri resource found for the request.");
         this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
         rep = new StringRepresentation("No uri resource found for the request.");
      }
      logger.debug("Leaving put method with return value: " + rep);
      return rep;
   }

   /**
    * process HTTP DELETE request
    */
   @Override
   protected Representation delete()
      throws ResourceException
   {
      logger.debug("Entering delete method.");
      StringRepresentation rep = null;
      parseItemId();
      if (itemId != null)
      {
         if (targetClass != null)
         {
            try
            {
               rep = new StringRepresentation(targetClass.delete(itemId));
            }
            catch (Exception e)
            {
               RESTUtil.logErrorStack(logger, e);
               this.setStatus(Status.SERVER_ERROR_INTERNAL);
               rep = new StringRepresentation(e.getMessage());
            }
         }
         else
         {
            logger.warn("No uri resource found for the request.");
            this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            rep = new StringRepresentation("No uri resource found for the request.");
         }
      }
      else
      {
         this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
         rep = new StringRepresentation("The requested item id is not correct.");
      }
      logger.debug("Leaving delete method with return value: " + rep);
      return rep;
   }

   private void parseItemId()
   {
      logger.debug("Entering parseItemId method.");
      try
      {
         Object action = getRequest().getAttributes().get("id");
         if (action != null)
         {
            itemId = Long.parseLong(action.toString());
         }
      }
      catch (NumberFormatException ex)
      {
         RESTUtil.logErrorStack(logger, ex);
      }
      logger.debug("Leaving parseItemId method, with itemId: " + itemId);
   }

}
