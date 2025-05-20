package sn.intouch.pfe.cmdbdataprocess.dtos;


import java.util.Set;

public class ResponseWrapper<T> {
    public String message;
    private Integer code;
    public T data;
    public Set<String> errorDetails;

    public ResponseWrapper(String message) {
        this.message = message;
    }

    public ResponseWrapper(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ResponseWrapper(String message, Integer code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }
    public ResponseWrapper( Integer code, Set<String> errorDetails) {
        this.code = code;
        this.errorDetails = errorDetails;
    }

}

