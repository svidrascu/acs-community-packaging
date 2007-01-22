/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.forms;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * XML utility functions.
 * 
 * @author Ariel Backenroth
 */
public class XMLUtil
{   

   private static final Log LOGGER = LogFactory.getLog(XMLUtil.class);

   private static DocumentBuilder documentBuilder;

   /** utility function for creating a document */
   public static Document newDocument()
   {
      return XMLUtil.getDocumentBuilder().newDocument();
   }

   /** utility function for serializing a node */
   public static void print(final Node n, final Writer output)
   {
      XMLUtil.print(n, output, true);
   }
   
   /** utility function for serializing a node */
   public static void print(final Node n, final Writer output, final boolean indent)
   {
      try 
      {
         final TransformerFactory tf = TransformerFactory.newInstance();
         final Transformer t = tf.newTransformer();
         t.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
         
         if (LOGGER.isDebugEnabled())
         {
            LOGGER.debug("writing out a document for " + 
      			 (n instanceof Document
      			  ? ((Document)n).getDocumentElement()
      			  : n).getNodeName() + 
   			     " to " + (output instanceof StringWriter
                                       ? "string"
                                       : output));
         }
         t.transform(new DOMSource(n), new StreamResult(output));
      }
      catch (TransformerException te)
      {
         te.printStackTrace();
         assert false : te.getMessage();
      }
   }
   
   /** utility function for serializing a node */
   public static void print(final Node n, final File output)
      throws IOException
   {
      XMLUtil.print(n, new FileWriter(output));
   }
   
   /** utility function for serializing a node */
   public static String toString(final Node n)
   {
      return XMLUtil.toString(n, true);
   }

   /** utility function for serializing a node */
   public static String toString(final Node n, final boolean indent)
   {
      final StringWriter result = new StringWriter();
      XMLUtil.print(n, result, indent);
      return result.toString();
   }
   
   /** utility function for parsing xml */
   public static Document parse(final String source)
      throws SAXException,
      IOException
   {
      return XMLUtil.parse(new ByteArrayInputStream(source.getBytes()));
   }
   
   /** utility function for parsing xml */
   public static Document parse(final NodeRef nodeRef,
                                final ContentService contentService)
      throws SAXException,
      IOException
   {
      final ContentReader contentReader = 
         contentService.getReader(nodeRef, ContentModel.TYPE_CONTENT);
      final InputStream in = contentReader.getContentInputStream();
      return XMLUtil.parse(in);
   }
   
   /** utility function for parsing xml */
   public static Document parse(final File source)
      throws SAXException,
      IOException
   {
      return XMLUtil.parse(new FileInputStream(source));
   }
   
   /** utility function for parsing xml */
   public static Document parse(final InputStream source)
      throws SAXException,
      IOException
   {
      final DocumentBuilder db = XMLUtil.getDocumentBuilder();
      
      final Document result = db.parse(source);
      source.close();
      return result;
   }

   /** provides a document builder that is namespace aware but not validating by default */
   public static DocumentBuilder getDocumentBuilder()
   {
      if (XMLUtil.documentBuilder == null)
      {
         XMLUtil.documentBuilder = XMLUtil.getDocumentBuilder(true, false);
      }
      return XMLUtil.documentBuilder;
   }

   public static DocumentBuilder getDocumentBuilder(final boolean namespaceAware,
                                                    final boolean validating)
   { 
      try
      {
         final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(namespaceAware);
         dbf.setValidating(validating);
         return dbf.newDocumentBuilder();
      }
      catch (ParserConfigurationException pce)
      {
         LOGGER.error(pce);
         return null;
      }
   }
}