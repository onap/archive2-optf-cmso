package org.onap.optf.cmso;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider
  implements AuthenticationProvider {
 
	@Autowired
	Environment env;
	
    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
    	org.springframework.security.web.authentication.www.BasicAuthenticationFilter f = null;
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        //TODO check credentials until we enable AAF 
        return new UsernamePasswordAuthenticationToken(
          name, password, new ArrayList<>());
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
          UsernamePasswordAuthenticationToken.class);
    }
}
