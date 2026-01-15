package com.igloo.bar.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class InventoryServiceClientConfiguration {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * ClientHttpRequestInterceptor propagate the Oauth2 authentication to the inventory API,
     * As they share the same authentication mechanism.
     *
     * @return ClientHttpRequestInterceptor
     */
    @Bean ClientHttpRequestInterceptor bearerPropagationInterceptor() {
        return (request, body, execution) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
            }

            return execution.execute(request, body);
        };
    }

    @Bean
    public InventoryServiceClient inventoryServiceClient(@Value("${inventory.service.url}") String baseUrl, RestClient.Builder restClientBuilder,
                                                         ClientHttpRequestInterceptor bearerPropagationInterceptor) {

        RestClient restClient = restClientBuilder
            .baseUrl(baseUrl)
            .requestInterceptor(bearerPropagationInterceptor)
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build();

        return factory.createClient(InventoryServiceClient.class);
    }
}