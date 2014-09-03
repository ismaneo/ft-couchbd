package com.ftech.couchdb.test.model;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ftech.couchdb.annotations.CouchDocument;
import com.ftech.couchdb.annotations.Id;
import com.ftech.couchdb.annotations.Ignored;
import com.ftech.couchdb.annotations.Join;
import com.ftech.couchdb.annotations.Revision;

@CouchDocument
public class UserDoc {
	
	@Ignored
	private Logger log = Logger.getLogger( UserDoc.class );
	
	@Id
	private String id;
	@Revision
	private String  revision;
	private String  username;
	private String  password;
	private String  email;
	private Date    fechaCreacion;
	private boolean habilitado;
	@Join(fields={"clave"})
	private Pais pais;
	
	public UserDoc(){}

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}
	
	public Pais getPais() {
		return pais;
	}
	
	public void setPais(Pais pais) {
		this.pais = pais;
	}
	
}
