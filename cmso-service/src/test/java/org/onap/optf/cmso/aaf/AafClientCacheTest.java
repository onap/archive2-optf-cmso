package org.onap.optf.cmso.aaf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.springframework.core.env.Environment;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AafClientCacheTest {

    @Mock
    Environment env;

    @Mock
    AafClient aafClient;

    @Mock
    AafUserRoleProperties aafUserRoleProperties;

    @Mock
    SecurityContext securityContext;

    @Mock
    ContainerRequestContext requestContext;

    @Mock
    UriInfo uriInfo;

    @Mock
    Response aafResponse;

    @InjectMocks
    AafClientCache aafClientCache;

    @Test
    public void shouldAuthorizeActorWhenAafIsDisabled() {
        // given
        when(env.getProperty(
                eq(AafProperties.aafEnabled.toString()),
                eq(Boolean.class),
                eq(true))
        ).thenReturn(false);

        // when
        final AafClientCache.AuthorizationResult result = aafClientCache.authorize(requestContext);

        // then
        assertThat(result).isEqualTo(AafClientCache.AuthorizationResult.Authorized);
    }

    @Test
    public void shouldAuthorizeActorToResourceWhenActorHasProperPermissions() throws URISyntaxException, CmsoException, JsonProcessingException {
        // given
        when(env.getProperty(
                eq(AafProperties.aafEnabled.toString()),
                eq(Boolean.class),
                eq(true))
        ).thenReturn(true);

        AafUserRole requiredRole = new AafUserRole("/context/someMethod","org\\.onap\\.osaaf\\.resources\\.access\\|rest\\|read");

        // configure request context
        setRequestContextWithAuthorizationHeader();

        // configure aaf response
        configureAafClientToReturnRequiredRole(requiredRole);

        // define which role is required/expected for selected url and method
        when(aafUserRoleProperties.getForUrlMethod(eq("/context"), eq("someMethod"))).thenReturn(List.of(requiredRole));

        // when
        final AafClientCache.AuthorizationResult result = aafClientCache.authorize(requestContext);

        // then
        assertThat(result).isEqualTo(AafClientCache.AuthorizationResult.Authorized);
    }

    @Test
    public void shouldReportThatActorDoesNotHaveAccessToResourceWhenRolesDoNotMatch() throws URISyntaxException, CmsoException, JsonProcessingException {
        // given
        when(env.getProperty(
                eq(AafProperties.aafEnabled.toString()),
                eq(Boolean.class),
                eq(true))
        ).thenReturn(true);

        AafUserRole requiredRole = new AafUserRole("/context/someMethod","org\\.onap\\.osaaf\\.resources\\.access\\|rest\\|read");
        AafUserRole roleReturnedByAaf = new AafUserRole("/context/someMethod","org\\.onap\\.osaaf\\.resources\\.access\\|rest\\|write");

        // configure request context
        setRequestContextWithAuthorizationHeader();

        // configure aaf response
        configureAafClientToReturnRequiredRole(roleReturnedByAaf);

        // define which role is required/expected for selected url and method
        when(aafUserRoleProperties.getForUrlMethod(eq("/context"), eq("someMethod"))).thenReturn(List.of(requiredRole));

        // when
        final AafClientCache.AuthorizationResult result = aafClientCache.authorize(requestContext);

        // then
        assertThat(result).isEqualTo(AafClientCache.AuthorizationResult.AuthorizationFailure);
    }

    @Test
    public void shouldReportThatActorDoesNotHaveAccessToResourceWhenUserAndPasswordWasNotSet() throws URISyntaxException {
        // given
        when(env.getProperty(
                eq(AafProperties.aafEnabled.toString()),
                eq(Boolean.class),
                eq(true))
        ).thenReturn(true);

        setDefaultRequestContextConfiguration();

        // when
        final AafClientCache.AuthorizationResult result = aafClientCache.authorize(requestContext);

        // then
        assertThat(result).isEqualTo(AafClientCache.AuthorizationResult.AuthenticationFailure);
    }

    private void setRequestContextWithAuthorizationHeader() throws URISyntaxException {
        when(requestContext.getHeaderString(eq("Authorization"))).thenReturn("Basic YWxhZGRpbjpvcGVuc2VzYW1l");
        setDefaultRequestContextConfiguration();
    }

    private void setDefaultRequestContextConfiguration() throws URISyntaxException {
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getAbsolutePath()).thenReturn(new URI("/context"));
        when(requestContext.getMethod()).thenReturn("someMethod");
        when(requestContext.getSecurityContext()).thenReturn(securityContext);
    }

    private void configureAafClientToReturnRequiredRole(AafUserRole requiredRole) throws JsonProcessingException, CmsoException {
        when(aafResponse.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
        when(aafResponse.getStatusInfo()).thenReturn(Response.Status.OK);
        when(aafResponse.readEntity(Mockito.eq(String.class))).thenReturn(givenAafResponse(requiredRole));

        when(aafClient.getAuthz(anyMap())).thenReturn(aafResponse);
    }

    private String givenAafResponse(AafUserRole role) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        final AafPermResponse aafPermResponse = new AafPermResponse();
        aafPermResponse.setPerm(role.getAafPerms());
        return om.writeValueAsString(aafPermResponse);
    }
}
