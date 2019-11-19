package environment.util;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootLogger;

public class LogImpl implements Log {

	// Log的输出器
	Logger logger;
	String configPath;

	@Override
	public void init(Properties properties) throws Exception {
		// 声明log4j配置文件的路径
		configPath = properties.getProperty("log_properties");
		// 读取解析配置文件，初始化log4j框架
		PropertyConfigurator.configure(configPath);
		// 获得输出器
		logger = Logger.getLogger(RootLogger.class);
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void fatal(String message) {
		logger.fatal(message);
	}

}
