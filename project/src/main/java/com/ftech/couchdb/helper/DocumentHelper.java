package com.ftech.couchdb.helper;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.ftech.couchdb.CouchdbConstants;
import com.ftech.couchdb.exception.CouchDocumentException;
import com.ftech.couchdb.models.Document;
import com.ftech.couchdb.models.DocumentPart;

/**
 * DocumentHelper provides methods to transform Documents in JSON and JSON into Document.
 * 
 * @author ismael.escobar
 *
 */
public final class DocumentHelper implements CouchdbConstants {
	
	/**
	 * Can not generate Instances.
	 */
	private DocumentHelper(){}
	
	
	/** 
	 * Check if Document is not an Inner Class, because Inner Class can not be instantiated. 
	 * 
	 * @param document
	 * @return
	 */
	private static void checkDocument( Document document ){
		if( document == null ){
			throw new CouchDocumentException( "Document provided is null." );
		}
		if( document.getClass().getEnclosingClass() != null ){
			throw new CouchDocumentException( "The document can not be an inner class" );
		}
	}
	
	/**
	 * Convert to JSON a Document.
	 * 
	 * @param document
	 * @return
	 */
	protected static JSONObject convertToJson( Document document ){
		checkDocument( document );
		
		JSONObject body = new JSONObject();
		
		if( document.getId() != null ){
			body.put( JSON_DOC_ID, document.getId() );
		}
		if( document.getRevision() != null ){
			body.put( JSON_DOC_REV, document.getRevision() );
		}
		body.put( JSON_DOC_TYPE, document.getDocType() );
		body.put( JSON_DOC_CLASS, document.getClass().getCanonicalName() );
		
		return getJsonProperties( document, document.getClass(), body );
	}
	
