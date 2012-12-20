import org.apache.log4j.Layout;
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.apache.log4j.RollingFileAppender
import org.apache.log4j.Level

def layout = new PatternLayout()
def appender = new RollingFileAppender(layout, 'test.log', false)
appender.maxBackupIndex =  2
appender.maxFileSize = '1KB'
Logger logger = Logger.getLogger("test")
println "Appenders -> $logger.allAppenders"
//for (e in logger.allAppenders) {
//	println e.name
//}
logger.removeAllAppenders()
logger.setLevel(Level.INFO)
logger.addAppender(appender)

1000.times {
	logger.info('{}')
}
