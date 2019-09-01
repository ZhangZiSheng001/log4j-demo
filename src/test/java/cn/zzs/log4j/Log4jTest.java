package cn.zzs.log4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * @ClassName: Log4jTest
 * @Description: 测试log4j
 * @author: zzs
 * @date: 2019年9月1日 下午3:47:35
 */
public class Log4jTest {
	//采用slf4j的方式
	//private static Logger logger = LoggerFactory.getLogger(Log4jTest.class);
	//采用commons-logging的方式
	private static Log logger = LogFactory.getLog(Log4jTest.class);
	
	@Test
	public void test01() {
		logger.debug("我是debug信息");
		logger.info("我是info信息");
		logger.warn("我是warn信息");
		logger.error("我是error信息");
	}
}
