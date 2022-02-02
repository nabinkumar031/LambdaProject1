package com.lululemon.lambda.fullsync;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.schema.registry.client.config.SchemaRegistryClientProperties;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@Slf4j
@EnableConfigurationProperties({SchemaRegistryClientProperties.class})
@SpringBootApplication
public class NedappInventoryFullSynchApp {
	@Bean
	public Consumer<S3Event> consumeEvents() {
		return event -> {
			event.getRecords().stream().findFirst().ifPresent(record -> {

			});
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(NedappInventoryFullSynchApp.class, args);
	}

}
