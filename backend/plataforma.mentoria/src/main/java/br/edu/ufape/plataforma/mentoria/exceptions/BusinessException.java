package br.edu.ufape.plataforma.mentoria.exceptions;

public class BusinessException extends RuntimeException {

    public BusinessException(){
        super();
    }

    public BusinessException (String message){
        super(message);
    }
}
