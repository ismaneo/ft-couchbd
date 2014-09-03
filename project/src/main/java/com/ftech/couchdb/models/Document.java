package com.ftech.couchdb.models;


public abstract class Document {
	
	private String id;
	private String revision;
	
	protected Document(){}
	
	public final String getId() {
		return id;
	}
	
	public final void setId(String id) {
		this.id = id;
	}
	
	public final String getRevision() {
		return revision;
	}
	
	public final void setRevision(String revision) {
		this.revision = revision;
	}
	
	public String getDocType() {
		return getClass().getSimpleName();
	}
	
}