	/**
	 * Get all fields of Document and transform it into JSON.
	 * 
	 * @param object
	 * @param clazz
	 * @param body
	 * @return
	 */
	private static JSONObject getJsonProperties(Object object, Class<?> clazz, JSONObject body){
		Field[] fields = clazz.getDeclaredFields();
		
		for( Field field: fields ){
			field.setAccessible( true );
			
			if( field.getName().equals( "id" ) || field.getName().equals( "revision" ) ){
				continue;
			}
			if( FieldHelper.isIgnoredField( field ) ){
				continue;
			}
			
			Object value = FieldHelper.get( field, object );
			if( value == null ){
				continue;
			}
			
			if( value instanceof Document ){
				if( FieldHelper.isJoinField( field ) ){
					body.put( field.getName(), getJsonProperties( value, 
							FieldHelper.getJoinFields( field ), new JSONObject() ) );
					continue;
				}
				throw new CouchDocumentException( "Document can not be inside of another document. ["
						+ field.getName() + " in " + object.getClass().getCanonicalName() + "]" );
			}
			
			if( value instanceof DocumentPart ){
				body.put( field.getName(), getJsonProperties( 
						value, value.getClass(), new JSONObject() ) );
			}
			else if( FieldHelper.isBasicField( field ) ){
				JsonHelper.set( body, field.getName(), value );
			}
			else if( FieldHelper.isArrayField( field ) ){
				body.put( field.getName(), getJsonArray ( field, value ) );
			}
			else {
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		
		if( object instanceof Document && ! clazz.getSuperclass().equals( Document.class ) ) {
			return getJsonProperties( object, clazz.getSuperclass(), body );
		}
		return body;
	}


	/**
	 * Get all fields of Document and transform it into JSON.
	 * 
	 * @param object
	 * @param fields
	 * @param body
	 * @return
	 */
	private static JSONObject getJsonProperties(Object object, Field[] fields, JSONObject body){
		
		for( Field field: fields ){
			field.setAccessible( true );
			if( field.getName().equals( "id" ) || field.getName().equals( "revision" ) ){
				continue;
			}
			
			Object value = FieldHelper.get( field, object );
			if( value == null ){
				continue;
			}
			
			if( value instanceof Document ){
				if( FieldHelper.isJoinField( field ) ){
					body.put( field.getName(), getJsonProperties( value, 
							FieldHelper.getJoinFields( field ), new JSONObject() ) );
					continue;
				}
				throw new CouchDocumentException( "Document can not be inside of another document. ["
						+ field.getName() + " in " + object.getClass().getCanonicalName() + "]" );
			}
			
			if( value instanceof DocumentPart ){
				body.put( field.getName(), getJsonProperties( 
						value, value.getClass(), new JSONObject() ) );
			}
			else if( FieldHelper.isBasicField( field ) ){
				JsonHelper.set( body, field.getName(), value );
			}
			else if( FieldHelper.isArrayField( field ) ){
				body.put( field.getName(), getJsonArray ( field, value ) );
			}
			else {
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		return body;
	}
	
	
	
	/**
	 * Obtain a JSON array for Java array, List or Set.
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	private static Object getJsonArray( Field field, Object value ) {
		Class<?> type = FieldHelper.getArrayType( field );
		
		if( type.isArray() || type.equals( List.class ) || type.equals( Set.class ) ){
			throw new CouchDocumentException( 
					"Field " + field.getName() + " is an array of arrays." );
		}
		
		JSONArray arr = new JSONArray();
		Iterator<?> it = FieldHelper.getArrayIterator( field, value );
		
		while( it.hasNext() ){
			Object arrItem = it.next();
			
			if( arrItem instanceof Document ){
				if( FieldHelper.isJoinField( field ) ){
					arr.add( getJsonProperties( arrItem, 
							FieldHelper.getJoinFields( field, type ), 
							new JSONObject() ) );
					continue;
				}
				throw new CouchDocumentException( 
						"Document can not be inside of another document. ["
						+ field.getName() + "]" );
			}
			
			if( arrItem instanceof DocumentPart ){
				arr.add( getJsonProperties( arrItem, 
						arrItem.getClass(), new JSONObject() ) );
			}
			else if( FieldHelper.isBasicType( type ) ){
				JsonHelper.set( arr, arrItem );
			}
			else {
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		return arr;
	}
	
	
	/**
	 * Complete a document from JSON response.
	 * 
	 * @param document
	 * @param body
	 */
	protected static void completeDocument( Document document, JSONObject body ) {
		checkDocument( document );
		
		try {
			if( document.getId() == null || document.getId().isEmpty() ) {
				document.setId( body.getString( JSON_RESP_ID ) );
			}
			document.setRevision( body.getString( JSON_RESP_REV ) );
		} 
		catch (JSONException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	
	/**
	 * Load all fields of the Document form JSON response.
	 * 
	 * @param document
	 * @param body
	 */
	protected static void loadDocument( Document document, JSONObject body ) {
		checkDocument( document );
		
		try {
			if( ! body.containsKey( JSON_DOC_CLASS ) ){
				throw new CouchDocumentException( "Class of document is not defined." );
			}
			
			String className = body.getString( JSON_DOC_CLASS );
			if( ! document.getClass().getCanonicalName().equals( className ) ){
				throw new CouchDocumentException( "Unexpected document class " + className );
			}
			
			document.setId( body.getString( JSON_DOC_ID ) );
			document.setRevision( body.getString( JSON_DOC_REV ) );
			loadProperties( document, document.getClass(), body );
		} 
		catch (JSONException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	/**
	 * Load all fields values of the Document from JSON properties.
	 * 
	 * @param object
	 * @param clazz
	 * @param body
	 */
	private static void loadProperties(Object object, Class<?> clazz, JSONObject body) {
		Field[] fields = clazz.getDeclaredFields();
		for( Field field: fields ){
			field.setAccessible( true );
			
			if( FieldHelper.isIgnoredField( field ) ){
				continue;
			}
			if( ! body.containsKey( field.getName() ) ){
				continue;
			}
			
			if( FieldHelper.isBasicField( field ) ){
				FieldHelper.set( field, object, JsonHelper.get( 
						body, field.getName(), field.getType() ) );
				continue;
			}
			if( FieldHelper.isArrayField( field ) ){
				if( body.get( field.getName() ) instanceof JSONArray ){
					loadArray( field, object, body.getJSONArray( field.getName() ) );
					continue;
				}
				throw new CouchDocumentException( 
						"Error on response, field type is an array [" + field.getName() + "]" );
			}
				
			Object temp = FieldHelper.getInstance( field );
			JSONObject value = body.getJSONObject( field.getName() );
			
			if( temp instanceof DocumentPart ){
				loadProperties( temp, temp.getClass(), value );
				FieldHelper.set( field, object, temp );
			}
			else if( temp instanceof Document && FieldHelper.isJoinField( field ) ){
				loadProperties( temp, FieldHelper.getJoinFields( field ), value );
				FieldHelper.set( field, object, temp );
			}
			else{
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		
		if( object instanceof Document && ! clazz.getSuperclass().equals( Document.class ) ) {
			loadProperties( object, clazz.getSuperclass(), body );
		}
	}
	
	
	/**
	 * Load all fields values of the Document from JSON properties.
	 * 
	 * @param object
	 * @param fields
	 * @param body
	 */
	private static void loadProperties(Object object, Field[] fields, JSONObject body) {
		for( Field field: fields ){
			field.setAccessible( true );
			
			if( ! body.containsKey( field.getName() ) ){
				continue;
			}
			
			if( FieldHelper.isBasicField( field ) ){
				FieldHelper.set( field, object, JsonHelper.get(
						body, field.getName(), field.getType() ) );
				continue;
			}
			if( FieldHelper.isArrayField( field ) ){
				if( body.get( field.getName() ) instanceof JSONArray ){
					loadArray( field, object, body.getJSONArray( field.getName() ) );
					continue;
				}
				throw new CouchDocumentException( 
						"Error on response, field type is an array [" + field.getName() + "]" );
			}
				
			Object temp = FieldHelper.getInstance( field );
			JSONObject value = body.getJSONObject( field.getName() );
			
			if( temp instanceof DocumentPart ){
				loadProperties( temp, temp.getClass(), value );
				FieldHelper.set( field, object, temp );
			}
			else if( temp instanceof Document && FieldHelper.isJoinField( field ) ){
				loadProperties( temp, FieldHelper.getJoinFields( field ), value );
				FieldHelper.set( field, object, temp );
			}
			else{
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
	}
	
	
	/**
	 * Load the array property from an JSONArray.
	 * 
	 * @param field
	 * @param object
	 * @param arr
	 */
	private static void loadArray( Field field, Object object, JSONArray arr ) {
		Class<?> type = FieldHelper.getArrayType( field );
		
		if( type.isArray() || type.equals( List.class ) || type.equals( Set.class ) ){
			throw new CouchDocumentException( 
					"Field " + field.getName() + " is an array of arrays." );
		}
		
		Object temparr = FieldHelper.getInstance( field, arr.size() );
		
		for( int index = 0; index < arr.size(); index++ ){
			if( FieldHelper.isBasicType( type ) ){
				FieldHelper.add( temparr, index, 
						JsonHelper.get( arr, index, type ) );
				continue;
			}
			
			Object temp = FieldHelper.getInstance( type );
			JSONObject value = arr.getJSONObject( index );
			
			if( temp instanceof DocumentPart ){
				loadProperties( temp, temp.getClass(), value );
				FieldHelper.add( temparr, index, temp );
			}
			else if( temp instanceof Document && FieldHelper.isJoinField( field ) ){
				loadProperties( temp, FieldHelper.getJoinFields( field, type ), value );
				FieldHelper.add( temparr, index, temp );
			}
			else{
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		//Complete the field.
		FieldHelper.set( field, object, temparr );
	}
	
}
