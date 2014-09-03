package com.ftech.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ftech.couchdb.exception.CouchConfigurationException;

public final class CouchdbConfigurator {
	
    private static final CouchdbConfigurator PROPERTY_CONFIGURATION = 
    		new CouchdbConfigurator( "application-config" );
	
    private String baseDirectory = "/config/";
	private Properties properties;
    
    private CouchdbConfigurator( String propertyFile ){
        properties = new Properties();
        loadConfiguration( propertyFile );
    }
    
    private void loadConfiguration( String propertiesFile ) {
        if( propertiesFile == null || propertiesFile.isEmpty() ){
            throw new CouchConfigurationException( "File parmeter is required" );
        }
        
        String file = propertiesFile.toLowerCase().endsWith( ".properties" ) ?
                propertiesFile : propertiesFile + ".properties";
        try {
            InputStream input = CouchdbConfigurator.class.
            		getResourceAsStream( baseDirectory + file );
            properties.load( input );
        } catch (IOException e) {
            throw new CouchConfigurationException( e );
        }
    }
    
    public static CouchdbConfigurator getInstance(){
    	return PROPERTY_CONFIGURATION;
    }
    
    public static CouchdbConfigurator getInstance(String propertyFile){
    	return new CouchdbConfigurator( propertyFile );
    }
    
    public boolean contains( String name ){
        return properties.containsKey( name );
    }
    
    public String getProperty( String name ){
    	System.out.println( name + " :: " + properties.getProperty( name ) );
        return properties.getProperty( name );
    }
    
    public int getIntProperty( String name, int defValue ){
        try {
            return Integer.parseInt( getProperty( name ) );
        }
        catch( Exception e ){}
        return defValue;
    }
    
    public boolean getBooleanProperty( String name, boolean defValue ){
        try {
            return Boolean.parseBoolean( getProperty( name ) );
        }
        catch( Exception e ){}
        return defValue;
    }
    
}
