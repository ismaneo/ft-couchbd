package com.ftech.commons.dao;

import net.sf.json.JSONObject;

public interface CouchdbMapper<T> {
	
	public T mapRow( String id, Object key, JSONObject value );
	
}
