package environment.client;



import environment.server.Server;
import environment.util.Configuration;
import environment.util.ConfigurationImpl;

public class ServerTest {

	public static void main(String[] args) throws Exception {
		  
		   //SercerImpl s=new SercerImpl();
		   //s.reciver();
		  // Collection<Environment> coll=s.reciver();
		   
		   
		   //JDK1.8  新特性
		   //coll.forEach(a -> System.out.println(a));
		   
		   
		   Configuration conf=new ConfigurationImpl();
		   Server server=conf.getServer();
		   server.reciver();
		   
		  
		   
		   
		
	}
}
