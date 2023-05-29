package br.com.rodrigoproject;

import br.com.rodrigoproject.OrderApp;
import br.com.rodrigoproject.config.AsyncSyncConfiguration;
import br.com.rodrigoproject.config.EmbeddedElasticsearch;
import br.com.rodrigoproject.config.EmbeddedKafka;
import br.com.rodrigoproject.config.EmbeddedRedis;
import br.com.rodrigoproject.config.EmbeddedSQL;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { OrderApp.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedKafka
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
