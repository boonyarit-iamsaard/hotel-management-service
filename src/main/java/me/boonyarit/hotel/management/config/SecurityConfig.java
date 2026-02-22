package me.boonyarit.hotel.management.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/public",
            "/api/actuator/health",
            "/api/actuator/info"
    };

    private final SecurityProperties securityProperties;
    private final RestTemplate restTemplate;

    @Bean
    // Suppress SonarQube warnings for Spring Security DSL pattern:
    // - S1130: Remove the declaration of thrown exception 'java.lang.Exception' -
    // required by Spring Security's HttpSecurity DSL
    // - S112: Define and throw a dedicated exception instead of using a generic one
    // - HttpSecurity.build() throws generic Exception per Spring Security API
    // design
    @SuppressWarnings({ "java:S1130", "java:S112" })
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        String issuerUri = securityProperties.getIssuerUri();
        String normalizedIssuerUri = issuerUri.endsWith("/")
                ? issuerUri.substring(0, issuerUri.length() - 1)
                : issuerUri;
        String jwkSetUri = normalizedIssuerUri + "/.well-known/jwks.json";

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(restTemplate)
                .build();

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(
                List.of(securityProperties.getAudiencesArray()));
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    private record AudienceValidator(List<String> expectedAudiences) implements OAuth2TokenValidator<Jwt> {

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            List<String> tokenAudiences = jwt.getAudience();
            if (tokenAudiences == null || tokenAudiences.isEmpty()) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "The required audience is missing", null));
            }
            for (String expected : expectedAudiences) {
                if (tokenAudiences.contains(expected)) {
                    return OAuth2TokenValidatorResult.success();
                }
            }

            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token audience does not match expected audiences", null));
        }
    }
}
