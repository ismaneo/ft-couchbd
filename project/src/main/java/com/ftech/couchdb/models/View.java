package com.ftech.couchdb.models;

import com.ftech.couchdb.CouchdbConstants;

public class View {
	
	private Integer version;
	private String  name;
	private String  function;
	private String  reduce;
	
	public View(){
		this.version = 0;
	}
	
	public View(String name) {
		this.version = 0;
		this.name = name;
	}
	
	public View(String name, int version) {
		this.version = version;
		this.name = name;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFunction() {
		return function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	public String getReduce() {
		return reduce;
	}
	
	public void setReduce(String reduce) {
		this.reduce = reduce;
	}
	
	public String getViewId(){
		return CouchdbConstants.DESIGN_PATH + name;
	}
	
	public String getViewPath(){
		return getViewId() + "/" + CouchdbConstants.VIEWS_PATH + name;
	}
	
}
