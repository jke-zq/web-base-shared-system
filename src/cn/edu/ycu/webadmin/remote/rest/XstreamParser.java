package cn.edu.ycu.webadmin.remote.rest;



import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.JVM;

public class XstreamParser
{
   private static final Logger logger = Logger.getLogger(XstreamParser.class);

   private XStream xstream = null;

   public XstreamParser(Map<String, String> uriMap)
   {
      logger.debug("Entering XstreamParser, with: " + uriMap.toString());
      if (xstream == null)
      {
         JVM jvm = new JVM();
         xstream = new XStream(jvm.bestReflectionProvider());
         Iterator<Entry<String, String>> iter = uriMap.entrySet().iterator();
         Entry<String, String> tmp = null;
         while (iter.hasNext())
         {
            tmp = iter.next();
            try
            {
               Class< ? > aliasClass = Class.forName(tmp.getValue());
               xstream.alias(tmp.getKey(), aliasClass);
            }
            catch (ClassNotFoundException e)
            {
               logger.warn(e.getMessage());
            }
         }
      }
      logger.debug("Leaving XstreamParser, with: " + xstream);
   }

   /**
    * 
    * @param c
    * @param xml
    * @return
    * @throws EdgeAppException
    */
   public Object fromXml(Class< ? > c, String xml)
      throws Exception
   {
      logger.debug("Entering fromXml, with: " + c + "," + xml);
      if (xstream == null)
         throw new Exception(" xstream is null!");
      Object rtn = xstream.fromXML(xml);
      if (!c.isInstance(rtn))
         throw new Exception("The xml is not an instance of :"
               + c.getCanonicalName());
      logger.debug("Leaving fromXml, with: " + rtn);
      return rtn;
   }

   /**
    * 
    * @param bodyStr
    * @return
    * @throws EdgeAppException
    */
   public Object fromXML(String bodyStr)
      throws Exception
   {
      logger.debug("Entering fromXml, with: " + bodyStr);
      if (xstream == null)
         throw new Exception(" xstream is null!");
      Object rtn = xstream.fromXML(bodyStr);
      logger.debug("Leaving fromXml, with: " + rtn);
      return rtn;
   }

   /**
    * 
    * @param obj
    * @param writer
    * @throws EdgeAppException
    */
   public String toXml(Object obj)
      throws Exception
   {
      logger.debug("Entering toXml, with: " + obj);
      StringBuffer buffer = new StringBuffer();
      if (xstream == null)
         throw new Exception(" xstream is null!");
      try
      {
         buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
         buffer.append(xstream.toXML(obj));
      }
      catch (Exception ex)
      {
         RESTUtil.logErrorStack(logger, ex);
      }
      logger.debug("Leaving toXml, with return value:" + buffer);
      return buffer.toString();
   }
}
