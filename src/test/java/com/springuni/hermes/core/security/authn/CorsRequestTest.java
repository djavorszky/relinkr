package com.springuni.hermes.core.security.authn;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.cors.CorsConfiguration.ALL;

import com.springuni.hermes.core.security.authn.CorsRequestTest.TestConfig;
import com.springuni.hermes.core.security.authn.CorsRequestTest.TestController;
import com.springuni.hermes.test.security.AbstractWebSecurityTest;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@WebMvcTest(controllers = TestController.class)
@ContextConfiguration(classes = TestConfig.class)
public class CorsRequestTest extends AbstractWebSecurityTest {

    private static final String ORIGIN_HEADER_VALUE = "http://localhost:9999";

    private static final HttpEntity<?> TEST_HTTP_RESPONSE = ok().build();

    @Test
    public void givenAllowedCorsMethod_whenCorsRequest_thenAllowOriginAddedAndOk()
            throws Exception {

        mockMvc.perform(
                get("/").header(ORIGIN, ORIGIN_HEADER_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ALL));
    }

    @Test
    public void givenAllowedCorsMethod_whenNormalRequest_thenAllowOriginIsNotAddedAndOk()
            throws Exception {

        mockMvc.perform(
                get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void givenDisallowedCorsMethod_whenCorsRequest_thenAllowOriginIsNotAddedAndForbidden()
            throws Exception {

        mockMvc.perform(
                head("/").header(ORIGIN, ORIGIN_HEADER_VALUE))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Controller
    public static class TestController {

        @GetMapping
        public HttpEntity<?> get() {
            return TEST_HTTP_RESPONSE;
        }

        @RequestMapping(method = HEAD)
        HttpEntity<?> head() {
            return TEST_HTTP_RESPONSE;
        }

    }

    @TestConfiguration
    @Import({AbstractWebSecurityTest.TestConfig.class})
    static class TestConfig {

        @Bean
        TestController testController() {
            return new TestController();
        }

    }

}
