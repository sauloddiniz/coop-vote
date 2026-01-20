package br.com.coopvote.exceptions;

public class AssociadoNaoAutorizadoException extends RuntimeException {
    public AssociadoNaoAutorizadoException(String message) {
        super(message);
    }
}
