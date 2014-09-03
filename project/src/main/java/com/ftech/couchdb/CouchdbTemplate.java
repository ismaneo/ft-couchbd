package com.ftech.couchdb;

import java.util.List;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

import com.ftech.commons.dao.CouchdbMapper;
import com.ftech.couchdb.exception.CouchConfigurationException;
import com.ftech.couchdb.exception.CouchException;
import com.ftech.couchdb.helper.CouchdbJsonHelper;
import com.ftech.couchdb.models.AdHocView;
import com.ftech.couchdb.models.DeclaredView;
import com.ftech.couchdb.models.Document;
import com.ftech.couchdb.models.View;
import com.ftech.couchdb.models.ViewParameters;


public final class CouchdbTemplate implements CouchdbConstants {
	
	private static final Logger logger = Logger.getLogger( CouchdbTemplate.class );
	
	private CouchdbConnection connection;
	private CouchdbDatasource datasource;
	private String charset = "UTF-8";
	
	/**
	 * Save document in database
	 * @param document
	 */
	public void persist( Document document ){
		JSONObject body = CouchdbJsonHelper.convertToJson( document );
		logger.info( body.toString() );
		
		CouchdbResponse resp;
		if( document.getId() == null || document.getId().isEmpty() ){
			resp = getConnection().post( getUrl(), body.toString() );
		}
		else {
			resp = getConnection().put( getUrl( document.getId() ), body.toString() );
		}
		
		if( resp.isOk() ) {
			CouchdbJsonHelper.completeDocument( document, resp.getBodyAsJSON() );
		} 
		else {
			throw new CouchException( resp.getErrorReason() );
		}
	}
	
	/**
	 * Read document content from database
	 * @param document
	 */
	public <T extends Document> T load( T document ) {
		if( document.getId() == null || document.getId().isEmpty() ){
			throw new CouchException( "The document id is required." );
		}
		
		CouchdbResponse resp = getConnection().get( getUrl( document.getId() ) );
		if( ! resp.isOk() ){
			throw new CouchException( resp.getErrorReason() );
		}
		
		CouchdbJsonHelper.loadDocument( document, resp.getBodyAsJSON() );
		return document;
	}

	/**
	 * Delete document from database
	 * @param document
	 */
	public void remove( Document document ) {
		if( document.getId() == null || document.getId().isEmpty() ){
			throw new CouchException( "The document id is required." );
		}
		if( document.getRevision() == null || document.getRevision().isEmpty() ){
			throw new CouchException( "The document revision is required." );
		}
		
		CouchdbResponse resp = getConnection().delete( 
				getUrl( document.getId() ) + "?rev=" + document.getRevision() );
		if( ! resp.isOk() ){
			throw new CouchException( resp.getErrorReason() );
		}
	}
	
	/**
	 * Save view template in database
	 * @param view
	 */
	public boolean registerView( View view ){
		if( view instanceof AdHocView ){
			throw new CouchException( "The AdHocViews can not be registered." );
		}
		if( view instanceof DeclaredView ){
			throw new CouchException( "The DeclaredViews can not be registered." );
		}
		
		CouchdbResponse resp = getConnection().get( getUrl( view.getViewId() ) );
		if( resp.isOk() ){
			Integer viewVersion = CouchdbJsonHelper.getVersion( resp.getBodyAsJSON() );
			String viewRevision = "?rev=" + CouchdbJsonHelper.getRevision( resp.getBodyAsJSON() );
			
			if( datasource.isForceRegister() ){
				getConnection().delete( getUrl( view.getViewId() ) + viewRevision );
			}
			else if( viewVersion < view.getVersion() ){
				getConnection().delete( getUrl( view.getViewId() ) + viewRevision );
			}
			else{
				return false;
			}
		}
		
		JSONObject body = CouchdbJsonHelper.viewToJson( view );
		logger.info( body.toString() );
		
		resp = getConnection().put( 
				getUrl( view.getViewId() ), body.toString() );
		
		if( datasource.isForceRegister() && ! resp.isOk() ){
			throw new CouchException( resp.getErrorReason() );
		}
		return resp.isOk();
	}
	
	/**
	 * Save a CouchDocument in database
	 * @param document
	 */
	public void persist(Object document) {
		if( document instanceof Document ){
			logger.debug( "Cange persist to document ... " );
			persist( (Document) document );
			return;
		}
		
		JSONObject body = CouchdbJsonHelper.convertToJson( document );
		logger.info( body.toString() );
		
		CouchdbResponse resp;
		if( body.containsKey( JSON_DOC_ID ) ){
			resp = getConnection().put( 
					getUrl( body.getString( JSON_DOC_ID ) ), body.toString() );
		}
		else {
			resp = getConnection().post( getUrl(), body.toString() );
		}
		
		if( resp.isOk() ) {
			CouchdbJsonHelper.completeDocument( document, resp.getBodyAsJSON() );
		} 
		else {
			throw new CouchException( resp.getErrorReason() );
		}
	}
	
