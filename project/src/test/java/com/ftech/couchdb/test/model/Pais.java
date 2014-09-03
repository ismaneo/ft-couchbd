package com.ftech.couchdb.test.model;


import org.apache.log4j.Logger;

import com.ftech.couchdb.annotations.CouchDocument;
import com.ftech.couchdb.annotations.Id;
import com.ftech.couchdb.annotations.Ignored;
import com.ftech.couchdb.annotations.Join;
import com.ftech.couchdb.annotations.Revision;

@CouchDocument
public class Pais {
	
	@Ignored
	private Logger log = Logger.getLogger( Pais.class );
	
	@Id
	private String  id;
	@Revision
	private String  rev;
	private String  clave;
	private String  nombre;
	
	@Join(fields={"clave","nombre"})
	private Pais[]  vecinos;
	
	public Pais(){}

	public Pais( String clave, String nombre ){
		this.clave = clave;
		this.nombre = nombre;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Pais[] getVecinos() {
		return vecinos;
	}

	public void setVecinos(Pais[] vecinos) {
		this.vecinos = vecinos;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
	
}
