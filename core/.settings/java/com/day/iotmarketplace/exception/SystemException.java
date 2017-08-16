package com.day.iotmarketplace.exception;

public class SystemException extends RuntimeException {
	/**
     * Default Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Variable holds the error string.
     */
    private final String errorString;

    /**
     * Default Constructor.
     * 
     * @param message
     *            exception message.
     */
    public SystemException(final String message) {
        super();
        this.errorString = message;
    }

    /**
     * Parameterized constructor. Takes exception object as argument
     * 
     * @param exc
     *            exception.
     * @param message
     *            exception message. throwable exception parameter.
     */
    public SystemException(final String message, final Throwable exc) {
        super(exc);
        this.errorString = message;
    }

    /**
     * Gets the error string of exception.
     * 
     * @return String error string
     */
    public String getErrorString() {
        return this.errorString;
    }

}
