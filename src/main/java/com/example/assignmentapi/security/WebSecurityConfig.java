package com.example.assignmentapi.security;

import com.example.assignmentapi.utilities.RSAKeyUtility;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
//                .requiresChannel().anyRequest().requiresSecure() // Note 1
//                .and()
                .authorizeHttpRequests(requests -> requests
                        .antMatchers("/authenticate").permitAll() // allow un-authed requests to this endpoint
                        .anyRequest().authenticated() // all other requests need to be authorised
                )
                .csrf().ignoringAntMatchers("/login") // this is necessary for making a post to this endpoint for authentication from a different domain name (or for testing with Postman)
                .and()
                .httpBasic(Customizer.withDefaults()) // Note 2
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt) // Note 3
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management - JWTs
                .exceptionHandling((exceptions) -> exceptions // Responsible for sending an HTTP response that requests an Authorization: Bearer token
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

        return http.build();
    }

    // Note 1
    // These lines set the server to require all requests over HTTPS, but this requires a valid TLS certificate which I did not set up
    // Note 2
    // Sets to expect Basic Auth - Authorization: Basic {base64 encoded user:password}
    // This is not secure as it is only encoded, not encrypted - essentially just plain text with an extra step
    // These requests should only be done over HTTPS/TLS
    // Note 3
    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html
    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
    // OAuth2 Resource Server is responsible for authenticating any incoming requests that contain an Authorization: Bearer header
    // 1. Validate the signature against public key
    // 2. Validate expiry/issued etc. claims
    // 3. Map each scope to an authority - "user"/"admin" etc.

    // In memory user for testing purposes, not good for production
    @Bean
    UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password("{noop}password")
                        .authorities("user")
                        .build()
        );
    }

    // public + private keypair required for JWT encoding/decoding
    // If not found, you should run the main() method in RSAKeyUtility which will generate them
    // Or, run it again to overwrite the keys with new ones
    RSAPublicKey publicKey = (RSAPublicKey) RSAKeyUtility.loadKeyFromFile("certificates/key.pub");
    RSAPrivateKey privateKey = (RSAPrivateKey) RSAKeyUtility.loadKeyFromFile("certificates/key.priv");


    // Responsible for decoding the JWT using the public key
    @Bean
    JwtDecoder jwtDecoder () {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    // Responsible for creating the JWT using public + private keypair
    @Bean
    JwtEncoder jwtEncoder () {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }
}
