package com.ftech.commons.dao;

import net.sf.json.JSONObject;

import com.ftech.commons.model.EmptyDocument;
import com.ftech.couchdb.CouchdbConstants;

public class EmptyDocumentMapper 
implements CouchdbMapper<EmptyDocument>, CouchdbConstants {

	@Override
	public EmptyDocument mapRow(String id, Object key, JSONObject value) {
		return new EmptyDocument( id, value.getString( JSON_RESP_REV ) );
	}

}
