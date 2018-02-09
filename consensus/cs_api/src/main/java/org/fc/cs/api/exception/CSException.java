package org.fc.cs.api.exception;

public class CSException extends RuntimeException {

	private static final long serialVersionUID = 3129124709055457388L;

	public CSException(String message) {
		super(message);
	}

	public CSException(String message, Throwable cause) {
		super(message, cause);
	}

	public CSException(Throwable cause) {
		super(cause);
	}

}
