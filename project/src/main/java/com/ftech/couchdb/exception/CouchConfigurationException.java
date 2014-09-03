package com.ftech.couchdb.exception;

public class CouchConfigurationException extends CouchException {

    /**
     * 
     */
    private static final long serialVersionUID = -8834670450786546585L;

    public CouchConfigurationException( Exception e ){
        super( e );
    }

    public CouchConfigurationException(String message) {
        super( message );
    }
    
}
