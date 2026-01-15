package com.igloo.bar;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    public static final String KEYCLOAK_ROLES_CLAIM = "roles";
    public static final String KEYCLOAK_REALM = "realm_access";

    /**
     * JwtAuthenticationConverter converts role claims from KeyCloak to Spring security authorities.
     * FIXME : fix the way to fill spring security authorities & avoid code duplication
     *
     * @return JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap(KEYCLOAK_REALM);
            if (realmAccess == null || !realmAccess.containsKey(KEYCLOAK_ROLES_CLAIM)) {
                return emptyList();
            }

            return ((List<String>) realmAccess.get(KEYCLOAK_ROLES_CLAIM)).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(toList());
        });

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtConverter) {
        return http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            ).oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))).build();
    }
}
