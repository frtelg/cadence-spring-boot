package com.frtelg.cadence.configuration;

import com.frtelg.cadence.config.CadenceConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CadenceTestServer.class, CadenceConfiguration.class})
public @interface EnableCadenceIntegrationTest {
}
