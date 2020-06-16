/*
 * Copyright (c) 2020 Nokia.
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@RunWith(MockitoJUnitRunner.class)
public class AafContainerFiltersTest {

    @Mock
    AafClientCache aafClientCache;

    @Mock
    ContainerRequestContext requestContext;

    @InjectMocks
    AafContainerFilters aafContainerFilters;

    @Test
    public void shouldReportUnauthorizedErrorWhenErrorOccursDuringFetchingDataFromAafCache() {

        // given
        Mockito.doThrow(new IllegalArgumentException("For JUnit only!"))
                .when(aafClientCache)
                .authorize(Mockito.any());

        // when/then
        assertThatThrownBy(() -> aafContainerFilters.filter(requestContext))
                .isInstanceOf(WebApplicationException.class)
                .hasMessage("HTTP 401 Unauthorized");
    }


    @Test
    public void shouldReportAuthorizationErrorWhenAccessToResourceIsForbidden() {

        // given
        Mockito.when(aafClientCache.authorize(Mockito.any()))
                .thenReturn(AafClientCache.AuthorizationResult.AuthorizationFailure);

        // when/then
        assertThatThrownBy(() -> aafContainerFilters.filter(requestContext))
                .isInstanceOf(WebApplicationException.class)
                .hasMessage("HTTP 403 Forbidden");
    }

    @Test
    public void shouldAcceptRequestWhenActorWasAuthenticated() {

        // given
        Mockito.when(aafClientCache.authorize(Mockito.any()))
                .thenReturn(AafClientCache.AuthorizationResult.Authenticated);

        // when/then
        try {
            aafContainerFilters.filter(requestContext);
        } catch (Exception e) {
            fail("Operation should pass!");
        }
    }

    @Test
    public void shouldAcceptRequestWhenActorWasAuthorized() {

        // given
        Mockito.when(aafClientCache.authorize(Mockito.any()))
                .thenReturn(AafClientCache.AuthorizationResult.Authorized);

        // when/then
        try {
            aafContainerFilters.filter(requestContext);
        } catch (Exception e) {
            fail("Operation should pass!");
        }
    }
}
