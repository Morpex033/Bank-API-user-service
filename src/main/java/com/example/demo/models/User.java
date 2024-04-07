package com.example.demo.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.models.enums.role.Role;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column
	@NonNull
	private String firstName;
	@Column
	@NonNull
	private String lastName;
	@Column
	@Email
	@NonNull
	private String email;
	@Column
	@NonNull
	private String password;

	@OneToOne
	private Account account;
	
	@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	private Collection<Role> roles = new HashSet<>();
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}
	@Override
	public String getUsername() {
		return email;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
}
