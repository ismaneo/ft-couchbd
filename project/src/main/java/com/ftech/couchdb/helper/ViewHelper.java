package com.ftech.couchdb.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ftech.couchdb.CouchdbConstants;
import com.ftech.couchdb.exception.CouchException;
import com.ftech.couchdb.models.ViewParameters;

public final class ViewHelper implements CouchdbConstants {
	
	/**
	 * Can not generate Instances.
	 */
	private ViewHelper(){}
	

	/**
	 * Create the QueryString associated with the View Parameters.
	 * 
	 * @param param
	 * @param charset
	 * @return
	 */
	protected static String generateQueryString(ViewParameters param, String charset) {
		if( param == null ){
			return null;
		}
		
		StringBuilder queryString = new StringBuilder();
		if ( param.getKey() != null && ! param.getKey().isEmpty() ){
			addParam( queryString, PARAM_KEY, param.getKey(), charset );
		}
		if ( param.getKeys() != null && param.getKeys().length > 0 ){
			addParam( queryString, PARAM_KEYS, param.getPreparedKeys(), charset );
		}
		if ( param.getStartKey() != null && ! param.getStartKey().isEmpty() ){
			addParam( queryString, PARAM_STARTKEY, param.getStartKey(), charset );
		}
		if ( param.getEndKey() != null && ! param.getEndKey().isEmpty() ){
			addParam( queryString, PARAM_ENDKEY, param.getEndKey(), charset );
		}
		if ( param.getSkip() != null && ! param.getSkip().isEmpty() ){
			addParam( queryString, PARAM_SKIP, param.getSkip(), charset );
		}
		if ( param.getCount() != null ){
			addParam( queryString, PARAM_COUNT, param.getCount() );
		}
		if ( param.getGroupLevel() != null ){
			addParam( queryString, PARAM_GPLEVEL, param.getGroupLevel() );
		}
		if ( param.getUpdate() != null && param.getUpdate().booleanValue() ){
			addParam( queryString, PARAM_UPDATE );
		}
		if ( param.getReverse() != null && param.getReverse().booleanValue() ){
			addParam( queryString, PARAM_REVERSE );
		}
		if ( param.getGroup() != null && param.getGroup().booleanValue() ){
			addParam( queryString, PARAM_GROUP );
		}
		return queryString.length() == 0 ? null : queryString.toString();
	}
	
	
	/**
	 * Add an Empty Parameter to QueryString.
	 * 
	 * @param queryString
	 * @param param
	 */
	private static void addParam( StringBuilder queryString, String param ) {
		if( queryString.length() > 0 ){
			queryString.append( "&" );
		}
		queryString.append( param );
	}
	
	
	/**
	 * Add an Integer parameter to QueryString.
	 * 
	 * @param queryString
	 * @param param
	 * @param value
	 */
	private static void addParam( StringBuilder queryString, String param, Integer value ) {
		// Add parameter
		if( value != null ){
			if( queryString.length() > 0 ){
				queryString.append( "&" );
			}
			queryString.append( param ).append( value );
		}
	}
	
	/**
	 * Add an String parameter to QueryString.
	 * 
	 * @param queryString
	 * @param param
	 * @param value
	 * @param charset
	 */
	private static void addParam( StringBuilder queryString, 
			String param, String value, String charset ) {
		// Add string parameter
		if( value != null ){
			if( queryString.length() > 0 ){
				queryString.append( "&" );
			}
			queryString.append( param );
			queryString.append( encode( value, charset ) );
		}
	}
	
	
	/**
	 * Encode the string value in specified charset.
	 * 
	 * @param value
	 * @param charset
	 * @return
	 */
	private static String encode( String value, String charset ){
		try {
			return URLEncoder.encode( value, getCharSet( charset ) );
		} 
		catch (UnsupportedEncodingException e) {
			throw new CouchException( e );
		}
	}
	
	
	/**
	 * Obtain standard charset.
	 * 
	 * @param charset
	 * @return
	 */
	protected static String getCharSet( String charset ){
		if( charset != null ){
			return charset;
		}
		return "UTF-8";
	}
	
}
