package com.ftech.couchdb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.ftech.couchdb.exception.CouchException;

public final class CouchdbConnection implements CouchdbConstants {
	
	private CouchdbDatasource datasource;
	private HttpClient httpClient;
	
	protected CouchdbConnection( CouchdbDatasource datasource ) {
		this.datasource = datasource;
		this.httpClient = new HttpClient();
		
		if( ! isValidDatabaseName( datasource.getDatabase() ) ){
			throw new CouchException( "Invalid name for database :: " + datasource.getDatabase() );
		}
		
		if( datasource.getUser() != null && datasource.getPassword() != null ){
			Credentials defaultcreds = new UsernamePasswordCredentials( 
					datasource.getUser(), datasource.getPassword() );
			httpClient.getState().setCredentials( AuthScope.ANY, defaultcreds );
		}
		
		if( ! getDatabaseNames().contains( datasource.getDatabase() ) &&
				datasource.isCreateByDefault() ){
			CouchdbResponse resp = put( datasource.getDatabase() );
			if( ! resp.isOk() ){
				throw new CouchException( resp.getErrorReason() );
			}
		}
	}
	
	private boolean isValidDatabaseName(String database) {
		if( database == null ) return false;
		if( ! database.toLowerCase().equals( database ) )
			return false;
		return database.matches( "^[a-z0-9_$()+\\-/]+$" );
	}
	
	public List<String> getDatabaseNames() {
		CouchdbResponse resp = get( ALL_DATABASES );
		JSONArray array = resp.getBodyAsJSONArray();
		
		List<String> dbs = new ArrayList<String>( array.size() );
		for ( int i=0 ; i< array.size(); i++ ) {
			dbs.add( array.getString( i ) );
		}
		return dbs;
	}
	
	private String buildUrl( String url ) {
		return datasource.getBaseUrl() + "/" + url;
	}
	
	protected CouchdbResponse delete( String url ) {
		DeleteMethod del = new DeleteMethod( buildUrl( url ) );
		return http(del);
	}
	
	protected CouchdbResponse post( String url ) {
		return post(url, null, null);
	}
	
	protected CouchdbResponse post( String url, String content ) {
		return post(url, content, null);
	}
	
	protected CouchdbResponse post( String url, String content, String queryString ) {
		PostMethod post = new PostMethod( buildUrl( url ) );
		if ( content != null ) {
			RequestEntity entity;
			try {
				entity = new StringRequestEntity(content, "application/json", "UTF-8");
				post.setRequestEntity( entity );
			} catch (UnsupportedEncodingException e) {
				throw new CouchException( e );
			}
		}
		if (queryString != null) {
			post.setQueryString( queryString );
		}
		return http( post );
	}
	
	protected CouchdbResponse put( String url ) {
		return put( url, null );
	}
	
	protected CouchdbResponse put( String url, String content ) {
		PutMethod put = new PutMethod( buildUrl( url ));
		if ( content != null ) {
			RequestEntity entity;
			try {
				entity = new StringRequestEntity(content, "application/json", "UTF-8");
				put.setRequestEntity( entity );
			} catch (UnsupportedEncodingException e) {
				throw new CouchException( e );
			}
		}
		return http( put );
	}
	
	protected CouchdbResponse get( String url ) {
		GetMethod get = new GetMethod( buildUrl( url ) );
		return http( get );
	}
	
	protected CouchdbResponse get( String url, NameValuePair[] queryParams ) {
		GetMethod get = new GetMethod( buildUrl( url ) );
		get.setQueryString( queryParams );
		return http( get );
	}
	
	protected CouchdbResponse get( String url, String queryString ) {
		GetMethod get = new GetMethod( buildUrl( url ) );
		get.setQueryString( queryString );
		return http( get );
	}
	
	private CouchdbResponse http( HttpMethod method ) {
		System.out.println( method.getPath() );
		try {
			httpClient.executeMethod( method );
			return new CouchdbResponse( method );
		} catch (HttpException e) {
			throw new CouchException( e );
		} catch (IOException e) {
			throw new CouchException( e );
		} finally {
			method.releaseConnection();
		}
	}
	
}
