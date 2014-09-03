package com.ftech.couchdb.models;

public class ViewParameters {
	
	private String  key;
	private String  keys[];
	private String  startKey;
	private String  endKey;
	private String  skip;
	private Integer count;
	private Integer groupLevel;
	private Boolean update;
	private Boolean reverse;
	private Boolean group;
	
	public String getKey() {
		return prepareData( key );
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String[] getKeys() {
		return keys;
	}
	
	public void setKeys(String ... keys) {
		this.keys = keys;
	}
	
	public String getStartKey() {
		return prepareData( startKey );
	}
	
	public void setStartKey(String startKey) {
		this.startKey = startKey;
	}
	
	public String getEndKey() {
		return prepareData( endKey );
	}
	
	public void setEndKey(String endKey) {
		this.endKey = endKey;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public Boolean getUpdate() {
		return update;
	}
	
	public void setUpdate(Boolean update) {
		this.update = update;
	}
	
	public Boolean getReverse() {
		return reverse;
	}
	
	public void setReverse(Boolean reverse) {
		this.reverse = reverse;
	}
	
	public String getSkip() {
		return prepareData( skip );
	}
	
	public void setSkip(String skip) {
		this.skip = skip;
	}
	
	public Boolean getGroup() {
		return group;
	}
	
	public void setGroup(Boolean group) {
		this.group = group;
	}
	
	public Integer getGroupLevel() {
		return groupLevel;
	}
	
	public void setGroupLevel(Integer groupLevel) {
		this.groupLevel = groupLevel;
	}
	
	private String prepareData( String data ){
		if( data == null )
			return null;
		if( data.startsWith("[") && data.endsWith( "]" ) )
			return data;
		if( data.startsWith("\"") && data.endsWith( "\"" ) )
			return data;
		return "\"" + data + "\"";
	}
	
	public void setComposedKey( String ... values ) {
		if( values != null && values.length > 0 ){
			StringBuilder builder = new StringBuilder( "[" );
			for( String xval: values ){
				if( xval == null || xval.isEmpty() ) continue;
				if( builder.length() > 1 ) builder.append( "," );
				builder.append( prepareData( xval ) );
			}
			builder.append( "]" );
			setKey( builder.toString() );
		}
		else {
			setKey( null );
		}
	}
	
	public String getPreparedKeys() {
		if( keys != null && keys.length > 0 ){
			StringBuilder builder = new StringBuilder( "[" );
			for( String xkey: keys ){
				if( xkey == null || xkey.isEmpty() ) continue;
				if( builder.length() > 1 ) builder.append( "," );
				builder.append( prepareData( xkey ) );
			}
			builder.append( "]" );
			return builder.toString();
		}
		return null;
	}
	
	
	
	public ViewParameters key(String  key){
		this.key = key;
		return this;
	}
	
	public ViewParameters keys(String  keys[]){
		this.keys = keys;
		return this;
	}
	
	public ViewParameters startKey(String  startKey){
		this.startKey = startKey;
		return this;
	}
	
	public ViewParameters endKey(String  endKey){
		this.endKey = endKey;
		return this;
	}
	
	public ViewParameters skip(String  skip){
		this.skip = skip;
		return this;
	}
	
	public ViewParameters count(Integer count){
		this.count = count;
		return this;
	}
	
	public ViewParameters groupLevel(Integer groupLevel){
		this.groupLevel = groupLevel;
		return this;
	}
	
	public ViewParameters update(Boolean update){
		this.update = update;
		return this;
	}
	
	public ViewParameters reverse(Boolean reverse){
		this.reverse = reverse;
		return this;
	}
	
	public ViewParameters group(Boolean group){
		this.group = group;
		return this;
	}
	
}
