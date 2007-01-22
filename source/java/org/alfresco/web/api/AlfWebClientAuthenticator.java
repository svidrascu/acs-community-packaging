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
package org.alfresco.web.api;

import javax.servlet.ServletContext;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.web.app.servlet.AuthenticationHelper;
import org.alfresco.web.app.servlet.AuthenticationStatus;
import org.alfresco.web.app.servlet.BaseServlet;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Alfresco Web Client Authentication Interceptor
 * 
 * @author davidc
 */
public class AlfWebClientAuthenticator implements MethodInterceptor, APIContextAware
{
    // Logger
    private static final Log logger = LogFactory.getLog(AlfWebClientAuthenticator.class);

    // dependencies
    private ServletContext context;
    private AuthenticationService authenticationService;

    
    /* (non-Javadoc)
     * @see org.alfresco.web.api.APIContextAware#setAPIContext(javax.servlet.ServletContext)
     */
    public void setAPIContext(ServletContext context)
    {
        this.context = context;
    }
    
    /**
     * @param authenticationService
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation)
        throws Throwable
    {
        String currentUser = null;
        Object retVal = null;
        Object[] args = invocation.getArguments();
        APIRequest request = (APIRequest)args[0];
        APIResponse response = (APIResponse)args[1];
        APIService service = (APIService)invocation.getThis();
        AuthenticationStatus status = null;

        try
        {
            
            //
            // Determine if user already authenticated
            //
            
            currentUser = AuthenticationUtil.getCurrentUserName();
            if (logger.isDebugEnabled())
                logger.debug("Current authentication: " + (currentUser == null ? "unauthenticated" : "authenticated as " + currentUser));
    
            //
            // validate credentials
            // 
    
            String ticket = request.getParameter("ticket");
            boolean isGuest = request.isGuest();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Service authentication required: " + service.getRequiredAuthentication());
                logger.debug("Guest login: " + isGuest);
                logger.debug("Ticket provided: " + (ticket != null && ticket.length() > 0));
            }
        
            if (ticket != null && ticket.length() > 0)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Authenticating ticket " + ticket);

                status = AuthenticationHelper.authenticate(context, request, response, ticket);
            }
            else
            {
                if (isGuest && service.getRequiredAuthentication() == APIRequest.RequiredAuthentication.Guest)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Authenticating as Guest");

                    status = AuthenticationHelper.authenticate(context, request, response, true);
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Authenticating session");

                    status = AuthenticationHelper.authenticate(context, request, response, false);
                }
            }

            //
            // execute API service or request authorization
            //
            
            if (status != null && status != AuthenticationStatus.Failure)
            {
                retVal = invocation.proceed();
            }
            else
            {
                // authentication failed - now need to display the login page to the user, if asked to
                if (logger.isDebugEnabled())
                    logger.debug("Redirecting to Alfresco Login");

                BaseServlet.redirectToLoginPage(request, response, context);
            }
        }
        finally
        {
            if (status != null && status != AuthenticationStatus.Failure)
            {
                authenticationService.clearCurrentSecurityContext();
                if (currentUser != null)
                {
                    AuthenticationUtil.setCurrentUser(currentUser);
                }
                
                if (logger.isDebugEnabled())
                    logger.debug("Authentication reset: " + (currentUser == null ? "unauthenticated" : "authenticated as " + currentUser));
            }
        }
        
        return retVal;        
    }

}