package ru.dstu.work.akselerator.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import ru.dstu.work.akselerator.integration.AuthorizationHeaderProvider;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("parkingCoreRestClient")
    RestClient parkingCoreRestClient(RestClient.Builder builder,
                                     AuthorizationHeaderProvider headerProvider,
                                     @Value("${parking.core.base-url}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    headerProvider.getAuthorizationHeader()
                            .ifPresent(value -> request.getHeaders().set(HttpHeaders.AUTHORIZATION, value));
                    return execution.execute(request, body);
                })
                .build();
    }
}