	public Object load( Object document ) {
		if( document instanceof Document ){
			logger.debug( "Change loader to document ... " );
			return load( (Document) document );
		}
		
		String documentId = CouchdbJsonHelper.getDocumentId( document );
		if( documentId == null || documentId.isEmpty() ){
			throw new CouchException( "The document id is required." );
		}
		
		CouchdbResponse resp = getConnection().get( getUrl( documentId ) );
		if( ! resp.isOk() ){
			throw new CouchException( resp.getErrorReason() );
		}
		
		CouchdbJsonHelper.loadDocument( document, resp.getBodyAsJSON() );
		return document;
	}
	
	public void remove( Object document ) {
		String documentId = CouchdbJsonHelper.getDocumentId( document );
		String documentRevision = CouchdbJsonHelper.getDocumentRevision( document );
		
		if( documentId == null || documentId.isEmpty() ){
			throw new CouchException( "The document id is required." );
		}
		if( documentRevision == null || documentRevision.isEmpty() ){
			throw new CouchException( "The document revision is required." );
		}
		
		CouchdbResponse resp = getConnection().delete( 
				getUrl( documentId ) + "?rev=" + documentRevision );
		if( ! resp.isOk() ){
			throw new CouchException( resp.getErrorReason() );
		}
	}
	
	/**
	 * Get all results from AdHocView
	 * @param view
	 * @param params
	 */
	public CouchdbViewResults query( AdHocView view, ViewParameters params ) {
		CouchdbResponse resp = getConnection().post( 
				getUrl( view.getViewPath() ),
				CouchdbJsonHelper.functionToJson( view ).toString(),
				CouchdbJsonHelper.generateQueryString( params, charset ) );
		
		if( ! resp.isOk() ){
			throw new CouchException( resp.getErrorReason() );
		}
		return new CouchdbViewResults( resp.getBodyAsJSON() );
	}
	
	/**
	 * Get all results from View Template
	 * @param view
	 */
	public CouchdbViewResults query( View view ) {
		ViewParameters params = null;
		if( view instanceof AdHocView ){
			return query( (AdHocView) view, params );
		}
		return query( view, params );
	}
	
	/**
	 * Get all results from View Template
	 * @param view
	 * @param params
	 */
	public CouchdbViewResults query( View view, ViewParameters params ) {
		CouchdbResponse resp = null;
		if( params == null ){
			resp = getConnection().get( getUrl( view.getViewPath() ) );
		}
		else{
			resp = getConnection().get( getUrl( view.getViewPath() ),
					CouchdbJsonHelper.generateQueryString( params, charset ) );
		}
		if( ! resp.isOk() ){
			throw new CouchException( resp.getErrorReason() );
		}
		return new CouchdbViewResults( resp.getBodyAsJSON() );
	}
	
	/**
	 * Get all results from View and return a List of Objects in mapper
	 * @param view
	 * @param params
	 * @param mapper
	 */
	public <T> List<T> query( View view, ViewParameters params, CouchdbMapper<T> mapper ) {
		if( view instanceof AdHocView ){
			return query( (AdHocView) view, params ).getResults( mapper );
		}
		return query( view, params ).getResults( mapper );
	}
	
	/**
	 * Get all results from View and return a List of Objects in mapper
	 * @param view
	 * @param params
	 * @param mapper
	 */
	public <T> List<T> query( View view, CouchdbMapper<T> mapper ) {
		return query( view, null, mapper );
	}
	
	/**
	 * Get all results from View and return a list of Documents
	 * @param view
	 * @param params
	 * @param clazz
	 */
	public <T> List<T> query( View view, ViewParameters params, Class<T> clazz ) {
		if( view instanceof AdHocView ){
			return query( (AdHocView) view, params ).getResults( clazz );
		}
		return query( view, params ).getResults( clazz );
	}
	
	/**
	 * Get all results from View and return a list of Documents
	 * @param view
	 * @param params
	 * @param clazz
	 */
	public <T> List<T> query( View view, Class<T> clazz ) {
		return query( view, null, clazz );
	}
	
	/*
	 * Construct a URL to send request to database
	 */
	private String getUrl( String ... params ) {
		StringBuilder url = new StringBuilder();
		if( params != null && params.length > 0 ){
			for( String p: params ) url.append( "/" ).append( p );
		}
		return datasource.getDatabase() + url.toString();
	}
	
	/**
	 * Return a connection to database
	 * @return CouchdbConnection
	 */
	private CouchdbConnection getConnection() {
		if( connection == null ){
			throw new CouchConfigurationException( "Error, connection was not created." );
		}
		return connection;
	}
	
	/**
	 * Set CoachDB Datasource
	 * @param datasource
	 */
	public void setDatasource(CouchdbDatasource datasource) {
		if( datasource == null ){
			throw new CouchConfigurationException( "Error, setted datasource is null" );
		}
		this.datasource = datasource;
		this.connection = datasource.getConnection();
	}
	
	/**
	 * Set Charset encode, by default UTF-8
	 * @param charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
