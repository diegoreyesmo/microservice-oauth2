package diegoreyesmo.springboot.authserverext.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import diegoreyesmo.springboot.authserverext.exception.AuthServerException;
import lombok.extern.java.Log;

@Configuration
@Order(1)
@Log
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${whitelist.actuator}")
	private String actuatorPath;

	@Override
	protected void configure(HttpSecurity http) throws AuthServerException {
		try {
			http.authorizeRequests().antMatchers(actuatorPath).permitAll().anyRequest().authenticated();

		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new AuthServerException(e.getMessage(), e);
		}
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws AuthServerException {
		try {
			return super.authenticationManagerBean();
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new AuthServerException(e.getMessage(), e);
		}
	}

}