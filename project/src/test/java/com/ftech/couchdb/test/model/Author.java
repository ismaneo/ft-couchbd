package com.ftech.couchdb.test.model;

import com.ftech.couchdb.models.DocumentPart;

public class Author implements DocumentPart {
	
	String name;
	String country;
	
	public Author(){}
	
	public Author(String name, String country){
		this.name = name;
		this.country = country;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
}
