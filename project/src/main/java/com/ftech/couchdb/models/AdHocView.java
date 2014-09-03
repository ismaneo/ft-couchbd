package com.ftech.couchdb.models;

import com.ftech.couchdb.CouchdbConstants;
import com.ftech.couchdb.exception.CouchConfigurationException;

public final class AdHocView extends View {
	
	public AdHocView( String function ){
		super.setFunction( function );
	}
	
	public String getViewId(){
		return null;
	}
	
	public String getViewPath(){
		return CouchdbConstants.TEMP_VIEW_PATH;
	}
	
	@Override
	public Integer getVersion() {
		throw new CouchConfigurationException( "AdHocView do not have version" );
	}
	
	@Override
	public void setVersion(Integer version) {
		throw new CouchConfigurationException( "AdHocView do not have version" );
	}

	@Override
	public String getName() {
		throw new CouchConfigurationException( "AdHocView do not have name" );
	}
	
	@Override
	public void setName(String name) {
		throw new CouchConfigurationException( "AdHocView do not have name" );
	}
	
	private AdHocView setReduceFunction( String reduce ){
		setReduce( reduce );
		return this;
	}
	
	public static AdHocView getCount( String function ){
		return new AdHocView( function ).setReduceFunction( 
				CouchdbConstants.REDUCE_COUNT );
	}
	
	public static AdHocView getSum( String function ){
		return new AdHocView( function ).setReduceFunction( 
				CouchdbConstants.REDUCE_SUM );
	}
	
	public static AdHocView getStats( String function ){
		return new AdHocView( function ).setReduceFunction( 
				CouchdbConstants.REDUCE_STATS );
	}
	
}
