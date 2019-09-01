
# log4j

## 简介    
`log4j`用于日志记录，可以将日志输出到控制台、文件、数据库和邮件等。使用时需要引入`log4j.properties`，配置日志信息的优先级、输出目的地、输出格式等。  

`log4j`是具体的日志实现，从解耦的角度考虑可以搭配`common-logging`或者`slf4j`等日志门面（`Logging Facade`）一起使用。`Logging Facade`是一种log的框架接口，它本身并不实现`log`记录的功能，而是在运行时动态查找目前存在的日志库，调用相关的日志函数，从而隐藏具体的日志实现即`commons-logging`会自动发现并应用`Log4j`。

## 使用例子
### 需求
使用log4j将日志信息分别输出到控制台、文件、数据库。

### 工程环境
JDK：1.8.0_201  
maven：3.6.1  
IDE：Spring Tool Suites4 for Eclipse  
mysql：5.7

### 主要步骤
由于`commons-logging`会自动发现并应用`Log4j`，所以我们只需要调用`commons-logging`的方法就行了。  
1. 调用`LogFactory`的`getLog`方法获得`logger`实例；
2. 利用`logger`实例打印日志，当然，前提我们需要配置好`logger`。

### 创建表
```sql
CREATE TABLE `logging_event` (
  `event_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '日志id',
  `project_name` varchar(254) COLLATE utf8_bin DEFAULT NULL COMMENT '项目名',
  `thread_name` varchar(254) COLLATE utf8_bin DEFAULT NULL COMMENT '线程名',
  `caller_filename` varchar(254) COLLATE utf8_bin DEFAULT NULL COMMENT '调用的文件名',
  `caller_class` varchar(254) COLLATE utf8_bin DEFAULT NULL COMMENT '调用的class',
  `caller_method` varchar(254) COLLATE utf8_bin DEFAULT NULL COMMENT '调用的method',
  `caller_line` char(4) COLLATE utf8_bin DEFAULT NULL COMMENT '调用的行数',
  `level_string` varchar(254) COLLATE utf8_bin DEFAULT NULL COMMENT '日志等级',
  `formatted_message` text COLLATE utf8_bin COMMENT '日志信息',
  `gmt_create` datetime DEFAULT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
```

### 创建项目
项目类型`Maven Project`，打包方式`jar`

### 引入依赖
前面已经说过，`log4j`可以搭配`common-logging`或者`slf4j`等日志门面（`Logging Facade`）一起使用。
```xml
<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.12</version>
	<scope>test</scope>
</dependency>
<!-- log4j -->
<dependency>
	<groupId>log4j</groupId>
	<artifactId>log4j</artifactId>
	<version>1.2.17</version>
</dependency>
<!-- Logging Facade:使用commons-logging -->
<dependency>
	<groupId>commons-logging</groupId>
	<artifactId>commons-logging</artifactId>
	<version>1.2</version>
</dependency>
<!-- Logging Facade:使用slf4j -->
<!-- <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.28</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.28</version>
    <scope>test</scope>
</dependency> -->
<!-- mysql驱动：日志存储到数据库才需要用的 -->
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
	<version>8.0.17</version>
</dependency>
```

### 编写log4j.properties
路径：`cn.zzs.log4j`  
这里配置了五种输出类型：  
1. 控制台
2. 文件
3. 文件，每天生成新文件
4. 文件，按大小滚动
5. 数据库（其实不建议输出到数据库的，开销太大）
```properties
#①配置根Logger
log4j.rootLogger=debug,systemOut,logFile,logDailyFile,logRollingFile,logDB
#表示Logger不会在父Logger的appender里输出，默认为true。
log4j.additivity.org.apache=false

#配置全局参数
#项目名
project.name=log4j-demo
#日志存放路径
log.store.path=E:/test/${project.name}

#输出到控制台 
log4j.appender.systemOut=org.apache.log4j.ConsoleAppender
log4j.appender.systemOut.layout=org.apache.log4j.PatternLayout
log4j.appender.systemOut.layout.ConversionPattern=[%p][Thread:%t]:  %m%n
log4j.appender.systemOut.Threshold=debug
log4j.appender.systemOut.ImmediateFlush=TRUE
log4j.appender.systemOut.Target=System.out

#输出到文件 
log4j.appender.logFile=org.apache.log4j.FileAppender
log4j.appender.logFile.layout=org.apache.log4j.PatternLayout
log4j.appender.logFile.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss a} [Thread:%t][Class:%c  Method: %M]%n%p:  %m%n
log4j.appender.logFile.Threshold=warn
log4j.appender.logFile.ImmediateFlush=TRUE
log4j.appender.logFile.Append=TRUE
log4j.appender.logFile.File=${log.store.path}/${project.name}_warn_file.log
log4j.appender.logFile.Encoding=utf-8

#输出到文件，文件按日期滚动
log4j.appender.logDailyFile=org.apache.log4j.DailyRollingFileAppender
log4j.appender.logDailyFile.layout=org.apache.log4j.PatternLayout
log4j.appender.logDailyFile.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss a} [Thread:%t][Class:%c  Method: %M]%n%p:  %m%n
log4j.appender.logDailyFile.Threshold=warn
log4j.appender.logDailyFile.ImmediateFlush=TRUE
log4j.appender.logDailyFile.Append=TRUE
log4j.appender.logDailyFile.File=${log.store.path}/${project.name}_error_daily
log4j.appender.logDailyFile.DatePattern='_'yyyy-MM-dd-HH-mm'.log'
log4j.appender.logDailyFile.Encoding=utf-8

