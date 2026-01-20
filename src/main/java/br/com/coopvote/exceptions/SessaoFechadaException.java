package br.com.coopvote.exceptions;

public class SessaoFechadaException extends RuntimeException {
    public SessaoFechadaException(String message) {
        super(message);
    }
}
