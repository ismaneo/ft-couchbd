package com.ftech.couchdb.test.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.ftech.couchdb.models.Document;

public class TypesDoc extends Document {
	
	public boolean xb;
	public byte    maxb;
	public byte    minb;
	public short   maxs;
	public short   mins;
	public int     maxi;
	public int     mini;
	public long    maxl;
	public long    minl;
	public float   maxf;
	public float   minf;
	public double  maxd;
	public double  mind;
	
	public Date    fecha;
	public BigInteger bi;
	public BigDecimal bd;
	
	/*
	public Boolean xb;
	public Byte    maxb;
	public Byte    minb;
	public Short   maxs;
	public Short   mins;
	public Integer maxi;
	public Integer mini;
	public Long    maxl;
	public Long    minl;
	public Float   maxf;
	public Float   minf;
	public Double  maxd;
	public Double  mind;
	*/
}
