package com.ftech.couchdb.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ftech.couchdb.exception.CouchException;
import com.ftech.couchdb.exception.CouchDocumentException;

public final class JsonHelper {
	
	/* Format date pattern for JSON [yyyy-MM-dd HH:mm:ss.SSS] */
	private static final DateFormat formatter = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	/**
	 * Can not generate Instances.
	 */
	private JsonHelper(){}
	
	
	/**
	 * Parse a String into a Date in format [yyyy-MM-dd HH:mm:ss.SSS] for JSON
	 * 
	 * @param dateStr
	 * @return
	 */
	private static Date parse( String dateStr ) {
		if( dateStr != null && ! dateStr.isEmpty() ){
			try {
				return formatter.parse( dateStr );
			} 
			catch (ParseException e) {
				throw new CouchException( e );
			}
		}
		return null;
	}
	
	/**
	 * Parse a Date into a String in format [yyyy-MM-dd HH:mm:ss.SSS] for JSON
	 * 
	 * @param value
	 * @return
	 */
	private static String format( Object value ) {
		if( value != null ){
			return formatter.format( value );
		}
		return null;
	}
	
	
	/**
	 * Get the object parameter.
	 * 
	 * @param object
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static Object get( JSONObject object, String key, Class<?> clazz ) {
		if( clazz.equals( Boolean.class ) || clazz.equals( boolean.class ) ){
			return object.getBoolean( key );
		}
		if( clazz.equals( Byte.class ) || clazz.equals( byte.class ) ){
			return (byte) object.getInt( key );
		}
		if( clazz.equals( Short.class ) || clazz.equals( short.class ) ){
			return (short) object.getInt( key );
		}
		if( clazz.equals( Integer.class ) || clazz.equals( int.class ) ){
			return object.getInt( key );
		}
		if( clazz.equals( Long.class ) || clazz.equals( long.class ) ){
			return object.getLong( key );
		}
		if( clazz.equals( Float.class ) || clazz.equals( float.class ) ){
			return (float) object.getDouble( key );
		}
		if( clazz.equals( Double.class ) || clazz.equals( double.class ) ){
			return object.getDouble( key );
		}
		if( clazz.equals( String.class ) ){
			return object.getString( key );
		}
		if( clazz.equals( Date.class ) ){
			return parse( object.getString( key ) );
		}
		if( clazz.equals( BigInteger.class ) ){
			return new BigInteger( object.getString( key ) );
		}
		if( clazz.equals( BigDecimal.class ) ){
			return new BigDecimal( object.getString( key ) );
		}
		if( clazz.isEnum() ){
			String ename = object.getString( key );
			for( Object e: clazz.getEnumConstants() ){
				if( e.toString().equals( ename ) ){
					return e;
				}
			}
		}
		throw new CouchDocumentException( "The class " + 
				clazz.getCanonicalName() + " is not recognized value." );
	}
	
	
	/**
	 * Set the value in the JSON object.
	 * 
	 * @param object
	 * @param key
	 * @param value
	 */
	public static void set( JSONObject object, String key, Object value ) {
		if( value instanceof Boolean || value instanceof Byte || value instanceof Short ||
				value instanceof Integer || value instanceof Long || value instanceof Float ||
				value instanceof Double || value instanceof String || value instanceof Short ){
			object.put( key, value );
			return;
		}
		if( value instanceof BigInteger || value instanceof BigDecimal ||
				value instanceof Enum ){
			object.put( key, value.toString() );
			return;
		}
		if( value instanceof Date ){
			object.put( key, format( value ) );
			return;
		}
		throw new CouchDocumentException( "The class " + 
				value.getClass().getCanonicalName() + " is not recognized value." );
	}
	
	
	/**
	 * Get the object value in the specified index of JSON Array.
	 * 
	 * @param arr as JSONArray
	 * @param index
	 * @param clazz
	 * @return
	 */
	public static Object get( JSONArray arr, int index, Class<?> clazz ) {
		if( clazz.equals( Boolean.class ) || clazz.equals( boolean.class ) ){
			return arr.getBoolean( index );
		}
		if( clazz.equals( Byte.class ) || clazz.equals( byte.class ) ){
			return (byte) arr.getInt( index );
		}
		if( clazz.equals( Short.class ) || clazz.equals( short.class ) ){
			return (short) arr.getInt( index );
		}
		if( clazz.equals( Integer.class ) || clazz.equals( int.class ) ){
			return arr.getInt( index );
		}
		if( clazz.equals( Long.class ) || clazz.equals( long.class ) ){
			return arr.getLong( index );
		}
		if( clazz.equals( Float.class ) || clazz.equals( float.class ) ){
			return (float) arr.getDouble( index );
		}
		if( clazz.equals( Double.class ) || clazz.equals( double.class ) ){
			return arr.getDouble( index );
		}
		if( clazz.equals( String.class ) ){
			return arr.getString( index );
		}
		if( clazz.equals( Date.class ) ){
			return parse( arr.getString( index ) );
		}
		if( clazz.equals( BigInteger.class ) ){
			return new BigInteger( arr.getString( index ) );
		}
		if( clazz.equals( BigDecimal.class ) ){
			return new BigDecimal( arr.getString( index ) );
		}
		if( clazz.isEnum() ){
			String ename = arr.getString( index );
			for( Object e: clazz.getEnumConstants() ){
				if( e.toString().equals( ename ) ){
					return e;
				}
			}
		}
		throw new CouchDocumentException( "The class " + 
				clazz.getCanonicalName() + " is not recognized value." );
	}
	/**
	 * Add the value in the JSONArray.
	 * 
	 * @param arr as JSONArray
	 * @param value
	 */
	public static void set( JSONArray arr, Object value ) {
		if( value instanceof Boolean || value instanceof Byte || value instanceof Short ||
				value instanceof Integer || value instanceof Long || value instanceof Float ||
				value instanceof Double || value instanceof String || value instanceof Short ){
			arr.add( value );
			return;
		}
		if( value instanceof BigInteger || value instanceof BigDecimal ||
				value instanceof Enum ){
			arr.add( value.toString() );
			return;
		}
		if( value instanceof Date ){
			arr.add( format( value ) );
			return;
		}
		throw new CouchDocumentException( "The class " + 
				value.getClass().getCanonicalName() + " is not recognized value." );
	}
	
}
