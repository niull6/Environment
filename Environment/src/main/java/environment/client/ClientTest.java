package environment.client;

import java.util.Collection;

import environment.bean.Environment;
import environment.util.Configuration;
import environment.util.ConfigurationImpl;

public class ClientTest {

	public static void main(String[] args) throws Exception {
		// GatherImpl e = new GatherImpl();
		// e.gather();

		// System.out.println();

		// Collection<Environment> coll = e.gather();
		// System.out.println(coll.size());

		// ClientImpl c = new ClientImpl();
		// c.send(e.gather());

		Configuration conf = new ConfigurationImpl();
		Gather e = conf.getGather();
		Client client = conf.getClient();
		
		client.send(e.gather());

	}

}
