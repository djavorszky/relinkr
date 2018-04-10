package com.springuni.hermes.core.web;

import static java.lang.reflect.Modifier.FINAL;

import com.springuni.hermes.click.ClickId;
import com.springuni.hermes.core.convert.EntityClassAwareIdToStringConverter;
import com.springuni.hermes.core.convert.StringToEntityClassAwareIdConverter;
import com.springuni.hermes.link.model.LinkId;
import com.springuni.hermes.link.model.LinkSetId;
import com.springuni.hermes.user.model.UserId;
import com.springuni.hermes.visitor.model.VisitorId;
import java.lang.reflect.Field;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements InitializingBean, WebMvcConfigurer {

    private static final String APPA$BMP_CLASS =
            "org.springframework.hateoas.mvc.AnnotatedParametersParameterAccessor$BoundMethodParameter";

    private static final String APPA$BMP_CONVERSION_SERVICE = "CONVERSION_SERVICE";

    private final ApplicationContext applicationContext;

    public WebMvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, ClickId.class,
                new StringToEntityClassAwareIdConverter<>(ClickId.class));
        registry.addConverter(new EntityClassAwareIdToStringConverter<ClickId>());

        registry.addConverter(String.class, LinkId.class,
                new StringToEntityClassAwareIdConverter<>(LinkId.class));
        registry.addConverter(new EntityClassAwareIdToStringConverter<LinkId>());

        registry.addConverter(String.class, LinkSetId.class,
                new StringToEntityClassAwareIdConverter<>(LinkSetId.class));
        registry.addConverter(new EntityClassAwareIdToStringConverter<LinkSetId>());

        registry.addConverter(String.class, UserId.class,
                new StringToEntityClassAwareIdConverter<>(UserId.class));
        registry.addConverter(new EntityClassAwareIdToStringConverter<UserId>());

        registry.addConverter(String.class, VisitorId.class,
                new StringToEntityClassAwareIdConverter<>(VisitorId.class));
        registry.addConverter(new EntityClassAwareIdToStringConverter<VisitorId>());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ConversionService conversionService = applicationContext.getBean(ConversionService.class);

        // Workaround for https://github.com/spring-projects/spring-hateoas/issues/118
        ReflectionUtils.doWithFields(
                Class.forName(APPA$BMP_CLASS),
                it -> setValue(it, conversionService),
                it -> APPA$BMP_CONVERSION_SERVICE.equals(it.getName())
        );
    }

    private void setValue(Field field, Object value) {
        field.setAccessible(true);

        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~FINAL);

            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
