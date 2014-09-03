package com.ftech.commons.dao;

import com.ftech.couchdb.CouchdbTemplate;

public abstract class CouchdbDAO {
	
	private CouchdbTemplate template;
	
	public CouchdbDAO(){}
	
	public CouchdbDAO( CouchdbFactory factory ){
		template = factory.getTemplate();
	}
	
	public CouchdbTemplate getTemplate() {
		return template;
	}
	
	public void setTemplate(CouchdbTemplate template) {
		this.template = template;
	}
	
}
