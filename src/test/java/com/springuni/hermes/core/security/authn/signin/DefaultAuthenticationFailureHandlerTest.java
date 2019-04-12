package com.springuni.hermes.core.security.authn.signin;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springuni.hermes.core.web.RestErrorResponse;
import com.springuni.hermes.test.web.BaseServletTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthenticationFailureHandlerTest extends BaseServletTest {

    private static final String MESSAGE = "an error occurred";

    private ObjectMapper objectMapper;
    private AuthenticationFailureHandler handler;

    @Before
    public void setUp() {
        super.setUp();
        objectMapper = new ObjectMapper();
        handler = new DefaultAuthenticationFailureHandler(objectMapper);
    }

    @Test
    public void giveBadCredentialsException_whenOnAuthenticationFailure_Unauthorized() throws Exception {
        handler.onAuthenticationFailure(request, response, new BadCredentialsException(MESSAGE));

        RestErrorResponse errorResponse =
                objectMapper.readValue(response.getContentAsByteArray(), RestErrorResponse.class);

        assertResponse(errorResponse, UNAUTHORIZED.value(), APPLICATION_JSON_VALUE, MESSAGE);
    }

    @Test
    public void giveInternalAuthenticationServiceException_whenOnAuthenticationFailure_Unauthorized() throws Exception {
        handler.onAuthenticationFailure(request, response, new InternalAuthenticationServiceException(MESSAGE));

        RestErrorResponse errorResponse =
                objectMapper.readValue(response.getContentAsByteArray(), RestErrorResponse.class);

        assertResponse(errorResponse, INTERNAL_SERVER_ERROR.value(), APPLICATION_JSON_VALUE, MESSAGE);
    }

    @Test
    public void giveAuthenticationServiceException_whenOnAuthenticationFailure_Unauthorized() throws Exception {
        handler.onAuthenticationFailure(request, response, new AuthenticationServiceException(MESSAGE));

        RestErrorResponse errorResponse =
                objectMapper.readValue(response.getContentAsByteArray(), RestErrorResponse.class);

        assertResponse(errorResponse, SERVICE_UNAVAILABLE.value(), APPLICATION_JSON_VALUE, MESSAGE);
    }

    private void assertResponse(
            RestErrorResponse errorResponse,
            int expectedStatusCode, String expectedContentType, String expectedDetailMessage) {

        assertEquals(expectedStatusCode, errorResponse.getStatusCode());
        assertEquals(expectedContentType, response.getContentType());
        assertEquals(expectedDetailMessage, errorResponse.getDetailMessage());
    }

}
