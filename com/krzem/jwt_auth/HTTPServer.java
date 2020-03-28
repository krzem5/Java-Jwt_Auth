package com.krzem.jwt_auth;



import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.Exception;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class HTTPServer extends Constants{
	public HTTPServer(Class fc){
		try{
			Map<String,Map<String,Method>> ft=this._gen_table(fc);
			List<String> pml=this._gen_payload_method_list(fc);
			HTTPServer cls=this;
			ServerSocket ss=new ServerSocket(PORT);
			while (true){
				Socket s=ss.accept();
				new Thread(new Runnable(){
					@Override
					public void run(){
						cls._process(s,pml,ft,fc);
					}
				}).start();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public void send_header(int c,PrintWriter out,String ct,int l){
		try{
			out.printf("HTTP/1.1 %d %s\nServer: HTTP Server: 1.0\nDate: %s\nContent-type: %s\nContent-length: %d\n\n",c,this._get_code_name(c),new Date().toString(),ct,l);
			out.flush();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public void send_200(File f,PrintWriter out,BufferedOutputStream b_out){
		try{
			this.send_header(200,out,this.get_type(f),this.get_length(f));
			this.read_file(f,b_out);
			b_out.flush();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public void send_404(PrintWriter out,BufferedOutputStream b_out){
		this.send_header(404,out,"text/plain",0);
	}



	public void send_501(PrintWriter out,BufferedOutputStream b_out){
		this.send_header(404,out,"text/plain",0);
	}



	public File get_file(String fp){
		fp=fp.replace("/","\\");
		fp=fp.replace("..\\","");
		if (fp.endsWith("\\")){
			fp+="index.html";
		}
		return new File(ROOT+fp);
	}



	public int get_length(File f){
		return (int)f.length();
	}



	public String get_type(File f){
		if (CONTENT_TYPES.containsKey(f.getName().split("\\.")[f.getName().split("\\.").length-1])==false){
			return "text/plain";
		}
		return CONTENT_TYPES.get(f.getName().split("\\.")[f.getName().split("\\.").length-1]);
	}



	public void read_file(File f,BufferedOutputStream o){
		try{
			FileInputStream s=new FileInputStream(f);
			int sz=this.get_length(f);
			byte[] b=new byte[1024];
			while (true){
				if (s.read(b,0,1024)==-1){
					break;
				}
				o.write(b);
			}
			s.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	private void _process(Socket cs,List<String> pml,Map<String,Map<String,Method>> ft,Class fc){
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(cs.getInputStream()));
			PrintWriter out=new PrintWriter(cs.getOutputStream());
			BufferedOutputStream b_out=new BufferedOutputStream(cs.getOutputStream());
			String[] dt;
			try{
				dt=in.readLine().split(" ");
			}
			catch (Exception e){
				in.close();
				out.close();
				b_out.close();
				return;
			}
			if (!ft.containsKey(dt[0])){
				System.out.printf("(%s) %s (send_501)\n",cs.getRemoteSocketAddress().toString(),String.join(" ",dt));
				this.send_501(out,b_out);
			}
			else{
				Map<String,String> hl=new HashMap<String,String>();
				while (true){
					String h=in.readLine();
					if (h.length()==0){
						break;
					}
					String v=String.join(":",h.substring(h.split(":")[0].length()+1));
					hl.put(h.split(":")[0],(v.charAt(0)==' '?v.substring(1):v));
				}
				String p=null;
				if (hl.containsKey("Content-Length")){
					p="";
					for (int i=0;i<Integer.parseInt(hl.get("Content-Length"));i++){
						p+=String.valueOf((char)in.read());
					}
				}
				if (p==null&&pml.contains(dt[0])){
					System.out.printf("(%s) %s (send_411)\n",cs.getRemoteSocketAddress().toString(),String.join(" ",dt));
					this.send_header(411,out,"text/plain",0);
				}
				else{
					boolean ex=false;
					for (Map.Entry<String,Method> e:ft.get(dt[0]).entrySet()){
						if (this._match_tree(e.getKey(),dt[1])==true){
							e.getValue().invoke(fc,new HTTPConnection(this,cs.getRemoteSocketAddress(),this.get_file(dt[1]),hl,p,out,b_out));
							ex=true;
							System.out.printf("(%s) %s (%s)\n",cs.getRemoteSocketAddress().toString(),String.join(" ",dt),e.getValue().getName());
							break;
						}
					}
					if (ex==false){
						System.out.printf("(%s) %s (send_405)\n",cs.getRemoteSocketAddress().toString(),String.join(" ",dt));
						this.send_header(405,out,"text/plain",0);
					}
				}
			}
			in.close();
			out.close();
			b_out.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	private Map<String,Map<String,Method>> _gen_table(Class c){
		try{
			Map<String,String> fm=(Map<String,String>)c.getDeclaredMethod("func_table").invoke(c);
			Map<String,Map<String,Method>> o=new HashMap<String,Map<String,Method>>();
			for (Map.Entry<String,String> e:fm.entrySet()){
				if (!o.containsKey(e.getKey().split(" ")[0])){
					o.put(e.getKey().split(" ")[0],new HashMap<String,Method>());
				}
				o.get(e.getKey().split(" ")[0]).put(e.getKey().substring(e.getKey().split(" ")[0].length()+1),c.getDeclaredMethod(e.getValue(),HTTPConnection.class));
			}
			return o;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	private List<String> _gen_payload_method_list(Class c){
		try{
			return (List<String>)c.getDeclaredMethod("payload_method_list").invoke(c);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	private boolean _match_tree(String m,String f){
		main: for (String p:m.split(";")){
			if (p.equals("*")||(p.equals("/")&&f.equals("/"))){
				return true;
			}
			String[] sf=f.split("/");
			int i=0;
			for (String s:p.split("/")){
				if (s.equals("*")){
					return true;
				}
				if (i==sf.length||s.equals(sf[i])==false){
					continue main;
				}
				i++;
			}
			if (p.split("/").length==sf.length){
				return true;
			}
		}
		return false;
	}



	private String _get_code_name(int c){
		return (CODE_NAMES.containsKey(c)==false?"OK (Default)":CODE_NAMES.get(c));
	}
}