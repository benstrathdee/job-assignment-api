package com.example.assignmentapi.security;

import com.example.assignmentapi.security.jwt.AuthEntryPointJwt;
import com.example.assignmentapi.security.jwt.AuthTokenFilter;
import com.example.assignmentapi.service.UserService;
import com.example.assignmentapi.utilities.RSAKeyUtility;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableGlobalMethodSecurity( // allows for the "@PreAuthorize" annotations on controllers
        prePostEnabled = true
)
public class WebSecurityConfig {
    // Responsible for handling UserDetails which is what the DAOAuthenticationManager uses to manage users
    @Autowired
    private UserService userService;

    // Responsible for handling unauthorized requests
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Responsible for getting JWT from client and authentication before each request
    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }

    // Responsible for salting + hashing passwords on registration/login
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Next two are responsible for authenticating user on login
    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    // Configures which endpoints can be accessed without authentication, as well as
    // CORS/CSRF rules
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
//                .requiresChannel().anyRequest().requiresSecure() // Note 1
//                .and()
//                .cors().and().csrf().disable() // not secure if API will be accessed with a browser, but good for testing
                .cors().and().csrf().ignoringAntMatchers("/auth/**") // this is necessary for making a post to this endpoint for authentication from a different domain name (or for testing with Postman)
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session management - JWTs
                .and()
                .authorizeRequests().antMatchers("/auth/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();

        http.authenticationProvider(authenticationProvider()); // defines the bean used to provide authentication

        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class); // define the filter used before each request

        return http.build();
    }

    // Note 1
    // These lines would set the server to require all requests over HTTPS, but this requires a valid TLS certificate which I did not set up

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