#输出到文件，文件按大小滚动
log4j.appender.logRollingFile=org.apache.log4j.RollingFileAppender
log4j.appender.logRollingFile.layout=org.apache.log4j.PatternLayout
log4j.appender.logRollingFile.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss a} [Thread:%t][Class:%c  Method: %M]%n%p:  %m%n
log4j.appender.logRollingFile.Threshold=DEBUG
log4j.appender.logRollingFile.ImmediateFlush=TRUE
log4j.appender.logRollingFile.Append=TRUE
log4j.appender.logRollingFile.File=${log.store.path}/${project.name}_debug_rolling.log
log4j.appender.logRollingFile.MaxFileSize=100mb
log4j.appender.logRollingFile.MaxBackupIndex=10
log4j.appender.logRollingFile.Encoding=utf-8


#将日志登录到MySQL数据库 
log4j.appender.logDB=org.apache.log4j.jdbc.JDBCAppender
log4j.appender.logDB.layout=org.apache.log4j.PatternLayout
log4j.appender.logDB.Driver=com.mysql.cj.jdbc.Driver
log4j.appender.logDB.URL=jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=true
log4j.appender.logDB.User=root
log4j.appender.logDB.Password=root
log4j.appender.logDB.Sql=INSERT INTO logging_event(project_name,thread_name,caller_filename,caller_class,caller_method,caller_line,level_string,formatted_message,gmt_create) values('${project.name}','%t','%F','%c','%M','%L','%p','%m','%d{yyyy-MM-dd HH:mm:ss}')
```

### 编写测试类
路径：test目录下的`cn.zzs.log4j`
```java
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
```
## log4j配置详解
Log4j中有三种组件，它们分别是 `Logger`、`Appender`和`Layout`。  

1. `Logger`：日志记录器，用来输出多种级别的日志信息。
Log4j中最顶级的记录器称为`rootLogger`，可通过`LoggerFactory.getLogger(class)`方法获得根记录器，其它`Logger`通过`LoggerFactory.getLogger(class)`方法获得。  

2. `Appender`：附加器，用来控制日志信息的输出目的地。  
Log4j中有多种`appender`，分别用来将日志信息输出到控制台、文件、数据库或邮件等。  
一个`Logger`可以关联多个`Appender`。  

3. `Layout`：定义日志的输出内容及格式。  

### Logger
语法如下：
```properties
log4j.rootLogger = [ level ] , appenderName, appenderName, …
```
level设定日志输出等级，记录器将只记录该等级以上的日志信息。  
level取值：`OFF`, `FATAL`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `ALL`。官方建议只使用四个级别，优先级从高到低分别是`ERROR`、`WARN`、`INFO`、`DEBUG`。  

### Appender
语法如下：
```properties
log4j.appender.[appenderName] = [appender.class]
log4j.appender.[appenderName].[option1] = [value1]
　　… 　　
log4j.appender.[appenderName].[optionN] = [valueN]
```
常用的[appender.class]有以下几种：  
```java
org.apache.log4j.ConsoleAppender//控制台 
org.apache.log4j.FileAppender//文件
org.apache.log4j.DailyRollingFileAppender//每天产生一个日志文件
org.apache.log4j.RollingFileAppender//文件大小到达指定尺寸时产生一个新的文件 
org.apache.log4j.WriterAppender//将日志信息以流格式发送到任意指定的地方
org.apache.log4j.jdbc.JDBCAppender//数据库
```
[option1] 是和具体appender类相关的属性，如果是ConsoleAppender，至少需要设置其layout属性；如果是FileAppender，需要设置layout和file属性。  

### Layout
语法如下：
```properties
log4j.appender.[appenderName].layout = [ layout class ] 
log4j.appender.[appenderName].layout.[option1] = value1 
　　… 
log4j.appender.[appenderName].layout.[optionN] = valueN
```
常用的[layout.class]有以下几种：  
```java
org.apache.log4j.HTMLLayout//以HTML表格形式布局  
org.apache.log4j.PatternLayout//可以灵活地指定布局模式,最常用  
org.apache.log4j.SimpleLayout//包含日志信息的级别和信息字符串  
org.apache.log4j.TTCCLayout//包含日志产生的时间、线程、类别等等信息
```
如果使用`PatternLayout`，还需设置`conversionPattern`，该属性类似于java中的格式说明字符串，其中包含的转义符规定了输出的内容和格式。  
```
%p：输出日志信息的优先级，即DEBUG，INFO，WARN，ERROR，FATAL。
%d：输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，如：%d{yyyy/MM/dd HH:mm:ss,SSS}。
%r：输出自应用程序启动到输出该log信息耗费的毫秒数。
%t：输出产生该日志事件的线程名。
%l：输出日志事件的发生位置，相当于%c.%M(%F:%L)的组合，包括类全名、方法、文件名以及在代码中的行数。例如：test.TestLog4j.main(TestLog4j.java:10)。
%c：输出日志信息所属的类目，通常就是所在类的全名。
%M：输出产生日志信息的方法名。
%F：输出日志消息产生时所在的文件名称。
%L:：输出代码中的行号。
%m:：输出代码中指定的具体日志信息。
%n：输出一个回车换行符，Windows平台为'rn'，Unix平台为'n'。
%x：输出和当前线程相关联的NDC(嵌套诊断环境)，尤其用到像java servlets这样的多客户多线程的应用中。
%%：输出一个'%'字符。
```

> 学习使我快乐！！
