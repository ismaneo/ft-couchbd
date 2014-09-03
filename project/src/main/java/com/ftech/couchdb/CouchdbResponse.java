package com.ftech.couchdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CouchdbResponse {

	private static Log log = LogFactory.getLog(CouchdbResponse.class);
	
	private static boolean inError( int statusCode ){
		return statusCode==400 || statusCode==404 || 
				statusCode==409 || statusCode==412;
	} 
	
	private static boolean isBadRequest( String methodName, int statusCode ){
		return (methodName.equals("GET") && inError(statusCode)) || 
				(methodName.equals("PUT") && inError(statusCode)) ||
				(methodName.equals("PUT") && inError(statusCode)) ||
				(methodName.equals("POST") && inError(statusCode)) ||
				(methodName.equals("DELETE") && inError(statusCode));
	}
	
	private static boolean isXRequest( String methodName, int statusCode ){
		return (methodName.equals("PUT") && statusCode==201) ||
				(methodName.equals("POST") && statusCode==201) ||
				(methodName.equals("DELETE") && statusCode==200); 
	}
	
	private static boolean isHttpRequest( String methodName, int statusCode ){
		return (methodName.equals("GET") || methodName.equals("POST")) 
				&& statusCode==200;
	}
	
	private String        methodName;
	private Header[]      headers;
	private int           statusCode;
	private StringBuilder body = new StringBuilder();
	private boolean       ok = false;
	private JSONObject    error;
	
	
	public CouchdbResponse(HttpMethod method) throws IOException {
		this.methodName = method.getName();
		this.headers = method.getResponseHeaders();
		this.statusCode = method.getStatusCode();
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( 
				method.getResponseBodyAsStream() ) );
		String line = null;
		
		while ( ( line = reader.readLine() ) != null ) {
			if ( body.length() > 0 ) {
				body.append( "\n" );
			}
			body.append( line );
		}
		
		log.info( "["+method.getName()+"] ["+method.getPath()+"] "+
				((method.getQueryString()!=null) ?"?"+method.getQueryString():"")+
				" ["+method.getStatusCode()+"] "+" => " + body );
		
		if ( isBadRequest( methodName, statusCode ) ){
			//error = JSONObject.fromObject( body.toString() ).getJSONObject("error");
			error = JSONObject.fromObject( body.toString() );
		}
		else if ( isXRequest( methodName, statusCode ) ) {
			ok = JSONObject.fromObject( body.toString() ).getBoolean( "ok" );
		}
		else if ( isHttpRequest( methodName, statusCode ) ) {
			ok = true;
		}
	}
	
	public boolean isOk() {
		return ok;
	}
	
	public String getErrorType() {
		if ( error != null ) {
			return error.getString("error");
		}
		return null;
	}
	
	public String getErrorReason() {
		if ( error != null ) {
			return error.getString("reason");
		}
		return null;
	}
	
	public String getHeader( String key ) {
		for (Header h: headers) {
			if ( h.getName().equals( key ) ) {
				return h.getValue();
			}
		}
		return null;
	}
	
	public int getStatusCode() {
		return this.statusCode;
	}
	
	public JSONObject getBodyAsJSON() {
		if ( body != null ) {
			return JSONObject.fromObject( body.toString() );
		}
		return null;
	}
	
	public JSONArray getBodyAsJSONArray() {		
		if ( body != null ) {
			return JSONArray.fromObject( body.toString() );
		}
		return null;
	}
	
}