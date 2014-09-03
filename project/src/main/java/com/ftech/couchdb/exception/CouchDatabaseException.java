package com.ftech.couchdb.exception;

public class CouchDatabaseException extends CouchException {

    /**
     * 
     */
    private static final long serialVersionUID = 381625576590406728L;

    public CouchDatabaseException( Exception e ){
        super( e );
    }

    public CouchDatabaseException(String message) {
        super( message );
    }
    
}
