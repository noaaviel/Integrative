package iob.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ForbiddenRequestException extends RuntimeException {
	// Fields
	private static final long serialVersionUID = -4674903947479661923L;

	// Constructors
	public ForbiddenRequestException() {
	}

	public ForbiddenRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public ForbiddenRequestException(String message) {
		super(message);
	}

	public ForbiddenRequestException(Throwable cause) {
		super(cause);
	}

	// Getters & Setters

	// Methods
}