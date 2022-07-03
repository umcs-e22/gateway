package com.assigment.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = exchange.getRequest().getCookies().getFirst("accessToken") != null ? exchange.getRequest().getCookies().getFirst("accessToken").getValue():"";
        ServerHttpRequest request = exchange.getRequest().mutate().header("Authorization", "Bearer "+accessToken).build();
        ServerWebExchange exchange1 = exchange.mutate().request(request).build();
        return chain.filter(exchange1);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
