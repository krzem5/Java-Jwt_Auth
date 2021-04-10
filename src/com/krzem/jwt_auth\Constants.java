package com.krzem.jwt_auth;



import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class Constants{
	public static final String ROOT=new File("./assets/web/").getAbsolutePath();
	public static final int PORT=9000;

	public static final Map<Integer,String> CODE_NAMES=new HashMap<Integer,String>(){{
		this.put(200,"OK");
		this.put(401,"Unauthorized");
		this.put(404,"Not Found");
		this.put(405,"Method Not Allowed");
		this.put(411,"Length Required");
		this.put(429,"Too Many Requests");
		this.put(501,"Method Not Supported");
	}};
	public static final Map<Integer,Integer> AUTH_OUTPUT_CODES=new HashMap<Integer,Integer>(){{
		this.put(0,200);
		this.put(1,200);
		this.put(2,200);
		this.put(3,200);
		this.put(3,200);
		this.put(4,200);
		this.put(5,500);
		this.put(6,401);
	}};
	public static final Map<String,String> CONTENT_TYPES=new HashMap<String,String>(){{
		this.put("html","text/html");
		this.put("svg","image/svg+xml");
	}};

	public static final class AUTH{
		public static final String DATABASE_PATH="./assets/user.db";
		public static final String[] DATABASE_USER_DATA_ORDER=new String[]{"id","em","nm","sd","ip","pw","ts","te","ic"};
		public static final int USERNAME_MIN_LENGTH=3;
		public static final int USERNAME_MAX_LENGTH=16;
		public static final int PASSWORD_MIN_LENGTH=6;
		public static final int PASSWORD_MAX_LENGTH=20;
		public static final String USERNAME_VALID_LETTERS="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-";
		public static final int PROFILE_IMAGE_SIZE=100;
		public static final int TOKEN_SECRET_LENGTH=512;
		public static final int TOKEN_EXPIRATION_LENGTH=3600000;
		public static final int ID_BYTES_LENGTH=256;

		public static final boolean RATE_LIMITING=false;

		public static final Map<String,Integer> RATE_LIMIT=new HashMap<String,Integer>(){{
			this.put("check_email",100);
			this.put("check_user",100);
			this.put("login",3500);
			this.put("delete",60000);
		}};
	}
}
