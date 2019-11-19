package environment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

public class BackupImpl implements Backup, ConfigurationAWare {

	Log log;
	String backPath;

	@Override
	public void setConfiguration(Configuration configuration) {
		try {
			log = configuration.getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Properties properties) {
		backPath = properties.getProperty("path");
	}

	// 备份文件
	@Override
	public void backup(String fileName, Object data) throws Exception {

		// File类的使用
		// 主要用来操作文件系统文件或目录的结构
		// 一个File对象就代表文件系统中存在或不存在的文件或目录

		// 声明备份目录
		// String backPath="src/backup";
		// 创建备份文件
		File file = new File(backPath, fileName);

		// 判断文件是否存在
		if (file.exists() == false) {
			file.createNewFile(); // 创建新文件
		}

		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(data);
		log.info(backPath + "/" + fileName + "备份成功！");

		if (oos != null) {
			oos.close();
		}
		log.info("读取本地备份文件连接关闭！");

	}

	// 加载备份文件
	@Override
	public Object load(String fileName) throws Exception {

		File file = new File(backPath, fileName);

		if (file.exists() == false) {
			log.info(backPath + "/" + fileName + "备份文件不存在");
			return null;
		} else {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			log.info("加载备份成功！");

			if (ois != null) {
				ois.close();
				log.info("关闭加载备份连接");
			}
			return obj;
		}

	}

	@Override
	public void deleteBackup(String fileName) {
		File file = new File(backPath, fileName);
		if (!file.exists()) {
			log.info("备份文件不存在");
		} else {
			file.delete();
			log.info(backPath + "/" + fileName + "删除备份文件成功！");
		}

	}

}
