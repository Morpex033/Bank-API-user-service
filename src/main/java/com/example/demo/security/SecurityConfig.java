package com.example.demo.security;

import com.example.demo.models.role.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.TokenService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.text.ParseException;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomUserDetailsService userDetailsService;
	@Bean
	public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(
			@Value("${ACCESS_TOKEN_KEY}") String accessTokenKey,
			@Value("${REFRESH_TOKEN_KEY}") String refreshTokenKey
	) throws ParseException, JOSEException {
		return new JwtAuthenticationConfigurer(new TokenService(
				new MACSigner(OctetSequenceKey.parse(accessTokenKey)),
				new MACVerifier(OctetSequenceKey.parse(accessTokenKey)),
				new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey)),
				new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey))));
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
											JwtAuthenticationConfigurer jwtAuthenticationConfigurer) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.with(jwtAuthenticationConfigurer, Customizer.withDefaults())
				.httpBasic(Customizer.withDefaults())
				.authorizeHttpRequests(request ->
						request
								.requestMatchers("/api/user/delete").hasRole("ADMIN")
								.requestMatchers("/api/user/registration", "/error", "/doc/**", "/jwt/tokens")
								.permitAll().anyRequest().authenticated())
				.sessionManagement(sessionManagement ->
						sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.logout(LogoutConfigurer::permitAll);

		return http.build();
	}
	
	@Bean
	AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(8);
	}
}
