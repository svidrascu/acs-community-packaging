<%--
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
--%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page buffer="32kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ page import="org.alfresco.web.ui.common.PanelGenerator" %>


<f:verbatim>
<script type="text/javascript">
   window.onload = pageLoaded;
   
   function pageLoaded()
   {
      document.getElementById("dialog:finish-button").onclick = showProgress;
   }
   
   function showProgress()
   {
      document.getElementById('progress').style.display = 'block';
   }
</script>

<div id="progress" style="margin-left: 90px; margin-top: 4px; margin-bottom: 4px; display: none">
   <img src="<%=request.getContextPath()%>/images/icons/process_animation.gif" width=174 height=14>
</div>

 <%-- Details --%>
      <table cellpadding="2" cellspacing="2" border="0" width="100%">
         <tr>
            <td class="mainSubTitle"></f:verbatim><h:outputText value="#{msg.change_user_perms}" /><f:verbatim></td>
         </tr>
         <tr><td class="paddingRow"></td></tr>
         <tr>
            <td>1.&nbsp;</f:verbatim><h:outputText value="#{msg.select_perm}" /><f:verbatim></td>
         </tr>
         <tr>
            <td>
            </f:verbatim>
               <h:selectOneListbox id="perms" style="width:250px" size="5">
                  <f:selectItems value="#{DialogManager.bean.perms}" />
               </h:selectOneListbox>
               <f:verbatim>
            </td>
         </tr>
         <tr>
            <td>
               2.&nbsp;</f:verbatim><h:commandButton value="#{msg.add_to_list_button}" actionListener="#{DialogManager.bean.addPermission}" /><f:verbatim>
            </td>
         </tr>
         <tr><td class="paddingRow"></td></tr>
         <tr>
            <td></f:verbatim><h:outputText value="#{msg.selected_perm}" /><f:verbatim></td>
         </tr>
         <tr>
            <td>
            </f:verbatim>
               <h:dataTable value="#{DialogManager.bean.personPermsDataModel}" var="row"
                            rowClasses="selectedItemsRow,selectedItemsRowAlt"
                            styleClass="selectedItems" headerClass="selectedItemsHeader"
                            cellspacing="0" cellpadding="4" 
                            rendered="#{DialogManager.bean.personPermsDataModel.rowCount != 0}">
                  <h:column>
                     <f:facet name="header">
                        <h:outputText value="#{msg.name}" />
                     </f:facet>
                     <h:outputText value="#{row.role}" />
                  </h:column>
                  <h:column>
                     <a:actionLink actionListener="#{DialogManager.bean.removePermission}" image="/images/icons/delete.gif"
                                   value="#{msg.remove}" showLink="false" style="padding-left:6px" />
                  </h:column>
               </h:dataTable>
               <a:panel id="no-items" rendered="#{DialogManager.bean.personPermsDataModel.rowCount == 0}">
               <f:verbatim>
                  <table cellspacing='0' cellpadding='2' border='0' class='selectedItems'>
                     <tr>
                        <td colspan='2' class='selectedItemsHeader'></f:verbatim><h:outputText id="no-items-name" value="#{msg.name}" /><f:verbatim></td>
                     </tr>
                     <tr>
                        <td class='selectedItemsRow'></f:verbatim><h:outputText id="no-items-msg" value="#{msg.no_selected_items}" /><f:verbatim></td>
                     </tr>
                  </table>
                  </f:verbatim>
               </a:panel>
               <f:verbatim>
            </td>
         </tr>
         
         <tr><td colspan=2 class="paddingRow"></td></tr>
         </f:verbatim>
         
         <f:verbatim>
         
      </table>
     
</f:verbatim>