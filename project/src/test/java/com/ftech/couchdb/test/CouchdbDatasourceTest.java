package com.ftech.couchdb.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.ftech.commons.dao.EmptyDocumentMapper;
import com.ftech.commons.model.EmptyDocument;
import com.ftech.couchdb.CouchdbDatasource;
import com.ftech.couchdb.CouchdbTemplate;
import com.ftech.couchdb.models.AdHocView;
import com.ftech.couchdb.models.DeclaredView;
import com.ftech.couchdb.models.View;
import com.ftech.couchdb.models.ViewParameters;
import com.ftech.couchdb.test.model.Author;
import com.ftech.couchdb.test.model.Book;
import com.ftech.couchdb.test.model.Pais;
import com.ftech.couchdb.test.model.TypesDoc;
import com.ftech.couchdb.test.model.UserDoc;

public class CouchdbDatasourceTest {
	
	public static void main(String args[]){
		CouchdbDatasource datasource = new CouchdbDatasource(
				"127.0.0.1", "5984", "totoro"); 
		datasource.setCreateByDefault( true );
		
		CouchdbTemplate template = new CouchdbTemplate();
		template.setDatasource( datasource );
		
		//saveBook( template );
		//loadBook( template );
		//createView( template );
		//queryView( template );
		//queryAdHocView( template );
		//saveUser( template );
		//loadUser( template );
		//queryUser( template );
		//queryAllDocs( template );
		//queryCount( template );
		//queryReduce( template );
		//saveAllTypes( template );
		//loadAllTypes( template );
		//savePais( template );
		loadPais( template );
	}
	
	public static void loadPais(CouchdbTemplate template) {
		Pais ppp = new Pais();
		ppp.setId( "68aaa6da1d7f584a99ca8812e10027c1" );
		template.load( ppp );
		
		System.out.println( "Clave :: " + ppp.getClave() );
		System.out.println( "Nombre :: " + ppp.getNombre() );
		if( ppp.getVecinos() != null ){
			System.out.println( "Vecinos :: " );
			for( Pais vvv: ppp.getVecinos() ){
				System.out.println( "[" + vvv.getClave() + "," + vvv.getNombre() + "] " );
			}
		}
	}
	
	//"id":"68aaa6da1d7f584a99ca8812e1001ea5","rev":"1-8ffdc832521f79b911ad7df6ff63b4ff"
	//"id":"68aaa6da1d7f584a99ca8812e10027c1","rev":"1-d297347e8d1c25b0265b85e52dfe8a3b"
	public static void savePais(CouchdbTemplate template) {
		/*
		Pais ppp = new Pais( "MX", "Mexico" );
		ppp.setVecinos( new Pais[]{ 
				new Pais( "EU", "Estados Unidos" ),
				new Pais( "GT", "Guatemala" ),
				new Pais( "BC", "Belice" )
		} );
		*/
		Pais ppp = new Pais( "EU", "Estados Unidos" );
		ppp.setVecinos( new Pais[]{ 
				new Pais( "CN", "Canada" ),
				new Pais( "MX", "Mexico" )
		} );
		template.persist( ppp );
	}
	
	public static void loadAllTypes(CouchdbTemplate template) {
		TypesDoc doc = new TypesDoc();
		doc.setId( "68aaa6da1d7f584a99ca8812e1000048" );
		template.load( doc );
		System.out.println( "Boolean :: " + doc.xb );
		System.out.println( "Byte :: " + doc.maxb );
		System.out.println( "Byte :: " + doc.minb );
		System.out.println( "Short :: " + doc.maxs );
		System.out.println( "Short :: " + doc.mins );
		System.out.println( "Integer :: " + doc.maxi );
		System.out.println( "Integer :: " + doc.mini );
		System.out.println( "Long :: " + doc.maxl );
		System.out.println( "Long :: " + doc.minl );
		System.out.println( "Float :: " + doc.maxf );
		System.out.println( "Float :: " + doc.minf );
		System.out.println( "Double :: " + doc.maxd );
		System.out.println( "Double :: " + doc.mind );
		System.out.println( "Date :: " + doc.fecha );
		System.out.println( "BigInteger :: " + doc.bi );
		System.out.println( "BigDecimal :: " + doc.bd );
	}

	//"id":"68aaa6da1d7f584a99ca8812e1000048","rev":"1-3750f87662937b94352c0ba17aa11e54"
	//"id":"68aaa6da1d7f584a99ca8812e1000048","rev":"2-3360a0fc4493724003d54e1d00761948"
	public static void saveAllTypes(CouchdbTemplate template) {
		TypesDoc doc = new TypesDoc();
		doc.xb = true;
		doc.maxb = Byte.MAX_VALUE;
		doc.minb = Byte.MIN_VALUE;
		doc.maxs = Short.MAX_VALUE;
		doc.mins = Short.MIN_VALUE;
		doc.maxi = Integer.MAX_VALUE;
		doc.mini = Integer.MIN_VALUE;
		doc.maxl = Long.MAX_VALUE;
		doc.minl = Long.MIN_VALUE;
		doc.maxf = Float.MAX_VALUE;
		doc.minf = Float.MIN_VALUE;
		doc.maxd = Double.MAX_VALUE;
		doc.mind = Double.MIN_VALUE;
		doc.fecha = new Date();
		doc.bi = new BigInteger( "999" + Long.MAX_VALUE );
		doc.bd = new BigDecimal( Double.MAX_VALUE ).add( new BigDecimal( Double.MAX_VALUE ) );
		
		template.persist( doc );
	}

