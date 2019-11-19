package environment.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import environment.client.Client;
import environment.client.Gather;
import environment.server.DBStore;
import environment.server.Server;

public class ConfigurationImpl implements Configuration {

	// map集合保存所有模块的对象
	Map<String, WossModule> map = new HashMap<>();

	public ConfigurationImpl() throws Exception {

		// 读取解析配置文件
		SAXReader reader = new SAXReader();

		Document doc = reader.read("src/main/java/com/briup/environment/util/config.xml");
		// 获取根元素
		Element root = doc.getRootElement();

		// 获取根元素下所有子元素
		List<Element> elements = root.elements();
		for (Element element : elements) {
			String className = element.attributeValue("class");

			// 反射机制创建实例化对象
			WossModule woss = (WossModule) Class.forName(className).newInstance();
			// 向集合中添加Woss对象
			map.put(element.getName(), woss);

			List<Element> childElements = element.elements();
			Properties pro = new Properties();
			for (Element child : childElements) {
				String key = child.getName();
				String value = child.getText();
				pro.setProperty(key, value);
			}
			woss.init(pro);
		}

		for (String key : map.keySet()) {
			// 调用创建好的模块对象的setConfiguration方法
			// 判断模块是否实现了ConfigurationAWare接口
			// 如果实现了，再强转并调用setConfiguration方法
			WossModule woss = map.get(key);
			if (woss instanceof ConfigurationAWare) {
				((ConfigurationAWare) woss).setConfiguration(this);
			}
		}

	}

	@Override
	public Log getLogger() throws Exception {
		return (Log) map.get("logger");
	}

	@Override
	public Server getServer() throws Exception {
		return (Server) map.get("server");
	}

	@Override
	public Client getClient() throws Exception {
		return (Client) map.get("client");
	}

	@Override
	public DBStore getDbStore() throws Exception {
		return (DBStore) map.get("dbstore");
	}

	@Override
	public Gather getGather() throws Exception {
		return (Gather) map.get("gather");
	}

	@Override
	public Backup getBackup() throws Exception {
		return (Backup) map.get("backup");
	}

}
