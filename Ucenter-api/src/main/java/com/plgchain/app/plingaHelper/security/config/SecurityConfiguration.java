package com.plgchain.app.plingaHelper.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.plgchain.app.plingaHelper.security.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserService userService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((request) -> request
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/**")).permitAll()
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/public/**")).permitAll()
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/godaction/**")).hasRole("GOD")
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/godaction/")).hasRole("GOD")
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/godArea/**")).hasRole("GOD")
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/adminArea/**")).hasRole("ADMIN")
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/memberArea/**")).hasRole("USER")
						.anyRequest().authenticated())
				.sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService.userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		String hierarchy = "ROLE_GOD > ROLE_ADMIN \n ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER \n ROLE_USER > ROLE_GUEST";
		roleHierarchy.setHierarchy(hierarchy);
		return roleHierarchy;
	}

	/*
	 * @Bean public DefaultWebSecurityExpressionHandler
	 * webSecurityExpressionHandler() { DefaultWebSecurityExpressionHandler
	 * expressionHandler = new DefaultWebSecurityExpressionHandler();
	 * expressionHandler.setRoleHierarchy(roleHierarchy()); return
	 * expressionHandler; }
	 */
}