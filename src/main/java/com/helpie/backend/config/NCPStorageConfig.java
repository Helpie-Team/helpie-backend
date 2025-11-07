package com.helpie.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class NCPStorageConfig {
    @Value("${ncp.storage.region}")
    private String region;

    @Value("${ncp.storage.endpoint}")
    private String endPoint; // https://kr.object.ncloudstorage.com

    @Value("${ncp.storage.accessKey}")
    private String accessKey;

    @Value("${ncp.storage.secretKey}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .endpointOverride(URI.create(endPoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // ★ NCP 호환용
                        .build())
                .build();
    }
}
