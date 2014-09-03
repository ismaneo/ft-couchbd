package com.ftech.couchdb.helper;


import org.apache.log4j.Logger;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.ftech.couchdb.CouchdbConstants;
import com.ftech.couchdb.exception.CouchDatabaseException;
import com.ftech.couchdb.exception.CouchDocumentException;
import com.ftech.couchdb.models.AdHocView;
import com.ftech.couchdb.models.DeclaredView;
import com.ftech.couchdb.models.Document;
import com.ftech.couchdb.models.View;
import com.ftech.couchdb.models.ViewParameters;

public final class CouchdbJsonHelper implements CouchdbConstants {
	
	private static final Logger logger = Logger.getLogger( CouchdbJsonHelper.class );
	
	/**
	 * Can not generate instance.
	 */
	private CouchdbJsonHelper(){}
	
	public static JSONObject convertToJson( Document document ) {
		logger.debug( "convertToJson" );
		return DocumentHelper.convertToJson( document );
	}
	
	public static JSONObject convertToJson( Object document ) {
		logger.debug( "convertToJson" );
		if( document instanceof Document ){
			return convertToJson( (Document) document );
		}
		return CouchDocumentHelper.convertToJson( document );
	}
	
	public static void completeDocument( Document document, JSONObject body ) {
		logger.debug( "completeDocument" );
		DocumentHelper.completeDocument( document, body );
	}
	
	public static void completeDocument( Object document, JSONObject body ) {
		logger.debug( "completeDocument" );
		if( document instanceof Document ){
			completeDocument( (Document) document, body );
			return;
		}
		CouchDocumentHelper.completeDocument( document, body );
	}
	
	public static void loadDocument( Document document, JSONObject body ) {
		logger.debug( "loadDocument" );
		DocumentHelper.loadDocument( document, body );
	}
	
	public static void loadDocument( Object document, JSONObject body ) {
		logger.debug( "loadDocument" );
		if( document instanceof Document ){
			loadDocument( (Document) document, body );
			return;
		}
		CouchDocumentHelper.loadDocument( document, body );
	}
	
	public static Object fillDocument( Class<?> clazz, JSONObject body ) {
		logger.debug( "fillDocument" );
		Object document = FieldHelper.getInstance( clazz );
		
		if( document instanceof Document ){
			DocumentHelper.loadDocument( (Document) document, body );
		}
		else {
			CouchDocumentHelper.loadDocument( document, body );
		}
		return document;
	}
	
	public static String getDocumentId( Object document ) {
		logger.debug( "getDocumentId" );
		if( document instanceof Document ){
			return ((Document) document).getId();
		}
		return CouchDocumentHelper.getDocumentId( document );
	}

	public static String getDocumentRevision( Object document ) {
		logger.debug( "getDocumentRevision" );
		if( document instanceof Document ){
			return ((Document) document).getRevision();
		}
		return CouchDocumentHelper.getDocumentRevision( document );
	}
	
	public static JSONObject viewToJson(View view) {
		logger.debug( "viewToJson" );
		if( view instanceof AdHocView || view instanceof DeclaredView ){
			throw new CouchDatabaseException( "The AdHocViews and DeclaredViews can not be registered." );
		}
		if( view.getViewId() == null || view.getViewId().isEmpty() ){
			throw new CouchDatabaseException( "The view id is required." );
		}
		if( view.getName() == null || view.getName().isEmpty() ){
			throw new CouchDatabaseException( "The view id is required." );
		}
		if( view.getFunction() == null || view.getFunction().isEmpty() ){
			throw new CouchDatabaseException( "The view function is required." );
		}
		
		JSONObject function = new JSONObject();
		function.put( JSON_MAP, " " + view.getFunction() + " " );
		if( view.getReduce() != null ){
			if( view.getReduce().startsWith( "function" ) ){
				function.put( JSON_REDUCE, " " + view.getReduce() + " " );
			}
			else{
				function.put( JSON_REDUCE, view.getReduce() );
			}
		}
		
		JSONObject customView = new JSONObject();
		customView.put( view.getName(), function );
		
		JSONObject body = new JSONObject();
		body.put( JSON_VIEW, customView );
		
		body.put( "language", "javascript" );
		if( view.getVersion() != null ){
			body.put( JSON_VERSION, view.getVersion() );
		}
		return body;
	}
	
	public static JSONObject functionToJson( AdHocView view ) {
		logger.debug( "functionToJson" );
		JSONObject function = new JSONObject();
		function.put( JSON_MAP, " " + view.getFunction() + " " );
		
		if( view.getReduce() != null ){
			if( view.getReduce().startsWith( "function" ) ){
				function.put( JSON_REDUCE, " " + view.getReduce() + " " );
			}
			else{
				function.put( JSON_REDUCE, view.getReduce() );
			}
		}
		logger.info( function );
		return function;
	}
	
	public static String getId( JSONObject body ) {
		logger.debug( "getId" );
		return getString( body, JSON_DOC_ID );
	}

	public static String getRevision( JSONObject body ) {
		logger.debug( "getRevision" );
		return getString( body, JSON_DOC_REV );
	}

	public static Integer getVersion( JSONObject body ) {
		logger.debug( "getVersion" );
		return getInt( body, JSON_VERSION );
	}
	
	
	public static String getString( JSONObject body, String property ){
		logger.debug( "getString" );
		try{
			if( body.containsKey( property ) ){
				return body.getString( property );
			}
			return null;
		}
		catch( JSONException e ){
			throw new CouchDocumentException( e );
		}
	}
	
	public static int getInt( JSONObject body, String property ){
		logger.debug( "getInt" );
		try{
			if( body.containsKey( property ) ){
				return body.getInt( property );
			}
			return 0;
		}
		catch( JSONException e ){
			throw new CouchDocumentException( e );
		}
	}

	public static String generateQueryString(ViewParameters params, String charset) {
		return ViewHelper.generateQueryString( params, charset );
	}
	
}
