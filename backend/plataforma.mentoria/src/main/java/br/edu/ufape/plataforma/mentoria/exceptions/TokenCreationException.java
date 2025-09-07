package br.edu.ufape.plataforma.mentoria.exceptions;

public class TokenCreationException extends RuntimeException {

    public TokenCreationException() {
        super();
    }

    public TokenCreationException(String message) {
        super(message);
    }

    public TokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenCreationException(Throwable cause) {
        super(cause);
    }
}