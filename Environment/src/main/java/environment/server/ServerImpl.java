package environment.server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import environment.bean.Environment;
import environment.util.Configuration;
import environment.util.ConfigurationAWare;
import environment.util.Log;

public class ServerImpl implements Server, ConfigurationAWare {

	Log log;
	DBStore db;
	int port;

	@Override
	public void setConfiguration(Configuration configuration) {
		try {
			log = configuration.getLogger();
			db = configuration.getDbStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Properties properties) throws Exception {
		port = Integer.parseInt(properties.getProperty("port"));

	}

	@Override
	public Collection<Environment> reciver() throws Exception {

		ServerSocket ss = new ServerSocket(port);
		log.info("等待客户端连接...");

		Socket client = ss.accept();
		log.info("客户端连接成功！");

		// 一次获取一个对象集合
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		log.info("开始接收对象");
		Collection<Environment> coll = (Collection<Environment>) ois.readObject();

		if (ois != null) {
			ois.close();
		}
		log.info("网络连接释放！");

		log.info("开始建入数据库！");
		db.saveDb(coll);
		return coll;
	}

	@Override
	public void shutdown() {

	}
}
