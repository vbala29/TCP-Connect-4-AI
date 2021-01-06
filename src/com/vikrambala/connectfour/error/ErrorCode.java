package com.vikrambala.connectfour.error;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCode {
    
    CALM(0),
    
    SERVER_ERROR(200),
    
    IMAGE_RETRIVAL_ERROR(201),
    
    SERVER_TIMEOUT(202),
    
    INVALID_CREDENTIAL(301),
    
    USERNAME_EXISTS(302),
    
    DEFAULT;
    
    private int value;
    private static Map<Integer, ErrorCode> map = new HashMap<Integer, ErrorCode>();
    
    ErrorCode(){
        
    }
    
    static {
        for (ErrorCode e : ErrorCode.values()) {
            map.put(e.value, e);
        }
    }
    
    ErrorCode(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public ErrorCode getErrorCode() {
        for (ErrorCode e:  ErrorCode.values()) {
            if (e.getValue() == value) {
                return e; 
            } 
        }
        
        return DEFAULT; 
    }

    public static ErrorCode valueOf(int errorCode) {
        return (ErrorCode) map.get(errorCode);
    }
    

    

}