	public static void queryReduce(CouchdbTemplate template) {
		String function = "function(doc) { if (doc.docType) emit(doc.docType, 1); }";
		String reduce = "function(keys, values, rereduce){ return sum( values ); }";
		
		View view = new View( "groupDocs" );
		view.setFunction( function );
		view.setReduce( reduce );
		view.setVersion( 15 );
		template.registerView( view );
		
		template.query( view, new ViewParameters().group( true ) );
	}

	public static void queryCount(CouchdbTemplate template) {
		String function = "function(doc) { if (doc.docType==\'Book\') emit(doc.docType, 1); }";
		AdHocView view = AdHocView.getCount( function );
		template.query( view );
	}

	public static void queryAllDocs(CouchdbTemplate template) {
		DeclaredView view = new DeclaredView( "/_all_docs" );
		List<EmptyDocument> rs = template.query( view, new EmptyDocumentMapper() );
		for( EmptyDocument empty: rs ){
			System.out.println( "EmptyDoc :: " + empty.getId() + " - " + empty.getRevision() );
		}
	}

	public static void queryUser(CouchdbTemplate template) {
		String function = "function(doc) { if (doc.docType == \'UserDoc\') emit(doc.username, doc); }";
		AdHocView view = new AdHocView( function );
		template.query( view, UserDoc.class );
	}

	public static void loadUser(CouchdbTemplate template) {
		UserDoc user = new UserDoc();
		user.setId( "665e3f95424d29efae864d8ebb03bcfa" );
		template.load( user );
		System.out.println( "Username :: " + user.getUsername() );
		System.out.println( "Password :: " + user.getPassword() );
		System.out.println( "Email :: " + user.getEmail() );
		System.out.println( "FechaCreacion :: " + user.getFechaCreacion() );
		System.out.println( "Pais :: " + user.getPais().getClave() );
	}

	//"id":"665e3f95424d29efae864d8ebb03bcfa","rev":"1-911dbca3dac10bdfaa84644ab539df31"
	//"id":"665e3f95424d29efae864d8ebb03bcfa","rev":"2-0bcf66ee606f9adbf51dbcea8a7c2d4a"
	//"id":"665e3f95424d29efae864d8ebb03bcfa","rev":"3-1dd9a90e2483e4de97abfdd32cb1ff06"
	//"id":"665e3f95424d29efae864d8ebb03bcfa","rev":"4-e01ea6c4e31747ac54dcfc362821479d"
	//"id":"665e3f95424d29efae864d8ebb03bcfa","rev":"5-eeff980ee8424df331914068943102b4"
	public static void saveUser(CouchdbTemplate template) {
		UserDoc user = new UserDoc();
		user.setId( "665e3f95424d29efae864d8ebb03bcfa" );
		user.setRevision( "4-e01ea6c4e31747ac54dcfc362821479d" );
		user.setUsername( "pinochito" );
		user.setPassword( "NoMient0" );
		user.setEmail( "pinoccio@disney.com" );
		user.setFechaCreacion( new Date() );
		user.setPais( new Pais( "EU", "Estados Unidos" ) );
		template.persist( user );
	}

	public static void queryAdHocView(CouchdbTemplate template) {
		String function = "function(doc) { if (doc.docType == \'Book\' && doc.author.country == 'MX' ) "
				+ "emit(doc._id, doc); }";
		AdHocView view = new AdHocView( function );
		//template.registerView( view );
		template.query( view, Book.class );
	}

	public static void queryView(CouchdbTemplate template) {
		View view = new View( "myfirstview" );
		template.query( view, Book.class );
	}
	
	public static void createView(CouchdbTemplate template) {
		View view = new View( "myfirstview" );
		view.setFunction( "function(doc) { if (doc.docType == \'Book\') emit(null, doc); }" );
		
		System.out.println( "registerView :: " + template.registerView( view ) );
	}
	
	public static void loadBook(CouchdbTemplate template) {
		Book libro = template.load( new Book( 
				"665e3f95424d29efae864d8ebb03a56b", null ) );
		System.out.println( "title :: " + libro.getTitle() );
		System.out.println( "editorial :: " + libro.getEditorial() );
		System.out.println( "author :: " + libro.getAuthor().getName() );
		System.out.println( "country :: " + libro.getAuthor().getCountry() );
		
		if( libro.getAnios() != null ){
			System.out.print( "años :: " );
			for( String anio: libro.getAnios() ){
				System.out.print( anio + " " );
			}
			System.out.println();
		}
	}
	
	//"id":"665e3f95424d29efae864d8ebb03a56b","rev":"1-9e1643a5be12212f13b68a4550066013"
	//"id":"68aaa6da1d7f584a99ca8812e1000a55","rev":"1-f99d167c12adb1faf30458c2e49bcf91"
	public static void saveBook( CouchdbTemplate template ) {
		Book libro = new Book( "Que onda con tu cuerpo", "Televisa", 
				new Author( "Jordi Rosado", "MX" )  );
		libro.setAnios( new String[]{ "1997", "2001", "2005" } );
		template.persist( libro );
	}
	
}
