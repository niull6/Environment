package environment.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import environment.bean.Environment;
import environment.util.Backup;
import environment.util.Configuration;
import environment.util.ConfigurationAWare;
import environment.util.Log;

public class GatherImpl implements Gather, ConfigurationAWare {

	Log log;
	Backup backup;

	String srcFile;
	String backupFile;

	@Override
	public void init(Properties properties) throws Exception {
		srcFile = properties.getProperty("src_file");
		backupFile = properties.getProperty("char_num");
	}

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
	public Collection<Environment> gather() throws Exception {

		FileInputStream fs = new FileInputStream(srcFile);
		InputStreamReader isr = new InputStreamReader(fs);
		BufferedReader br = new BufferedReader(isr);

		// 备份
		// 创建备份对象

		Object o = backup.load(backupFile);
		long charNum = 0;
		if (o != null) {
			charNum += (long) o;
			br.skip((long) o);
		}

		// 读取备份完后删除备份
		backup.deleteBackup(backupFile);

		Collection<Environment> coll = new ArrayList<>();

		String line = null;
		while ((line = br.readLine()) != null) {
			String str = line;
			String[] s = str.split("[|]");

			Environment e = new Environment();
			e.setSrcId(s[0]);
			e.setDstId(s[1]);
			e.setDevId(s[2]);
			e.setCount(Integer.parseInt(s[4]));
			e.setCmd(s[5]);
			e.setSersorAddress(s[3]);
			e.setStatus(Integer.parseInt(s[7]));

			long time = Long.parseLong(s[8]);
			Timestamp t = new Timestamp(time);
			e.setGather_date(t);

			Environment en = new Environment();

			if ("16".equals(e.getSersorAddress())) {
				e.setName("温度");
				String a = s[6];
				String wendu = a.substring(0, 4);

				float wen = (float) ((Integer.parseInt(wendu, 16) * 0.00268127) - 46.85);
				e.setData(wen);

				en.setName("湿度");
				en.setSrcId(s[0]);
				en.setDstId(s[1]);
				en.setDevId(s[2]);

				en.setCount(Integer.parseInt(s[4]));
				en.setCmd(s[5]);
				en.setSersorAddress("16");
				en.setStatus(Integer.parseInt(s[7]));

				long times = Long.parseLong(s[8]);
				Timestamp tp = new Timestamp(times);
				en.setGather_date(tp);

				String aa = s[6];
				String shidu = aa.substring(4, 8);
				float sh = (float) ((Integer.parseInt(shidu, 16) * 0.00190735) - 6);

				en.setData(sh);
			}

			if ("256".equals(e.getSersorAddress())) {
				e.setName("光照强度");
				String a = s[6];
				String light = a.substring(0, 4);
				int i = Integer.parseInt(light, 16);
				e.setData(i);
			}
			if ("1280".equals(e.getSersorAddress())) {
				e.setName("二氧化碳");
				String a = s[6];
				String b = a.substring(0, 4);
				int co2 = Integer.parseInt(b, 16);
				e.setData(co2);
			}
			if (en.getName() != null) {
				coll.add(e);
				coll.add(en);
				// System.out.println(e);
				// System.out.println(en);
			} else {
				coll.add(e);
				// System.out.println(e);
			}

			// System.out.println(y);

			// 数字2 表示每条数据后的-->回车符+换行符
			charNum += line.length() + 2;
			// System.out.println(x);
		}

		backup.backup(backupFile, charNum);
		log.info("解析完毕，解析了" + coll.size() + "条数据,已备份" + charNum + "条数据");
		log.info("读取原始数据文件流关闭！");
		br.close();
		return coll;

	}

}
