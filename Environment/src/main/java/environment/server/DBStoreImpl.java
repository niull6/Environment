package environment.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import environment.bean.Environment;
import environment.util.Backup;
import environment.util.Configuration;
import environment.util.ConfigurationAWare;
import environment.util.Log;

public class DBStoreImpl implements DBStore, ConfigurationAWare {

	Log log;
	Backup backup;

	String backupFile;
	String dirverName;
	String url;
	String user;
	String password;
	int bathsize;

	@Override
	public void setConfiguration(Configuration configuration) {
		try {
			log = configuration.getLogger();
			backup = configuration.getBackup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Properties properties) throws Exception {
		backupFile = properties.getProperty("backup_file");
		dirverName = properties.getProperty("driver_name");
		url = properties.getProperty("url");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		bathsize = Integer.parseInt(properties.getProperty("bathsize"));
	}

	@Override
	public void saveDb(Collection<Environment> coll) throws Exception {

		Object obj = backup.load(backupFile);
		if (obj != null) {
			Collection<Environment> oldColl = (Collection<Environment>) obj;
			coll.addAll(oldColl);
			System.out.println("发现备份文件存在" + oldColl.size() + "条残留数据,已经添加到本次入库队列！");

			// 必须删除备份文件-->发送成功就不需要备份了,发送失败需要备份
			backup.deleteBackup(backupFile);
			log.info("备份文件删除成功");
		}

		Class.forName(dirverName);
		Connection conn = DriverManager.getConnection(url, user, password);

		int count = 0;

		try {

			String sql = "insert into e_detail values(?,?,?,?,?,?,?,?,?,?)";

			PreparedStatement pst = conn.prepareStatement(sql);

			conn.setAutoCommit(false); // 手动提交

			for (Environment temp : coll) {
				pst.setString(1, temp.getName());
				pst.setString(2, temp.getSrcId());
				pst.setString(3, temp.getDstId());
				pst.setString(4, temp.getDevId());
				pst.setString(5, temp.getSersorAddress());
				pst.setInt(6, temp.getCount());
				pst.setString(7, temp.getCmd());
				pst.setInt(8, temp.getStatus());
				pst.setFloat(9, temp.getData());
				pst.setTimestamp(10, temp.getGather_date());

				pst.addBatch();

				count++;
				if (count % bathsize == 0) {
					pst.executeBatch();
					conn.commit();
				}
			}
			pst.executeBatch();
			conn.commit();
			log.info("数据建入数据库完成！");
			conn.close();
		} catch (Exception e) {

			// 回滚没有提交的数据
			conn.rollback();

			// 备份 有几条数据成功入库
			// 将成功入库的数据删除 将剩下的数据备份
			// 不需备份的条数
			int deleteCount = count / bathsize * bathsize;
			// 迭代器
			Iterator<Environment> iterator = coll.iterator();

			// 删除备份成功的数据
			for (int j = 0; j <= deleteCount; j++) {
				iterator.next();
				iterator.remove();
			}

			backup.backup(backupFile, coll);
			log.error("插入的过程出现异常,已经成功插入" + deleteCount + "条数据,已备份" + coll.size() + "条数据");

		}
	}

}
