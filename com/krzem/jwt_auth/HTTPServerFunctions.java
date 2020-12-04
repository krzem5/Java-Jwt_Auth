package com.krzem.jwt_auth;



import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class HTTPServerFunctions extends Constants{
	public static JWTAuth auth=new JWTAuth();



	public static Map<String,String> func_table(){
		return new HashMap<String,String>(){{
			this.put("GET /;/login;/signup;/password;/img/*","get");
			this.put("POST /auth/signup","signup");
			this.put("POST /auth/check_user","check_user");
			this.put("POST /auth/check_email","check_email");
			this.put("POST /auth/login","login");
			this.put("PUT /auth/profile_image","set_profile_image");
			this.put("PUT /auth/password","set_password");
			this.put("PUT /auth/name","set_name");
			this.put("GET /auth/user_data","user_data");
			this.put("GET /auth/logout","logout");
			this.put("POST /auth/delete","delete");
			// this.put("GET /api/v1/notes","get_notes");
			// this.put("PUT /api/v1/notes","put_notes");
			// this.put("GET /api/v1/notes/recent","get_recent_notes");
			// this.put("GET /api/v1/notes/popular","get_popular_notes");
		}};
	}



	public static List<String> payload_method_list(){
		return new ArrayList<String>(){{
			this.add("POST");
			this.add("PUT");
		}};
	}



	public static void get(HTTPConnection c){
		if (c.f.exists()==false){
			c.f=new File(c.f.getAbsolutePath()+".html");
		}
		if (c.f.exists()){
			c.s.send_200(c.f,c.out,c.b_out);
		}
		else{
			c.s.send_404(c.out,c.b_out);
		}
	}



	public static void signup(HTTPConnection c){
		HTTPServerFunctions._send(c,"signup",HTTPServerFunctions.auth.signup(c.p,c.a));
	}



	public static void check_email(HTTPConnection c){
		HTTPServerFunctions._send(c,"check_email",HTTPServerFunctions.auth.check_email(c.p));

	}



	public static void check_user(HTTPConnection c){
		HTTPServerFunctions._send(c,"check_user",HTTPServerFunctions.auth.check_user(c.p,true));
	}



	public static void login(HTTPConnection c){
		HTTPServerFunctions._send(c,"login",HTTPServerFunctions.auth.login(c.p));
	}



	public static void user_data(HTTPConnection c){
		HTTPServerFunctions._send(c,"user_data",HTTPServerFunctions.auth.user_data(c.h.get("authorization")));
	}



	public static void set_profile_image(HTTPConnection c){
		HTTPServerFunctions._send(c,"set_profile_image",HTTPServerFunctions.auth.set_profile_image(c.p,c.h.get("authorization")));
	}



	public static void set_password(HTTPConnection c){
		HTTPServerFunctions._send(c,"set_password",HTTPServerFunctions.auth.set_password(c.p,c.h.get("authorization")));
	}



	public static void set_name(HTTPConnection c){
		HTTPServerFunctions._send(c,"set_name",HTTPServerFunctions.auth.set_name(c.p,c.h.get("authorization")));
	}



	public static void logout(HTTPConnection c){
		HTTPServerFunctions._send(c,"logout",HTTPServerFunctions.auth.logout(c.h.get("authorization")));
	}



	public static void delete(HTTPConnection c){
		HTTPServerFunctions._send(c,"delete",HTTPServerFunctions.auth.delete(c.p,c.h.get("authorization")));
	}



	// public static void get_notes(HTTPConnection c){
	// 	try{
	// 		String r=HTTPServerFunctions.auth.get_notes(c.h.get("authorization"));
	// 		c.s.send_header(200,c.out,"text/plain",r.length());
	// 		c.b_out.write(r.getBytes());
	// 		c.b_out.flush();
	// 	}
	// 	catch (Exception e){
	// 		e.printStackTrace();
	// 	}
	// }



	// public static void put_notes(HTTPConnection c){
	// 	try{
	// 		int r=HTTPServerFunctions.auth.put_notes(c.p,c.h.get("authorization"));
	// 		c.s.send_header(HTTPServerFunctions._map_output(r),c.out,"text/plain",1);
	// 		c.b_out.write((byte)r);
	// 		c.b_out.flush();
	// 	}
	// 	catch (Exception e){
	// 		e.printStackTrace();
	// 	}
	// }



	// public static void get_recent_notes(HTTPConnection c){
	// 	try{
	// 		String r=HTTPServerFunctions.auth.get_recent_notes();
	// 		c.s.send_header(200,c.out,"text/plain",r.length());
	// 		c.b_out.write(r.getBytes());
	// 		c.b_out.flush();
	// 	}
	// 	catch (Exception e){
	// 		e.printStackTrace();
	// 	}
	// }



	// public static void get_popular_notes(HTTPConnection c){
	// 	try{
	// 		String r=HTTPServerFunctions.auth.get_popular_notes();
	// 		c.s.send_header(200,c.out,"text/plain",r.length());
	// 		c.b_out.write(r.getBytes());
	// 		c.b_out.flush();
	// 	}
	// 	catch (Exception e){
	// 		e.printStackTrace();
	// 	}
	// }



	private static void _send(HTTPConnection c,String nm,Object _r){
		if (HTTPServerFunctions.auth.limit(c,nm)){
			return;
		}
		try{
			String r=(_r instanceof String?(String)_r:new String(new byte[]{(byte)(int)_r}));
			String o=Cryptography._base64_url_encode(r);
			c.s.send_header((r.length()==1?HTTPServerFunctions._map_output((int)r.getBytes()[0]):200),c.out,"text/plain",o.length());
			c.b_out.write(o.getBytes());
			c.b_out.flush();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	private static int _map_output(int c){
		return (int)AUTH_OUTPUT_CODES.get(c);
	}
}
