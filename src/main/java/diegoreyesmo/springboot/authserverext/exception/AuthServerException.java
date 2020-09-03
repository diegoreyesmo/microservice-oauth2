package diegoreyesmo.springboot.authserverext.exception;

public class AuthServerException extends Exception {

	public AuthServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthServerException(String message) {
		super(message);
	}

}
