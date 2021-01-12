package com.krzem.jwt_auth;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.SocketAddress;
import java.util.Map;



public class HTTPConnection{
	public HTTPServer s;
	public SocketAddress a;
	public File f;
	public Map<String,String> h;
	public String p;
	public PrintWriter out;
	public BufferedOutputStream b_out;



	public HTTPConnection(HTTPServer s,SocketAddress a,File f,Map<String,String> h,String p,PrintWriter out,BufferedOutputStream b_out){
		this.s=s;
		this.a=a;
		this.f=f;
		this.h=h;
		this.p=p;
		this.out=out;
		this.b_out=b_out;
	}
}