package com.ftn.plagiator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import com.ftn.plagiator.jwt.JwtTokenFilterConfigurer;
import com.ftn.plagiator.jwt.JwtTokenUtils;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtTokenUtils jwtTokenProvider;

	@Autowired
	private UserDetailsService userDetailsService;

	public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Disable CSRF (cross site request forgery)
		http.csrf().disable();
		
		http.cors().configurationSource(request -> {

            CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
            configuration.setAllowCredentials(true);
            configuration.addAllowedOrigin("*");

            configuration.addAllowedMethod("GET");
            configuration.addAllowedMethod("POST");
            configuration.addAllowedMethod("DELETE");
            configuration.addAllowedMethod("PUT");
            configuration.addAllowedMethod("PATCH");
            configuration.addAllowedMethod("OPTIONS");

            configuration.addAllowedHeader("Content-Type");
            configuration.addAllowedHeader("X-Authorization");
            configuration.addAllowedHeader("X-Authorization-Refresh");

            configuration.setMaxAge(3600L);

            return configuration;
        });

		// No session will be created or used by spring security
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		//TODO 1: ono cemu neregistrovani korisnik sme da pristupi
		http.authorizeRequests().antMatchers("/*", "/login", "/api/login", "/api/signup", "/api/search", "/api/email/*",
				"/api/registration/activate/*", "/api/proba/test/*",
				"/api/search/**").permitAll()
		.antMatchers(HttpMethod.POST, "/api/search").permitAll()
		.antMatchers(HttpMethod.PUT, "/api/korisnik/*/aktivirajNalog").permitAll().anyRequest()
				.authenticated();

		
		// If a user try to access a resource without having enough permissions
		http.exceptionHandling().accessDeniedPage("/api/login");

		// Apply JWT
		http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		//sve sto ce se ignorisati za web
		web.ignoring().antMatchers("/webjars/**")//
				.antMatchers("/public")//
				.antMatchers("/main**")//
				.antMatchers("/inline**")//
				.antMatchers("/polyfills**")//
				.antMatchers("/styles**")//
				.antMatchers("/favicon.ico")//
				.antMatchers("/scripts**")//
				.antMatchers("/glyphicons**")//
				.antMatchers("/fontawesome**")//
				.antMatchers("/vendor**")//
				.antMatchers("/assets/**")//
				.antMatchers("/Poppins**")//
				.antMatchers("/h2-console");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

}