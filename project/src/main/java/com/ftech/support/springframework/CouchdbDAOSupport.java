package com.ftech.support.springframework;

import org.springframework.beans.factory.annotation.Autowired;

import com.ftech.couchdb.CouchdbTemplate;

public class CouchdbDAOSupport {
	
	@Autowired
	private CouchdbTemplate template;
	
	public CouchdbTemplate getTemplate() {
		return template;
	}
	
	public void setTemplate(CouchdbTemplate template) {
		this.template = template;
	}
	
}
