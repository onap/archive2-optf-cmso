/*
 * Copyright © 2020 Nokia.
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

package org.onap.optf.cmso.aaf;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AafAuthProviderTest {

    public static final String USER_NAME = "someUserName";
    public static final String SESSION_NOT_EXIST = null;
    @Mock
    Environment env;

    @Mock
    AafClientCache clientCache;

    @InjectMocks
    AafAuthProvider aafAuthProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private WebAuthenticationDetails webAuthenticationDetails;

    @Test
    public void shouldRejectAuthenticationWhenUserIsUnauthenticated() {
        // given
        makeAafAuthenticationActive();
        String name = USER_NAME;
        String password = "invalidPass";
        mockAuthenticationInstance(name, password, null);
        rejectUserAuthentication(name, password);

        // when/then
        Assertions.assertThat(aafAuthProvider.authenticate(authentication)).isNull();
    }

    @Test
    public void shouldAuthenticateUser() {
        // given
        makeAafAuthenticationActive();
        when(webAuthenticationDetails.getRemoteAddress()).thenReturn("remoteAddress");
        when(webAuthenticationDetails.getSessionId()).thenReturn("123");
        String name = USER_NAME;
        String password = "properPassword";
        mockAuthenticationInstance(name, password, webAuthenticationDetails);
        acceptUserAuthentication(name, password);
        // when/then
        Assertions.assertThat(aafAuthProvider.authenticate(authentication)).isNotNull();
    }

    @Test
    public void shouldSupportUsernamePasswordAuthenticationToken() {
        // when/then
        Assertions.assertThat(aafAuthProvider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
    }

    private void makeAafAuthenticationActive() {
        when(env.getProperty(AafProperties.aafEnabled.toString(), Boolean.class, true)).thenReturn(true);
    }

    private void rejectUserAuthentication(String name, String password) {
        when(clientCache.authenticate(name, password, SESSION_NOT_EXIST)).thenReturn(AafClientCache.AuthorizationResult.AuthenticationFailure);
    }

    private void acceptUserAuthentication(String name, String password) {
        when(clientCache.authenticate(name, password, "remoteAddress:123")).thenReturn(AafClientCache.AuthorizationResult.Authenticated);
    }

    private void mockAuthenticationInstance(String name, String password, WebAuthenticationDetails webAuthenticationDetails) {
        when(authentication.getName()).thenReturn(name);
        when(authentication.getCredentials()).thenReturn(password);
        when(authentication.getDetails()).thenReturn(webAuthenticationDetails);
    }


}
