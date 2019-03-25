package com.springuni.hermes.click.service;

import static com.springuni.hermes.core.scheduling.AsyncConfig.THREAD_NAME_PREFIX;
import static com.springuni.hermes.test.Mocks.LINK_ID;
import static com.springuni.hermes.test.Mocks.USER_ID;
import static com.springuni.hermes.test.Mocks.VISITOR_ID;
import static com.springuni.hermes.test.Mocks.VISITOR_IP;
import static java.time.Instant.now;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.springuni.hermes.click.model.Click;
import com.springuni.hermes.click.service.RedirectedEventListenerTest.TestConfig;
import com.springuni.hermes.core.scheduling.AsyncConfig;
import com.springuni.hermes.link.model.RedirectedEvent;
import com.springuni.hermes.test.scheduling.AsyncActionInterceptor;
import com.springuni.hermes.test.scheduling.AsyncActionResult;
import java.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RedirectedEventListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private AsyncActionInterceptor clickServiceInterceptor;

    @Test
    public void shouldHandleEventAsynchronously() {
        Instant instant = now();

        RedirectedEvent redirectedEvent = RedirectedEvent.of(
                LINK_ID, VISITOR_ID, VISITOR_IP.getIpAddress(), USER_ID, instant
        );

        applicationEventPublisher.publishEvent(redirectedEvent);

        AsyncActionResult asyncActionResult = clickServiceInterceptor.takeResult();
        assertThat(asyncActionResult.getExecutorName(), startsWith(THREAD_NAME_PREFIX));

        Click click = (Click) asyncActionResult.getArguments()[0];
        assertEquals(LINK_ID, click.getLinkId());
        assertEquals(VISITOR_ID, click.getVisitorId());
        assertEquals(USER_ID, click.getUserId());
        assertEquals(instant, click.getVisitTimestamp().toInstant(UTC));

    }

    static class DummyClickService implements ClickService {

        @Override
        public void logClick(Click click) {

        }

    }

    @Configuration
    @Import(AsyncConfig.class)
    static class TestConfig {

        @Bean
        AsyncActionInterceptor clickServiceInterceptor() {
            return new AsyncActionInterceptor();
        }

        @Bean
        ClickService clickService(AsyncActionInterceptor clickServiceInterceptor) {
            ClickService clickService = new DummyClickService();
            ProxyFactory proxyFactory = new ProxyFactory(clickService);
            proxyFactory.addAdvice(clickServiceInterceptor);
            return (ClickService) proxyFactory.getProxy();
        }

        @Bean
        RedirectedEventListener redirectedEventListener(ClickService clickService) {
            return new RedirectedEventListener(clickService);
        }

    }

}
