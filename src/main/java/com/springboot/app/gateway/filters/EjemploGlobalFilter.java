package com.springboot.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class EjemploGlobalFilter implements GlobalFilter, Ordered {

	private final Logger log = LoggerFactory.getLogger(EjemploGlobalFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("Ejecutando el pre filtro");

		// then: operador then de web flux
		// chain.filter --> Continua con filtro

		// Nota: todo la logica sobre del return es el pre, y todo dentro del return es el post
		
		exchange.getRequest().mutate().headers( h -> h.add("token", "123456"));

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			log.info("Ejecutando el post filtro");
			
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(valor -> {
				exchange.getResponse().getHeaders().add("token", valor); // pasamos el el token de la cabecera del request ala la cabecera de la respuesta
			});
			
			exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "rojo").build()); // build genera el objeto
			//exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
		}));
	}

	@Override
	public int getOrder() {
		return 100;
	}
}
