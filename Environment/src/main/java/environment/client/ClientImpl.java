package environment.client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import environment.bean.Environment;
import environment.util.Backup;
import environment.util.BackupImpl;
import environment.util.Configuration;
import environment.util.ConfigurationAWare;
import environment.util.Log;

public class ClientImpl implements Client,ConfigurationAWare {

	Log log ;
	Backup backup;
	String ip;
	int port;
	String backupFile;
	
	
	@Override
	public void setConfiguration(Configuration configuration) {
		try {
			log=configuration.getLogger();
			backup=configuration.getBackup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void init(Properties properties) throws Exception {
		 ip = properties.getProperty("ip");
		 port = Integer.parseInt(properties.getProperty("port"));
		 backupFile=properties.getProperty("backup_file");
		
	}

	@Override
	public void send(Collection<Environment> coll) throws Exception {

		// 遍历集合
		ObjectOutputStream oos = null;
		Socket client = null;
		// 尝试读取备份
		//Backup backup = new BackupImpl();
		Object obj = backup.load(backupFile);

		if (obj != null) {
			Collection<Environment> oldColl = (Collection<Environment>) obj;
			coll.addAll(oldColl);
			log.info("发现备份文件存在" + oldColl.size() + "条残留数据,已经添加到本次发送队列！");

			// 必须删除备份文件-->发送成功就不需要备份了,发送失败需要备份
			backup.deleteBackup(backupFile);
			log.info("备份文件删除成功");
		}

		try {
			client = new Socket(ip, port);
			log.info("网络连接已建立！");
			oos = new ObjectOutputStream(client.getOutputStream());

			// 一次读取一个对象集合
			oos.writeObject(coll);
			oos.flush();
			log.info("数据发送完成");
		} catch (Exception e) {
			// 备份数据
			backup.backup(backupFile, coll);
			log.error("网络连接异常，备份数据完成！");

		}
		// 非空判断
		if (oos != null) {
			oos.close();
		}

		log.info("网络连接释放资源！");
	}

	

}
