package com.krzem.jwt_auth;



public class Main{
	public static void main(String[] args){
		new Main();
	}



	public Main(){
		new HTTPServer(HTTPServerFunctions.class);
	}
}