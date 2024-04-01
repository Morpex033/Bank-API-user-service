package com.example.demo.service;

import com.example.demo.models.Token;
import com.example.demo.models.TokenUser;
import com.example.demo.repository.UserRepository;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Data
public class CustomUserDetailsService
		implements UserDetailsService,
		AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email);
	}

	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException{
		if (authenticationToken.getPrincipal() instanceof Token token){

			return new TokenUser(token.subject(), "nopassword", true, true,
					token.expiresAt().isAfter(Instant.now()), true,
					token.authorities().stream()
							.map(SimpleGrantedAuthority::new)
							.toList(), token);
		}
		throw new UsernameNotFoundException("Principal must by type token");
	}

}
