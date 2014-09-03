package com.ftech.couchdb.exception;

public class CouchException extends RuntimeException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7364982819164180571L;

    public CouchException( Exception e ){
        super( e );
    }

    public CouchException(String message) {
        super( message );
    }
    
}
