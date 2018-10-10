package org.onap.optf.cmso;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

public class MyRequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter
{

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);

        // see comments in Servlet API around using sendError as an alternative
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
