package com.ftech.couchdb.test.model;

import com.ftech.couchdb.models.Document;

public class Book extends Document {
	
	String title;
	String editorial;
	Author author;
	String[] anios;
	
	public Book(){}
	
	public Book( String id, String revision ){
		setId(id);
		setRevision(revision);
	}
	
	public Book( String title, String editorial, Author author ){
		this.title = title;
		this.editorial = editorial;
		this.author = author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getEditorial() {
		return editorial;
	}
	
	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}
	
	public Author getAuthor() {
		return author;
	}
	
	public void setAuthor(Author author) {
		this.author = author;
	}
	
	public String[] getAnios() {
		return anios;
	}
	
	public void setAnios(String[] anios) {
		this.anios = anios;
	}
	
}
