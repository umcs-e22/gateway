package com.assigment.gateway;

import com.assigment.gateway.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    @Value("${my.secretKey}")
    private String secret;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String auth;
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                auth = "Bearer " + exchange.getRequest().getCookies().getFirst("accessToken").getValue();
                // throw new RuntimeException("Missing authorization information");
            }else{
                auth = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            }

            return webClientBuilder.build()
                    .post()
                    .uri("http://AUTH-SERVICE/api/auth/check").header("Authorization", auth)
                    .retrieve().bodyToMono(UserDto.class)
                    .map(userDto -> {
                        exchange.getRequest()
                                .mutate()
                                .header("X-auth-user-id", String.valueOf(userDto.getUserUUID()))
                                .header("X-auth-user-roles", userDto.getRoles().toString())
                                .header("X-Auth", secret);
                        return exchange;
                    }).flatMap(chain::filter);
        };
    }

    public static class Config {
        // empty class as I don't need any particular configuration
    }
}

