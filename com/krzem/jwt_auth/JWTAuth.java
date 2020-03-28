package com.krzem.jwt_auth;



import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.Exception;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;



public class JWTAuth extends Constants{
	private Map<String,User> db;
	private Map<String,Map<String,Long>> ll;



	public JWTAuth(){
		try{
			this.db=new HashMap<String,User>();
			String _db=Cryptography._base64_decode(new String(Files.readAllBytes(Paths.get(AUTH.DATABASE_PATH))));
			for (String l:_db.split("\1")){
				if (l.length()==0){
					continue;
				}
				this.db.put(l.split("\0")[0],new User(l));
			}
			this.ll=new HashMap<String,Map<String,Long>>();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public int check_email(String em){
		em=Cryptography._base64_decode(em);
		Pattern pt=Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
		if (pt.matcher(em.toLowerCase()).matches()==false){
			return 1;
		}
		for (Map.Entry<String,User> e:this.db.entrySet()){
			if (e.getValue().em.equals(em)){
				return 2;
			}
		}
		return 0;
	}



	public int check_user(String nm,boolean b64){
		nm=(b64==true?Cryptography._base64_url_decode(nm):nm);
		nm=nm.replace("\\|","|");
		for (int i=0;i<nm.length();i++){
			if (!AUTH.USERNAME_VALID_LETTERS.contains(String.valueOf(nm.charAt(i)))){
				return 1;
			}
		}
		if (nm.length()<AUTH.USERNAME_MIN_LENGTH){
			return 2;
		}
		if (nm.length()>AUTH.USERNAME_MAX_LENGTH){
			return 3;
		}
		for (Map.Entry<String,User> e:this.db.entrySet()){
			if (e.getValue().nm.equals(nm)){
				return 4;
			}
		}
		return 0;
	}



	public int signup(String dt,SocketAddress a){
		dt=Cryptography._base64_url_decode(dt);
		String[] sp=dt.split("\\|");
		String em=dt.substring(0,dt.length()-sp[sp.length-1].length()-sp[sp.length-2].length()-2).replace("\\|","|");
		String nm=sp[sp.length-2];
		int r=this.check_user(nm,false);
		if (r!=0){
			return r;
		}
		String id=Cryptography._gen_id();
		String pw=Cryptography._sha256(em+"\0"+id+"\0"+sp[sp.length-1]);
		if (pw==null){
			return 5;
		}
		String ip=a.toString().split(":")[0].replace("/","");
		this.db.put(id,new User(id,em,nm,pw,System.currentTimeMillis(),ip.substring(0,ip.length()-ip.split("\\.")[ip.split("\\.").length-1].length())+"*"));
		this._save();
		return 0;
	}



	public String login(String dt){
		dt=Cryptography._base64_url_decode(dt);
		String[] sp=dt.split("\\|");
		String em=dt.substring(0,dt.length()-sp[sp.length-1].length()-1).replace("\\|","|");
		String id=null;
		for (Map.Entry<String,User> e:this.db.entrySet()){
			if (e.getValue().em.equals(em)){
				id=e.getKey();
				break;
			}
		}
		if (id==null){
			return "\6";
		}
		String ph=Cryptography._sha256(em+"\0"+id+"\0"+sp[sp.length-1]);
		if (ph==null){
			return "\6";
		}
		if (!ph.equals(this.db.get(id).pw)){
			return "\6";
		}
		String ts=Cryptography._gen_token_secret();
		String tk=this._jwt_token(id,ts);
		this.db.get(id).ts=ts;
		this.db.get(id).te=System.currentTimeMillis()+AUTH.TOKEN_EXPIRATION_LENGTH;
		this._save();
		return tk;
	}



	public String user_data(String tk){
		String id=this._verify_token(tk);
		if (id==null){
			return "\6";
		}
		return String.format("{\"email\":\"%s\",\"name\":\"%s\",\"icon\":\"%s\",\"signup_date\":%d,\"signup_ip\":\"%s\"}",this.db.get(id).em,this.db.get(id).nm,this.db.get(id).ic,this.db.get(id).sd,this.db.get(id).ip);
	}



	public int set_profile_image(String dt,String tk){
		String id=this._verify_token(tk);
		if (id==null){
			return 6;
		}
		this.db.get(id).ic=dt;
		return 0;
	}



	public String set_password(String dt,String tk){
		dt=Cryptography._base64_url_decode(dt);
		String id=this._verify_token(tk);
		if (id==null){
			return "\6";
		}
		this.db.get(id).pw=Cryptography._sha256(this.db.get(id).em+"\0"+id+"\0"+dt);
		String ts=Cryptography._gen_token_secret();
		tk=this._jwt_token(id,ts);
		this.db.get(id).ts=ts;
		this.db.get(id).te=System.currentTimeMillis()+AUTH.TOKEN_EXPIRATION_LENGTH;
		return tk;
	}



	public int set_name(String dt,String tk){
		dt=Cryptography._base64_url_decode(dt);
		String id=this._verify_token(tk);
		if (id==null){
			return 6;
		}
		return 0;
	}



	public int logout(String tk){
		String id=this._verify_token(tk);
		if (id==null){
			return 6;
		}
		this.db.get(id).ts="";
		this.db.get(id).te=-1;
		return 0;
	}



	public int delete(String dt,String tk){
		dt=Cryptography._base64_url_decode(dt);
		if (this._verify_token(tk)==null){
			return 6;
		}
		for (Map.Entry<String,User> e:this.db.entrySet()){
			if (this._compare_jwt(tk,e.getKey(),e.getValue().ts)){
				if (!Cryptography._sha256(e.getValue().em+"\0"+e.getKey()+"\0"+dt).equals(e.getValue().pw)){
					return 6;
				}
				this.db.remove(e.getKey());
				this._save();
				return 0;
			}
		}
		return 6;
	}



	public boolean limit(HTTPConnection c,String p){
		if (AUTH.RATE_LIMITING==false||AUTH.RATE_LIMIT.containsKey(p)==false){
			return false;
		}
		try{
			if (!this.ll.containsKey(p)){
				this.ll.put(p,new HashMap<String,Long>());
			}
			String a=c.a.toString().split(":")[0];
			if (!this.ll.get(p).containsKey(a)||(this.ll.get(p).containsKey(a)&&this.ll.get(p).get(a)<=System.currentTimeMillis())){
				this.ll.get(p).put(a,System.currentTimeMillis()+AUTH.RATE_LIMIT.get(p));
				return false;
			}
			String df=Long.toString(this.ll.get(p).get(a)-System.currentTimeMillis());
			c.s.send_header(429,c.out,"text/plain",df.length());
			c.b_out.write(df.getBytes());
			c.b_out.flush();
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}



	private void _save(){
		try{
			String o="";
			for (Map.Entry<String,User> e:this.db.entrySet()){
				o+=e.getValue().to_string()+"\1";
			}
			Files.write(Paths.get(AUTH.DATABASE_PATH),Cryptography._base64_encode(o).getBytes());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	private String _jwt_token(String dt,String s){
		String h=Cryptography._base64_encode_no_padding("HS256".getBytes(StandardCharsets.UTF_8));
		String p=Cryptography._base64_encode_no_padding(dt.getBytes(StandardCharsets.UTF_8));
		String sg=Cryptography._hmacsha256(String.format("%s.%s",h,p),s);
		return String.format("%s.%s.%s",h,p,sg);
	}



	private boolean _compare_jwt(String tk,String dt,String s){
		if (s.length()==0||tk==null||tk.split("\\.").length!=3){
			return false;
		}
		String _tk=this._jwt_token(dt,s);
		return _tk.equals(tk);
	}



	private String _verify_token(String tk){
		if (tk==null||tk.length()<8){
			return null;
		}
		tk=tk.substring(7);
		if (tk.split("\\.").length!=3){
			return null;
		}
		String id=Cryptography._base64_decode_no_padding(tk.split("\\.")[1],32);
		if (id==null||this.db.containsKey(id)==false||this._compare_jwt(tk,id,this.db.get(id).ts)==false){
			return null;
		}
		if (this.db.get(id).te<=System.currentTimeMillis()){
			this.db.get(id).ts="";
			this.db.get(id).te=-1;
			return null;
		}
		return id;
	}
}