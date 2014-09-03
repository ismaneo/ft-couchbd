package com.ftech.couchdb;

public interface CouchdbConstants {

	String JSON_RESP_ID   = "id";
	String JSON_RESP_REV  = "rev";
	String JSON_RESP_KEY  = "key";
	String JSON_RESP_VAL  = "value";
	
	String JSON_DOC_ID    = "_id";
	String JSON_DOC_REV   = "_rev";
	String JSON_DOC_TYPE  = "docType";
	String JSON_DOC_CLASS = "jclass";
	String JSON_VIEW      = "views";
	String JSON_MAP       = "map";
	String JSON_VERSION   = "version";
	String JSON_REDUCE    = "reduce";
	
	String ALL_DATABASES  = "_all_dbs";
	String ALL_DOCUMENTS  = "_all_docs";
	
	String DESIGN_PATH    = "_design/";
	String VIEWS_PATH     = "_view/";
	String TEMP_VIEW_PATH = "_temp_view/";
	
	String PARAM_KEY      = "key=";
	String PARAM_KEYS     = "keys=";
	String PARAM_STARTKEY = "startkey=";
	String PARAM_ENDKEY   = "endkey=";
	String PARAM_SKIP     = "skip=";
	String PARAM_COUNT    = "count=";
	String PARAM_GPLEVEL  = "group_level=";
	String PARAM_UPDATE   = "update=true";
	String PARAM_REVERSE  = "reverse=true";
	String PARAM_GROUP    = "group=true";
	
	String REDUCE_SUM     = "_sum";
	String REDUCE_COUNT   = "_count";
	String REDUCE_STATS   = "_stats";
	
}
