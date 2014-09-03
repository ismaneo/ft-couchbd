package com.ftech.commons.model;

public class EmptyDocument {
	
	private String id;
	private String revision;
	
	public EmptyDocument(){}
	
	public EmptyDocument( String id, String revision ){
		this.id = id;
		this.revision = revision;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRevision() {
		return revision;
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
}
