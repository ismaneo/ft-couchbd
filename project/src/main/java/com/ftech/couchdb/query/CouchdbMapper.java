package com.ftech.couchdb.query;

import net.sf.json.JSONObject;

public interface CouchdbMapper<T> {
	
	public T mapRow( String id, Object key, JSONObject value );
	
}
