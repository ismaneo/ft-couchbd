package com.ftech.commons.helper;

import com.ftech.commons.CouchdbConfigurator;
import com.ftech.couchdb.exception.CouchConfigurationException;
import com.ftech.couchdb.models.View;

public final class CouchdbViewHelper {
	
	private CouchdbViewHelper(){}
	
	private static final CouchdbConfigurator CONFIG = 
			CouchdbConfigurator.getInstance( "views" );
	
	private static final String NAME = ".name";
	private static final String FUNC = ".function";
	private static final String VERS = ".version";
	
	public static View getView( String viewProperty ){
		if( ! CONFIG.contains( viewProperty + NAME ) ){
			throw missingException( viewProperty + NAME );
		}
		return getView( CONFIG.getProperty( viewProperty + NAME ), viewProperty );
	}
	
	public static View getView( String name, String viewProperty ){
		if( ! CONFIG.contains( viewProperty + FUNC ) ){
			throw missingException( viewProperty + FUNC );
		}
		
		View view = new View();
		view.setName( name );
		view.setFunction( CONFIG.getProperty( viewProperty + FUNC ) );
		
		if( ! CONFIG.contains( viewProperty + VERS ) ){
			view.setVersion( CONFIG.getIntProperty( viewProperty + VERS, 0 ) );
		}
		return view;
	}
	
	private static CouchConfigurationException missingException( String propertyName ){
		return new CouchConfigurationException( 
				"Missing property " + propertyName + " in /config/views.properties" );
	}
	
}
