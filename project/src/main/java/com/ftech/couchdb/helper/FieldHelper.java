package com.ftech.couchdb.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ftech.couchdb.annotations.Id;
import com.ftech.couchdb.annotations.Ignored;
import com.ftech.couchdb.annotations.Join;
import com.ftech.couchdb.annotations.Revision;
import com.ftech.couchdb.exception.CouchDocumentException;

public final class FieldHelper {
	
	/**
	 * Can not generate Instances.
	 */
	private FieldHelper(){}
	
	/**
	 * Check if field is Date Type.
	 * 
	 * @param field
	 * @return
	 */
	protected static boolean isDateField( Field field ){
		return field.getType().equals( java.util.Date.class ) || 
				field.getType().equals( java.sql.Date.class );
	}
	
	/**
	 * Check if field is a basic type of: Boolean, String, Character,
	 * Byte, Short, Integer, Long, Float, or Double
	 * 
	 * @param value
	 * @return
	 */
	protected static boolean isBasicField( Field field ){
		return isBasicType( field.getType() ); 
	}
	
	protected static boolean isBasicType( Class<?> clazz ){
		return clazz.equals( String.class ) || clazz.equals( Date.class ) ||
				clazz.equals( Character.class ) || clazz.equals( char.class ) ||
				clazz.equals( Boolean.class ) || clazz.equals( boolean.class ) ||
				clazz.equals( Byte.class ) || clazz.equals( byte.class ) ||
				clazz.equals( Short.class ) || clazz.equals( short.class ) ||
				clazz.equals( Integer.class ) || clazz.equals( int.class ) ||
				clazz.equals( Long.class ) || clazz.equals( long.class ) ||
				clazz.equals( Float.class ) || clazz.equals( float.class ) ||
				clazz.equals( Double.class ) || clazz.equals( double.class ) ||
				clazz.equals( BigInteger.class ) || clazz.equals( BigDecimal.class ) 
				|| clazz.isEnum();
	}
	
	
	/**
	 * Check if field is a array type.
	 * 
	 * @param value
	 * @return
	 */
	protected static boolean isArrayField( Field field ){
		if( field.getType().equals( Set.class ) || field.getType().equals( List.class ) ){
			return true;
		}
		return field.getType().isArray();
	}
	
	
	/**
	 * Check if field is a array type.
	 * 
	 * @param value
	 * @return
	 */
	protected static Class<?> getArrayType( Field field ){
		//For list and set types.
		if( field.getType().equals( Set.class ) || field.getType().equals( List.class ) ){
			
			if( !(field.getGenericType() instanceof ParameterizedType) ){
				throw new CouchDocumentException( "Field " + field.getName() +
						" is not a generic type." );
			}
			
			ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
			if( fieldType.getActualTypeArguments() == null || 
					fieldType.getActualTypeArguments().length == 0 ){
				throw new CouchDocumentException( "Field " + field.getName() +
						" is not a generic type." );
			}
			
			for( Type ta: fieldType.getActualTypeArguments() ){
				if( ta instanceof Class ){
					return (Class<?>) ta;
				}
			}
			throw new CouchDocumentException( "Field " + field.getName() +
					" is not a generic type." );
		}
		
		if( field.getType().isArray() ){
			return field.getType().getComponentType();
		}
		throw new CouchDocumentException( "Field type is not recognized [" 
				+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
	}
	
	
	protected static Iterator<?> getArrayIterator( Field field, final Object value ){
		if( field.getType().equals( Set.class ) ){
			return ((Set<?>) value).iterator(); 
		} 
		
		if( field.getType().equals( List.class ) ){
			return ((List<?>) value).iterator(); 
		} 
		
		if( field.getType().isArray() ){
			return new Iterator<Object>() {
				private int index = 0;
				
				public boolean hasNext() {
					return Array.getLength( value ) > index;
				}
				
				public Object next() {
					return Array.get( value, index++ );
				}
				
				public void remove() {
				}
			};
		}
		throw new CouchDocumentException( "Field type is not recognized as array. [" 
				+ field.getName() + "," + field.getType().getCanonicalName() + "]" );
	}
	
	/**
	 * Get the value in the specified "field" of the "object" 
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	protected static Object get( Field field, Object object ){
		try {
			return field.get( object );
		} 
		catch (IllegalArgumentException e) {
			throw new CouchDocumentException( e );
		} 
		catch (IllegalAccessException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	/**
	 * Set the "value" in the specified "field" of the "object" 
	 * 
	 * @param field
	 * @param object
	 * @param value
	 * @return
	 */
	protected static void set( Field field, Object object, Object value ){
		try {
			field.set( object, value );
		} 
		catch (IllegalArgumentException e) {
			throw new CouchDocumentException( e );
		} 
		catch (IllegalAccessException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	/**
	 * Return an object of the type of field.
	 * 
	 * @param field
	 * @return
	 */
	protected static Object getInstance(Field field) {
		return getInstance( field.getType() );
	}
	
	/**
	 * Return an object of the class.
	 * 
	 * @param clazz
	 * @return
	 */
	protected static Object getInstance( Class<?> clazz ) {
		if( clazz == null ){
			throw new CouchDocumentException( "Class of document can not be null." );
		}
		try {
			return clazz.newInstance();
		} 
		catch (InstantiationException e) {
			throw new CouchDocumentException( e );
		} 
		catch (IllegalAccessException e) {
			throw new CouchDocumentException( e );
		}
	}
	
	/**
	 * Return an array of the field class.
	 * 
	 * @param field
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	protected static Object getInstance( Field field, int length ) {
		if( field.getType().isArray() ){
			Class<?> clazz = getArrayType( field );
			return Array.newInstance( clazz, length );
		}
		if( field.getType().equals( List.class ) ){
			return new ArrayList();
		}
		if( field.getType().equals( Set.class ) ){
			return new HashSet();
		}
		throw new CouchDocumentException( "Class is not of array type." );
	}
	
	
	/**
	 * Add the value in the array.
	 * 
	 * @param arr
	 * @param value
	 * @param index
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void add( Object arr, int index, Object value ) {
		if( arr.getClass().isArray() ){
			Array.set( arr, index, value );
			return;
		}
		if( arr instanceof List ){
			((List) arr).add( value );
		}
		if( arr instanceof Set ){
			((Set) arr).add( value );
		}
	}
	
	
	/**
	 * Check if field is annotated with @Id and if it is String type.
	 * 
	 * @param field
	 * @return
	 */
	protected static boolean isIdField( Field field ){
		Annotation a = field.getAnnotation( Id.class );
		if( a instanceof Id ){ 
			if( ! field.getType().equals( String.class ) )
				throw new CouchDocumentException( "Id field must be of String type." );
			return true;
		}
		return false;
	}
	
	/**
	 * Check if field is annotated with @Revision and if it is String type.
	 * 
	 * @param field
	 * @return
	 */
	protected static boolean isRevisionField( Field field ){
		Annotation a = field.getAnnotation( Revision.class );
		if( a instanceof Revision ){ 
			if( ! field.getType().equals( String.class ) )
				throw new CouchDocumentException( "Revision field must be of String type." );
			return true;
		}
		return false;
	}
	
	/**
	 * Check if field is annotated with @Ignored.
	 * 
	 * @param field
	 * @return
	 */
	protected static boolean isIgnoredField( Field field ){
		Annotation a = field.getAnnotation( Ignored.class );
		return ( a instanceof Ignored ); 
	}
	
	/**
	 * Check if field is annotated with @Ignored.
	 * 
	 * @param field
	 * @return
	 */
	protected static boolean isJoinField( Field field ){
		Annotation a = field.getAnnotation( Join.class );
		return ( a instanceof Join ); 
	}
	
	/**
	 * Obtain all Join Fields from field class.
	 * 
	 * @param field
	 * @return
	 */
	protected static Field[] getJoinFields( Field field ){
		return getJoinFields( field, field.getType() );
	}
	
	
	/**
	 * Obtain all Join Fields from class.
	 * 
	 * @param field
	 * @return
	 */
	protected static Field[] getJoinFields( Field field, Class<?> clazz ){
		Annotation a = field.getAnnotation( Join.class );
		
		if( a instanceof Join ){
			String names[] = ((Join) a).fields();
			if( names == null || names.length == 0 ){
				throw new CouchDocumentException( "The field " + field.getName() + " does not have join fields." );
			}
			
			Field[] declaredfields = clazz.getDeclaredFields();
			Field[] joinfields = new Field[ names.length ];
			
			for( int i=0; i < names.length; i++ ){
				String name = names[ i ];
				boolean found = false;
				
				for( Field f: declaredfields ){
					if( f.getName().equals( name ) ){
						joinfields[ i ] = f;
						found = true;
						break;
					}
				}
				
				if( !found ){
					throw new CouchDocumentException( 
							"The class " + clazz.getCanonicalName() + 
							" does not have " + name + " property." );
				}
			}
			
			return joinfields;
		}
		throw new CouchDocumentException( "The field " + field.getName() + " is not an Join field." );
	}
	
}
