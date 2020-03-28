package com.krzem.jwt_auth;



import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;



public class Cryptography extends Constants{
	private static SecureRandom _sr=new SecureRandom();



	public static String _sha256(String s){
		try{
			MessageDigest md=MessageDigest.getInstance("SHA-256");
			byte[] hb=md.digest(s.getBytes("utf-8"));
			String o="";
			for (int i=0;i<hb.length;i++){
				String h=Integer.toHexString(hb[i]&0xff);
				if (h.length()==1){
					o+="0";
				}
				o+=h;
			}
			return o;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	public static String _md5(String s){
		try{
			MessageDigest md=MessageDigest.getInstance("MD5");
			byte[] hb=md.digest(s.getBytes("utf-8"));
			String o="";
			for (int i=0;i<hb.length;i++){
				String h=Integer.toHexString(hb[i]&0xff);
				if (h.length()==1){
					o+="0";
				}
				o+=h;
			}
			return o;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	public static String _hmacsha256(String s,String sc){
		try{
			Mac hmac=Mac.getInstance("HmacSHA256");
			SecretKeySpec sc_k=new SecretKeySpec(sc.getBytes(),"HmacSHA256");
			hmac.init(sc_k);
			String o=Cryptography._base64_encode_no_padding(hmac.doFinal(s.getBytes()));
			return o;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	public static String _gen_token_secret(){
		byte[] bl=new byte[AUTH.TOKEN_SECRET_LENGTH];
		Cryptography._sr.nextBytes(bl);
		for (int i=0;i<AUTH.TOKEN_SECRET_LENGTH;i++){
			while ((bl[i]&0xff)<=1){
				byte[] tmp=new byte[1];
				Cryptography._sr.nextBytes(tmp);
				bl[i]=tmp[0];
			}
		}
		return new String(bl);
	}



	public static String _gen_id(){
		byte[] bl=new byte[AUTH.ID_BYTES_LENGTH];
		Cryptography._sr.nextBytes(bl);
		return Cryptography._md5(new String(bl)+Long.toString(System.nanoTime()));
	}



	public static String _base64_decode(String s){
		return new String(Base64.getDecoder().decode(s));
	}



	public static String _base64_url_decode(String s){
		return new String(Base64.getUrlDecoder().decode(s));
	}



	public static String _base64_decode_no_padding(String s,int l){
		String o=new String(Cryptography._base64_decode(s));
		while (o.length()!=l){
			s+="=";
			o=new String(Cryptography._base64_decode(s));
		}
		return o;
	}



	public static String _base64_encode(String s){
		return Base64.getEncoder().encodeToString(s.getBytes());
	}



	public static String _base64_encode(byte[] dt){
		return Base64.getEncoder().encodeToString(dt);
	}



	public static String _base64_url_encode(String s){
		return Base64.getUrlEncoder().encodeToString(s.getBytes());
	}



	public static String _base64_url_encode(byte[] dt){
		return Base64.getUrlEncoder().encodeToString(dt);
	}



	public static String _base64_encode_no_padding(String s){
		return Base64.getEncoder().withoutPadding().encodeToString(s.getBytes());
	}



	public static String _base64_encode_no_padding(byte[] dt){
		return Base64.getUrlEncoder().withoutPadding().encodeToString(dt);
	}
}