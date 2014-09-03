package com.ftech.couchdb.exception;

public class CouchDocumentException extends CouchException {

    /**
     * 
     */
    private static final long serialVersionUID = 381625576590406728L;

    public CouchDocumentException( Exception e ){
        super( e );
    }

    public CouchDocumentException(String message) {
        super( message );
    }
    
}
