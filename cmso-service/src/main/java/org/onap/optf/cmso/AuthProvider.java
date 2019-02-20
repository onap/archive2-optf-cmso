/*
 * Copyright © 2018 AT&T Intellectual Property.
 * Modifications Copyright © 2019 IBM.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         https://creativecommons.org/licenses/by/4.0/
 * 
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.onap.optf.cmso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Profile(SpringProfiles.PROPRIETARY__AUTHENTICATION)

public class AuthProvider
  implements AuthenticationProvider {
 
	@Autowired
	Environment env;
	
    @Override
    public Authentication authenticate(Authentication authentication) {
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
