package org.ujar.jh.petclinic.vuebdd.config;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.ujar.jh.petclinic.vuebdd.aop.logging.LoggingAspect;
import tech.jhipster.config.JHipsterConstants;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}
