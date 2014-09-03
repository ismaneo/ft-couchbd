package com.ftech.commons.dao;


import com.ftech.commons.CouchdbConfigurator;
import com.ftech.couchdb.CouchdbDatasource;
import com.ftech.couchdb.CouchdbTemplate;
import com.ftech.couchdb.exception.CouchConfigurationException;

public final class CouchdbFactory {
	
	private CouchdbConfigurator config;
	private CouchdbDatasource datasource;
	private CouchdbTemplate template;
	
	private CouchdbFactory(){
		config = CouchdbConfigurator.getInstance();
		if( ! config.contains( FACTORY_HOST ) ){
			throw missingException( FACTORY_HOST );
		}
		if( ! config.contains( FACTORY_PORT ) ){
			throw missingException( FACTORY_PORT );
		}
		if( ! config.contains( FACTORY_DB ) ){
			throw missingException( FACTORY_DB );
		}
		createDatasource();
	}
	
	private CouchConfigurationException missingException( String property ){
		return new CouchConfigurationException( "Missing config property " + property );
	}
	
	private void createDatasource() {
		datasource = new CouchdbDatasource();
		datasource.setHostname( config.getProperty( FACTORY_HOST ) );
		datasource.setPort( config.getProperty( FACTORY_PORT ) );
		datasource.setDatabase( config.getProperty( FACTORY_DB ) );
		
		if( config.contains( FACTORY_USER ) ){
			datasource.setUser( config.getProperty( FACTORY_USER ) );
		}
		if( config.contains( FACTORY_PASS ) ){
			datasource.setPassword( config.getProperty( FACTORY_PASS ) );
		}
		if( config.contains( FACTORY_SSL ) ){
			datasource.setUseSSL( config.getBooleanProperty( FACTORY_SSL, false ) );
		}
		if( config.contains( FACTORY_CBYD ) ){
			datasource.setCreateByDefault( config.getBooleanProperty( FACTORY_CBYD, false ) );
		}
		if( config.contains( FACTORY_FREG ) ){
			datasource.setForceRegister( config.getBooleanProperty( FACTORY_FREG, false ) );
		}
	}
	
	public CouchdbTemplate getTemplate(){
		if( template == null ){
			template = createTemplate();
		}
		return template;
	}
	
	public CouchdbTemplate createTemplate(){
		CouchdbTemplate temp = new CouchdbTemplate();
		temp.setDatasource( datasource );
		return temp;
	}
	
	private static final String FACTORY_HOST = "couchdb.factory.hostname";
	private static final String FACTORY_PORT = "couchdb.factory.port";
	private static final String FACTORY_DB   = "couchdb.factory.database";
	private static final String FACTORY_USER = "couchdb.factory.username";
	private static final String FACTORY_PASS = "couchdb.factory.password";
	private static final String FACTORY_SSL  = "couchdb.factory.ssl";
	private static final String FACTORY_CBYD = "couchdb.factory.createByDefault";
	private static final String FACTORY_FREG = "couchdb.factory.forceRegister";
	
	/* Patron SINGLETON */
	private static final CouchdbFactory FACTORY = new CouchdbFactory();
	
	public static CouchdbFactory geInstance(){
		return FACTORY;
	}
	
}
