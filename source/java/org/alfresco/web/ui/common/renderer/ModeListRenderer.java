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
package org.alfresco.web.ui.common.renderer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIListItem;
import org.alfresco.web.ui.common.component.UIMenu;
import org.alfresco.web.ui.common.component.UIModeList;

/**
 * @author kevinr
 */
public class ModeListRenderer extends BaseRenderer
{
   // ------------------------------------------------------------------------------
   // Renderer implemenation
   
   /**
    * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void decode(FacesContext context, UIComponent component)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = getHiddenFieldName(context, component);
      String value = (String)requestMap.get(fieldId);
      
      // we encoded the value to start with our Id
      if (value != null && value.startsWith(component.getClientId(context) + NamingContainer.SEPARATOR_CHAR))
      {
         // found a new selected value for this ModeList
         // queue an event to represent the change
         // TODO: NOTE: The value object is passed in as a String here - is this a problem?
         //             As the 'value' field for a ModeListItem can contain Object...  
         Object selectedValue = value.substring(component.getClientId(context).length() + 1);
         UIModeList.ModeListItemSelectedEvent event = new UIModeList.ModeListItemSelectedEvent(component, selectedValue);
         component.queueEvent(event);
      }
   }

   /**
    * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeBegin(FacesContext context, UIComponent component) throws IOException
   {
      if (component.isRendered() == false)
      {
         return;
      }
      
      UIModeList list = (UIModeList)component;
      
      ResponseWriter out = context.getResponseWriter();

      Map attrs = list.getAttributes();
      
      if (list.isMenu() == false)
      {
         // start outer table container the list items
         out.write("<table cellspacing=1 cellpadding=0");
         outputAttribute(out, attrs.get("styleClass"), "class");
         outputAttribute(out, attrs.get("style"), "style");
         outputAttribute(out, attrs.get("width"), "width");
         out.write('>');
         
         // horizontal rendering outputs a single row with each item as a column cell
         if (list.isHorizontal() == true)
         {
            out.write("<tr>");
         }
         
         // output title row if present
         if (list.getLabel() != null)
         {
            // each row is an inner table with a single row and 2 columns
            // first column contains an icon if present, second column contains text
            if (list.isHorizontal() == false)
            {
               out.write("<tr>");
            }
            
            out.write("<td><table cellpadding=0 width=100%");
            outputAttribute(out, attrs.get("itemSpacing"), "cellspacing");
            out.write("><tr>");
            
            // output icon column
            if (list.getIconColumnWidth() != 0)
            {
               out.write("<td");
               outputAttribute(out, list.getIconColumnWidth(), "width");
               out.write("></td>");
            }
            
            // output title label
            out.write("<td><span");
            outputAttribute(out, attrs.get("labelStyle"), "style");
            outputAttribute(out, attrs.get("labelStyleClass"), "class");
            out.write('>');
            out.write(Utils.encode(list.getLabel()));
            out.write("</span></td></tr></table></td>");
            
            if (list.isHorizontal() == false)
            {
               out.write("</tr>");
            }
         }
      }
      else
      {
         // render as a pop-up menu
         // TODO: show the image set for the individual item if available?
         out.write("<table cellspacing=0 cellpadding=0 style='white-space:nowrap'><tr>");
         String selectedImage = (String)attrs.get("selectedImage");
         if (selectedImage != null)
         {
            out.write("<td style='padding-right:4px'>");
            out.write(Utils.buildImageTag(context, selectedImage, null, "absmiddle"));
            out.write("</td>");
         }
         
         String menuId = UIMenu.getNextMenuId(list, context);
         out.write("<td><a href='#' onclick=\"javascript:_toggleMenu(event, '");
         out.write(menuId);
         out.write("');return false;\">");
         
         // use default label if available
         String label = list.getLabel();
         if (label == null || label.length() == 0)
         {
            // else get the child components and walk to find the selected
            for (Iterator i=list.getChildren().iterator(); i.hasNext(); /**/)
            {
               UIComponent child = (UIComponent)i.next();
               if (child instanceof UIListItem && child.isRendered() == true)
               {
                  // found a valid UIListItem child to render
                  UIListItem item = (UIListItem)child;
                  
                  // if selected render as the label
                  if (item.getValue().equals(list.getValue()) == true)
                  {
                     label = item.getLabel();
                     break;
                  }
               }
            }
         }
         
         // render the label
         if (label != null && label.length() != 0)
         {
            out.write("<span");
            outputAttribute(out, attrs.get("labelStyle"), "style");
            outputAttribute(out, attrs.get("labelStyleClass"), "class");
            out.write('>');
            out.write(Utils.encode(label));
            out.write("</span>");
         }
         
         // output image
         if (list.getMenuImage() != null)
         {
            out.write(Utils.buildImageTag(context, list.getMenuImage(), null, "absmiddle"));
         }
         
         out.write("</a></td></tr></table>");
         
         // output the hidden DIV section to contain the menu item table
         out.write("<div id='");
         out.write(menuId);
         out.write("' style=\"position:absolute;display:none;padding-left:2px;\">");
         
         // start outer table container the list items
         out.write("<table cellspacing=1 cellpadding=0");
         outputAttribute(out, attrs.get("styleClass"), "class");
         outputAttribute(out, attrs.get("style"), "style");
         outputAttribute(out, attrs.get("width"), "width");
         out.write('>');
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeChildren(FacesContext context, UIComponent component) throws IOException
   {
      if (component.isRendered() == false)
      {
         return;
      }
      
      UIModeList list = (UIModeList)component;
      Map attrs = list.getAttributes();
      
      ResponseWriter out = context.getResponseWriter();
      
      String selectedImage = (String)attrs.get("selectedImage");
      
      // get the child components
      for (Iterator i=list.getChildren().iterator(); i.hasNext(); /**/)
      {
         UIComponent child = (UIComponent)i.next();
         if (child instanceof UIListItem && child.isRendered() == true)
         {
            // found a valid UIListItem child to render
            UIListItem item = (UIListItem)child;
            
            // each row is an inner table with a single row and 2 columns
            // first column contains an icon if present, second column contains text
            if (list.isHorizontal() == false)
            {
               out.write("<tr>");
            }
            
            out.write("<td><table cellpadding=0 width=100%");
            outputAttribute(out, attrs.get("itemSpacing"), "cellspacing");
            
            // if selected value render different style for the item
            boolean selected = item.getValue().equals(list.getValue());
            if (selected == true)
            {
               outputAttribute(out, attrs.get("selectedStyleClass"), "class");
               outputAttribute(out, attrs.get("selectedStyle"), "style");
            }
            else
            {
               outputAttribute(out, attrs.get("itemStyleClass"), "class");
               outputAttribute(out, attrs.get("itemStyle"), "style");
            }
            out.write("><tr>");
            
            // output icon column
            if (list.getIconColumnWidth() != 0)
            {
               out.write("<td");
               outputAttribute(out, list.getIconColumnWidth(), "width");
               out.write(">");
               
               // if the "selectedImage" property is set and this item is selected then show it
               if (selected == true && selectedImage != null)
               {
                  out.write( Utils.buildImageTag(context, selectedImage, item.getTooltip()) );
               }
               else
               {
                  // else show the image set for the individual item 
                  String image = (String)child.getAttributes().get("image"); 
                  if (image != null)
                  {
                     out.write( Utils.buildImageTag(context, image, item.getTooltip()) );
                  }
               }
               
               out.write("</td>");
            }
            
            // output item link
            out.write("<td>");
            if (!list.isDisabled() && !item.isDisabled())
            {
               out.write("<a href='#' onclick=\"");
               // generate javascript to submit the value of the child component
               String value = list.getClientId(context) + NamingContainer.SEPARATOR_CHAR + (String)child.getAttributes().get("value");
               out.write(Utils.generateFormSubmit(context, list, getHiddenFieldName(context, list), value));
               out.write('"');
            }
            else
            {
               out.write("<span");
               outputAttribute(out, attrs.get("disabledStyleClass"), "class");
               outputAttribute(out, attrs.get("disabledStyle"), "style");
            }
            
            // render style for the item link
            if (item.getValue().equals(list.getValue()))
            {
               outputAttribute(out, attrs.get("selectedLinkStyleClass"), "class");
               outputAttribute(out, attrs.get("selectedLinkStyle"), "style");
            }
            else
            {
               outputAttribute(out, attrs.get("itemLinkStyleClass"), "class");
               outputAttribute(out, attrs.get("itemLinkStyle"), "style");
            }
            
            outputAttribute(out, child.getAttributes().get("tooltip"), "title");
            out.write('>');
            out.write(Utils.encode(item.getLabel()));
            if (!list.isDisabled() && !item.isDisabled())
            {
               out.write("</a>");
            }
            else
            {
               out.write("</span>");
            }
            out.write("</td></tr></table></td>");
            
            if (list.isHorizontal() == false)
            {
               out.write("</tr>");
            }
         }
      }
   }

   /**
    * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeEnd(FacesContext context, UIComponent component) throws IOException
   {
      if (component.isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // end outer table
      UIModeList list = (UIModeList)component;
      if (list.isHorizontal() == true)
      {
         out.write("</tr>");
      }
      out.write("</table>");
      if (list.isMenu() == true)
      {
         // close menu hidden div section
         out.write("</div>");
      }
   }

   /**
    * @see javax.faces.render.Renderer#getRendersChildren()
    */
   public boolean getRendersChildren()
   {
      return true;
   }
   
   /**
    * We use a hidden field name based on the parent form component Id and
    * the string "modelist" to give a hidden field name that can be shared by all
    * ModeList components within a single UIForm component.
    * 
    * @return hidden field name
    */
   private static String getHiddenFieldName(FacesContext context, UIComponent component)
   {
      UIForm form = Utils.getParentForm(context, component);
      return form.getClientId(context) + NamingContainer.SEPARATOR_CHAR + "modelist";
   }
}
