/*
 * Copyright © 2019 IBM Intellectual Property.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.optf.cmso.topology.AuthProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RunWith(MockitoJUnitRunner.class)
public class AuthProviderTest {

    @Test
    public void authenticate() {
        String principal = "testName";
        String credential = "testPassword";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(principal);
        when(authentication.getCredentials()).thenReturn(credential);
        AuthProvider authProvider = new AuthProvider();
        Authentication auth = authProvider.authenticate(authentication);
        assertEquals(principal, auth.getPrincipal());
        assertEquals(credential, auth.getCredentials());
    }

    @Test
    public void supports() {
        AuthProvider authProvider = new AuthProvider();
        assertTrue(authProvider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(authProvider.supports(Authentication.class));
    }
}