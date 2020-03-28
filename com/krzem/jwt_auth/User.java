package com.krzem.jwt_auth;



public class User extends Constants{
	public String id="";
	public String em="";
	public String nm="";
	public long sd=-1;
	public String ip="";
	public String pw="";
	public String ts="";
	public long te=-1;
	public String ic="";



	public User(String _dt){
		try{
			String[] dt=_dt.split("\0");
			if (dt.length<AUTH.DATABASE_USER_DATA_ORDER.length){
				String[] ndt=new String[AUTH.DATABASE_USER_DATA_ORDER.length];
				System.arraycopy(dt,0,ndt,0,dt.length);
				for (int i=dt.length;i<ndt.length;i++){
					ndt[i]="";
				}
				dt=ndt;
			}
			int i=0;
			for (String fn:AUTH.DATABASE_USER_DATA_ORDER){
				switch (this.getClass().getDeclaredField(fn).getGenericType().toString()){
					case "long":
						this.getClass().getDeclaredField(fn).set(this,Long.parseLong(dt[i]));
						break;
					case "class java.lang.String":
						this.getClass().getDeclaredField(fn).set(this,dt[i]);
						break;
					default:
						System.out.println(this.getClass().getDeclaredField(fn).getGenericType().toString());
				}
				i++;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public User(String id,String em,String nm,String pw,long sd,String ip){
		this.id=id;
		this.em=em;
		this.nm=nm;
		this.pw=pw;
		this.sd=sd;
		this.ip=ip;
	}



	public String to_string(){
		try{
			String o="";
			for (String fn:AUTH.DATABASE_USER_DATA_ORDER){
				o+="\0"+this.getClass().getDeclaredField(fn).get(this).toString();
			}
			return o.substring(1);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}