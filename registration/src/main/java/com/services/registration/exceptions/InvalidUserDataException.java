package com.services.registration.exceptions;

public class InvalidUserDataException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidUserDataException(String message) {
        super(message);
    }
}
