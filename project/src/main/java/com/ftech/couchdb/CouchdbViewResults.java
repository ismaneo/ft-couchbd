package com.ftech.couchdb;

import java.util.ArrayList;
import java.util.List;

import com.ftech.commons.dao.CouchdbMapper;
import com.ftech.couchdb.helper.CouchdbJsonHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public final class CouchdbViewResults implements CouchdbConstants {
	
	private JSONObject response;
	
	protected CouchdbViewResults( JSONObject response ){
		this.response = response;
	}
	
	public List<JSONObject> getResults() {
		List<JSONObject> results = new ArrayList<JSONObject>();
		JSONArray array = response.getJSONArray("rows");
		//value
		
		for ( int i = 0 ; i < array.size(); i++ ) {
			if( array.get(i) != null && ! array.getString( i ).equals("null") ) {
				results.add( array.getJSONObject(i) );
			}
		}
		return results;
	}
	
	public <T> List<T> getResults( CouchdbMapper<T> mapper ) {
		List<T> results = new ArrayList<T>();
		List<JSONObject> rows = getResults();
		
		for( JSONObject row: rows ) {
			results.add( mapper.mapRow( row.getString( JSON_RESP_ID ),
					row.get( JSON_RESP_KEY ), row.getJSONObject( JSON_RESP_VAL ) ) );
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getResults( Class<T> clazz ) {
		List<T> results = new ArrayList<T>();
		List<JSONObject> rows = getResults();
		
		for( JSONObject row: rows ) {
			results.add( (T)CouchdbJsonHelper.fillDocument( 
					clazz, row.getJSONObject( JSON_RESP_VAL ) ) );
		}
		return results;
	}
	
}
