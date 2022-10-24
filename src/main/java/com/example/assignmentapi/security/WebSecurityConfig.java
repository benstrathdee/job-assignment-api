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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true // allows for the "@PreAuthorize" annotations on controllers
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
                // These lines would set the server to require all requests over HTTPS,
                // but this requires a valid TLS certificate which I did not set up
//                .requiresChannel().anyRequest().requiresSecure()
//                .and()

                // this is necessary for making a post to this endpoint for authentication from a different domain name
                // without an X-XSRF-TOKEN header
                .cors().and().csrf().ignoringAntMatchers("/auth/**")
                .and()

                // defines how unauthorised access attempts are handled
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()

                // defines session creation - stateless because this uses JWT
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // only URLs matching this pattern do not require JWT authentication (this is where login details are POSTed to)
                .authorizeRequests().antMatchers("/auth/**").permitAll()

                // All other requests require JWT authentication
                .anyRequest().authenticated()
                .and()

                // defines the bean used to provide authentication
                .authenticationProvider(authenticationProvider())

                // defines a filter used before each request, in this case for authenticating
                .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class)

                // Send a XSRF-TOKEN cookie to the client (which can then use it in an X-XSRF-TOKEN header to allow for requests
                // that modify state (POST, PUT, DELETE and PATCH))
                // Sends a new cookie with each successful response to a request, so it has to be updated each time a
                // new modifying request is made
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        return http.build();
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
