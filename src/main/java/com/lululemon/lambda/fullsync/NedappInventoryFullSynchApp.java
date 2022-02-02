package com.lululemon.lambda.fullsync;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.schema.registry.client.config.SchemaRegistryClientProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Consumer;

@Slf4j
@EnableConfigurationProperties({SchemaRegistryClientProperties.class})
@SpringBootApplication
public class NedappInventoryFullSynchApp {

	@Bean
	public S3Client s3Client(){
		return S3Client.builder()
				.credentialsProvider(AwsCredentialsProviderChain.builder()
						.credentialsProviders(EnvironmentVariableCredentialsProvider.create(),
								SystemPropertyCredentialsProvider.create(),
								ProfileCredentialsProvider.create(),
								InstanceProfileCredentialsProvider.create())
						.build())
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(NedappInventoryFullSynchApp.class, args);
	}

}
