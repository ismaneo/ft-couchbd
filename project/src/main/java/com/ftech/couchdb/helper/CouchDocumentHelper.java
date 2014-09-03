package com.ftech.couchdb.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.ftech.couchdb.CouchdbConstants;
import com.ftech.couchdb.annotations.CouchDocument;
import com.ftech.couchdb.exception.CouchDocumentException;


/**
 * CouchDocumentHelper provides methods to transform CouchDocuments in JSON and JSON into CouchDocument.
 * 
 * @author ismael.escobar
 *
 */
public final class CouchDocumentHelper implements CouchdbConstants {
	
	private static final Logger logger = Logger.getLogger( CouchDocumentHelper.class );
	
	/**
	 * Can not generate Instances.
	 */
	private CouchDocumentHelper(){}
	
	
	/**
	 * Convert to JSON a CouchDocument.
	 * 
	 * @param document
	 * @return
	 */
	protected static JSONObject convertToJson( Object document ){
		checkCouchDocument( document );
		
		JSONObject body = new JSONObject();
		body.put( JSON_DOC_TYPE, document.getClass().getSimpleName() );
		body.put( JSON_DOC_CLASS, document.getClass().getCanonicalName() );
		
		return getJsonProperties( document, body );
	}
	
	
	/**
	 * Get all fields of CouchDocument and transform it into JSON.
	 * 
	 * @param object
	 * @param body
	 * @return
	 */
	private static JSONObject getJsonProperties(Object object, JSONObject body){
		MappedDocument md = checkCouchDocument( object );
		Object value = null;
		
		value = FieldHelper.get( md.idField, object );
		if( value != null ){
			body.put( JSON_DOC_ID, value );
		}
		
		value = FieldHelper.get( md.revisionField, object );
		if( value != null ){
			body.put( JSON_DOC_REV, value );
		}
		
		for( Field field: md.basicFields ){
			value = FieldHelper.get( field, object );
			if( value != null ){
				JsonHelper.set( body, field.getName(), value );
			}
		}
		
		for( Field field: md.arrayFields ){
			value = FieldHelper.get( field, object );
			if( value != null ){
				body.put( field.getName(), getJsonArray( field, value ) );
			}
		}
		
		for( Field field: md.joinFields ){
			value = FieldHelper.get( field, object );
			if( value != null ){
				body.put( field.getName(), getJsonProperties( value, 
						FieldHelper.getJoinFields( field ), new JSONObject() ) );
			}
		}
		return body;
	}
	
	
	/**
	 * Get all fields of CouchDocument and transform it into JSON.
	 * 
	 * @param object
	 * @param joined
	 * @param body
	 * @return
	 */
	private static JSONObject getJsonProperties(Object object, Field[] joined, JSONObject body) {
		if( ! isCouchDocument( object ) ){
			throw new CouchDocumentException( "The property is not a Document for CouchDB. ["
					+ object.getClass().getCanonicalName() + "]" );
		}
		
		try{
			for( Field field: joined ){
				field.setAccessible( true );
				
				Object value = FieldHelper.get( field, object );
				if( value == null ){
					continue;
				}
				
				if( FieldHelper.isBasicField( field ) ){
					JsonHelper.set( body, field.getName(), value );
				}
				else if( FieldHelper.isArrayField( field ) ){
					body.put( field.getName(), getJsonArray( field, value ) );
				}
				else if( FieldHelper.isJoinField( field ) ){
					body.put( field.getName(), getJsonProperties( value, 
							FieldHelper.getJoinFields( field ), new JSONObject() ) );
				}
				else {
					throw new CouchDocumentException( "Field type is not recognized [" 
							+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
				}
			}
		} catch (JSONException e) {
			throw new CouchDocumentException( e );
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
			
			if( FieldHelper.isBasicType( type ) ){
				JsonHelper.set( arr, arrItem );
			}
			else if( FieldHelper.isJoinField( field ) ){
				arr.add( getJsonProperties( arrItem, 
						FieldHelper.getJoinFields( field, type ), 
						new JSONObject() ) );
			}
			else {
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		return arr;
	}
	
	
	/**
	 * Complete a CouchDocument from JSON response.
	 * 
	 * @param document
	 * @param body
	 */
	protected static void completeDocument( Object document, JSONObject body ) {
		MappedDocument md = checkCouchDocument( document );
		try{
			if( body.containsKey( JSON_RESP_ID ) ){
				FieldHelper.set( md.idField, document, body.get( JSON_RESP_ID ) );
			}
			if( body.containsKey( JSON_RESP_REV ) ){
				FieldHelper.set( md.revisionField, document, body.get( JSON_RESP_REV ) );
			}
		} catch (JSONException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	/**
	 * Load all fields of the CouchDocument form JSON response.
	 * 
	 * @param document
	 * @param body
	 */
	protected static void loadDocument( Object document, JSONObject body ) {
		checkCouchDocument( document );
		
		try {
			if( ! body.containsKey( JSON_DOC_CLASS ) ){
				throw new CouchDocumentException( "Class of document is not defined." );
			}
			
			String className = body.getString( JSON_DOC_CLASS );
			if( ! document.getClass().getCanonicalName().equals( className ) ){
				throw new CouchDocumentException( "Unexpected document class " + className );
			}
			
			loadProperties( document, body );
		} 
		catch (JSONException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	
	/**
	 * Load all fields values of the CouchDocument from JSON properties.
	 * 
	 * @param object
	 * @param body
	 */
	private static void loadProperties(Object object, JSONObject body) {
		MappedDocument md = checkCouchDocument( object );
		Object value = null;
		
		try{
			if( body.containsKey( JSON_DOC_ID ) ){
				FieldHelper.set( md.idField, object, body.getString( JSON_DOC_ID ) );
			}
			if( body.containsKey( JSON_DOC_REV ) ){
				FieldHelper.set( md.revisionField, object, body.getString( JSON_DOC_REV ) );
			}
			
			for( Field field: md.basicFields ){
				if( body.containsKey( field.getName() ) ){
					value = JsonHelper.get( body, field.getName(), field.getType() );
					FieldHelper.set( field, object, value );
				}
			}
			
			for( Field field: md.arrayFields ){
				if( body.get( field.getName() ) instanceof JSONArray ){
					loadArray( field, object, body.getJSONArray( field.getName() ) );
				}
			}

			for( Field field: md.joinFields ){
				if( body.containsKey( field.getName() ) ){
					Object temp = FieldHelper.getInstance( field );
					loadProperties( temp, FieldHelper.getJoinFields( field ), 
							body.getJSONObject( field.getName() ) );
					FieldHelper.set( field, object, temp );
				}
			}
		} catch (JSONException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	
	/**
	 * Load all fields values of the CouchDocument from JSON properties.
	 * 
	 * @param object
	 * @param joined
	 * @param body
	 */
	private static void loadProperties(Object object, Field[] joined, JSONObject body) {
		if( ! isCouchDocument( object ) ){
			throw new CouchDocumentException( "The property is not a Document for CouchDB. ["
					+ object.getClass().getCanonicalName() + "]" );
		}
		
		try{
			for( Field field: joined ){
				field.setAccessible( true );
				if( ! body.containsKey( field.getName() ) ){
					continue;
				}
				
				Object value = body.get( field.getName() );
				if( FieldHelper.isBasicField( field ) ){
					FieldHelper.set( field, object, value );
				}
				else if( FieldHelper.isJoinField( field ) ){
					Object temp = FieldHelper.getInstance( field );
					loadProperties( temp, 
							FieldHelper.getJoinFields( field ), 
							body.getJSONObject( field.getName() ) );
					FieldHelper.set( field, object, temp );
				}
				else if( value != null ){
					throw new CouchDocumentException( "Field type is not recognized [" 
							+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
				}
			}
		} catch (JSONException e) {
			throw new CouchDocumentException( e );
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
				FieldHelper.add( temparr, index, JsonHelper.get( arr, index, type ) );
				continue;
			}

			Object temp = FieldHelper.getInstance( type );
			JSONObject value = arr.getJSONObject( index );
			
			if( FieldHelper.isJoinField( field ) ){
				loadProperties( temp, FieldHelper.getJoinFields( field, type ), value );
				FieldHelper.add( temparr, index, temp );
			}
			else if( value != null ){
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		//Complete the field.
		FieldHelper.set( field, object, temparr );
	}
	
	
	/**
	 * Get the Id of CouchDocument.
	 * 
	 * @param document
	 * @return
	 */
	protected static String getDocumentId( Object document ) {
		MappedDocument md = checkCouchDocument( document );
		return (String) FieldHelper.get( md.idField, document );	
	}
	
	/**
	 * Get the Revision of CouchDocument.
	 * 
	 * @param document
	 * @return
	 */
	protected static String getDocumentRevision( Object document ) {
		MappedDocument md = checkCouchDocument( document );
		return (String) FieldHelper.get( md.revisionField, document );	
	}
	
	/**
	 * Check if the object is a CouchDocument
	 * 
	 * @param document
	 * @return
	 */
	protected static boolean isCouchDocument( Object document ){
		Annotation a = document.getClass().getAnnotation( CouchDocument.class );
		return ( a instanceof CouchDocument );
	}
	
	
	
	/**
	 * This class is used to map Class and fields from annotated @CouchDocument classes.
	 * 
	 * @author ismael.escobar
	 */
	static class MappedDocument {
		Field idField;
		Field revisionField;
		List<Field> basicFields = new ArrayList<Field>();
		List<Field> arrayFields = new ArrayList<Field>();
		List<Field> joinFields = new ArrayList<Field>();
	}
	
	
	/* This is the Map of All annotated classes  */
	private static final Map<Class<?>, MappedDocument> ANNOTATED_DOCS = new HashMap<Class<?>, MappedDocument>();
	
	
	/**
	 * Generate the MappedDocument of the annotated class
	 * @param clazz
	 * @return
	 */
	private static MappedDocument generate( Class<?> clazz ) {
		logger.debug( "Mapping ... " + clazz );
		
		int idCount = 0;
		int revCount = 0;
		MappedDocument md = new MappedDocument();
		
		Field[] fields = clazz.getDeclaredFields();
		
		for( Field field: fields ){
			field.setAccessible( true );
			if( FieldHelper.isIgnoredField( field ) ){
				continue;
			}
			
			if( FieldHelper.isIdField( field ) ){
				md.idField = field;
				idCount++;
			}
			else if( FieldHelper.isRevisionField( field ) ){
				md.revisionField = field;
				revCount++;
			}
			else if( FieldHelper.isBasicField( field ) ) {
				md.basicFields.add( field );
			}
			else if( FieldHelper.isArrayField( field ) ) {
				md.arrayFields.add( field );
			}
			else if( FieldHelper.isJoinField( field ) ){
				md.joinFields.add( field );
			}
			else {
				throw new CouchDocumentException( "Field type is not recognized [" 
						+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
			}
		}
		
		if( idCount == 0 ){
			throw new CouchDocumentException( 
					"Document must have one id field. [" + clazz.getCanonicalName() +"]" );
		}
		if( idCount > 1 ){
			throw new CouchDocumentException( 
					"Document can not have more than one id field.[" + clazz.getCanonicalName() +"]" );
		}
		if( revCount == 0 ){
			throw new CouchDocumentException( 
					"Document must have one revision field.[" + clazz.getCanonicalName() +"]" );
		}
		if( revCount > 1 ){
			throw new CouchDocumentException( 
					"Document can not have more than one revision field.[" + clazz.getCanonicalName() +"]" );
		}
		return md;
	}
	
	
	/**
	 * This method checks if object is an annotated class of @CouchDocument.
	 * 
	 * @param document
	 * @return
	 */
	private static MappedDocument checkCouchDocument( Object document ){
		if( document == null ){
			throw new CouchDocumentException( "Object provided as document is null." );
		}
		
		Annotation a = document.getClass().getAnnotation( CouchDocument.class );
		if( a instanceof CouchDocument ){
			if( ! ANNOTATED_DOCS.containsKey( document.getClass() ) ){
				ANNOTATED_DOCS.put( document.getClass(), generate( document.getClass() ) );
			}
			
			return ANNOTATED_DOCS.get( document.getClass() );
		}
		throw new CouchDocumentException( "This object is not a Document for CouchDB" );
	}
		
}
