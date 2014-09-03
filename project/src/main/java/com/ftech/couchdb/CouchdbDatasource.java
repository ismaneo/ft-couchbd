package com.ftech.couchdb;

public final class CouchdbDatasource {
	
	private String  baseUrl;
	private String  hostname;
	private String  port;
	private String  database;
	private String  user;
	private String  password;
	private boolean useSSL;
	private boolean createByDefault;
	private boolean forceRegister;
	
	public CouchdbDatasource(){}
	
	public CouchdbDatasource( String hostname, String port, String database ){
		this();
		this.hostname = hostname;
		this.port = port;
		this.database = database;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getDatabase() {
		return database;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isUseSSL() {
		return useSSL;
	}
	
	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}
	
	public boolean isCreateByDefault() {
		return createByDefault;
	}
	
	public void setCreateByDefault(boolean createByDefault) {
		this.createByDefault = createByDefault;
	}
	
	public boolean isForceRegister() {
		return forceRegister;
	}

	public void setForceRegister(boolean forceRegister) {
		this.forceRegister = forceRegister;
	}

	protected String getBaseUrl() {
		if( baseUrl == null ){
			baseUrl = ((useSSL) ? "https://" : "http://") + hostname + ":" + port;
		}
		return baseUrl;
	}
	
	protected CouchdbConnection getConnection(){
		return new CouchdbConnection( this );
	}
	
}
