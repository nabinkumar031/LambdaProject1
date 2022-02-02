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
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Consumer;

import static com.lululemon.lambda.fullsync.S3EventForEfs.fromS3Event;

@Slf4j
@EnableConfigurationProperties({SchemaRegistryClientProperties.class})
@SpringBootApplication
public class NedappInventoryFullSynchApp {

	@Bean
	public Consumer<S3Event> nedappFullSyncConsumer(S3Client s3Client,
													@Value("#{System.getenv(\"EFS_FOLDER_PATH\")}") String strEFSPath1){
		var consumer = new NedappFullSyncConsumer(s3Client, strEFSPath1);
		return s3Event -> {
			try {
				consumer.accept(fromS3Event(s3Event));
			} catch (NedappFullSyncException e) {
				log.error(e.formattedTrace());
				log.error("Error processing event ", e);
			}
		};
	}

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
