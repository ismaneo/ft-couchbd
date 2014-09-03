package com.ftech.couchdb.models;

import com.ftech.couchdb.exception.CouchConfigurationException;

public final class DeclaredView extends View {
	
	private String viewPath;
	
	public DeclaredView( String viewPath ){
		this.viewPath = viewPath;
	}
	
	public String getViewId(){
		return null;
	}
	
	public String getViewPath(){
		return viewPath;
	}
	
	@Override
	public Integer getVersion() {
		throw new CouchConfigurationException( "DeclaredView can not have version" );
	}
	
	@Override
	public void setVersion(Integer version) {
		throw new CouchConfigurationException( "DeclaredView can not have version" );
	}

	@Override
	public String getName() {
		throw new CouchConfigurationException( "DeclaredView can not have a custom name" );
	}
	
	@Override
	public void setName(String name) {
		throw new CouchConfigurationException( "DeclaredView can not have a custom name" );
	}
	
	@Override
	public String getFunction() {
		throw new CouchConfigurationException( "DeclaredView can not have a custom function" );
	}
	
	@Override
	public void setFunction(String function) {
		throw new CouchConfigurationException( "DeclaredView can not have a custom function" );
	}
	
	public String getReduce() {
		throw new CouchConfigurationException( "DeclaredView can not have a reduce function" );
	}
	
	public void setReduce(String reduce) {
		throw new CouchConfigurationException( "DeclaredView can not have a reduce function" );
	}
	
}
