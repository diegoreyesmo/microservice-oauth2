package diegoreyesmo.springboot.authserverext.configuration;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import diegoreyesmo.springboot.authserverext.exception.AuthServerException;
import diegoreyesmo.springboot.authserverext.util.RsaUtils;
import lombok.extern.java.Log;

@Log
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

	private static final String ACCESS = "permitAll()";

	@Value("${key.private}")
	private String privateKeyPath;

	@Value("${key.public}")
	private String publicKeyPath;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private AuthenticationManager authenticationManager;

	public AuthServerConfig() {
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws AuthServerException {
		try {
			clients.jdbc(dataSource);
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new AuthServerException(e.getMessage(), e);
		}
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws AuthServerException {
		try {
			security.tokenKeyAccess(ACCESS).checkTokenAccess(ACCESS);
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new AuthServerException(e.getMessage(), e);
		}

	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws AuthServerException {
		try {
			endpoints.authenticationManager(authenticationManager).tokenStore(jwtTokenStore())
					.accessTokenConverter(accessTokenConverter());
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new AuthServerException(e.getMessage(), e);
		}
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() throws AuthServerException {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		PrivateKey privateKey = RsaUtils.readPrivateKeyFromFile(privateKeyPath);
		PublicKey publicKey = RsaUtils.readPublicKeyFromFile(publicKeyPath);
		KeyPair keyPair = new KeyPair(publicKey, privateKey);
		converter.setKeyPair(keyPair);
		return converter;
	}

	@Bean
	public JwtTokenStore jwtTokenStore() throws AuthServerException {
		return new JwtTokenStore(accessTokenConverter());
	}

}
