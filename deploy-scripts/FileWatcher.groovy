import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

Properties config = new Properties() 
config.load(new FileInputStream("deployment.properties"))

backupDir = Paths.get(config.getProperty("backupDir"))
restoreDir = Paths.get(config.getProperty("restoreDir"))
restoreCompleteDir = Paths.get(config.getProperty("restoreCompleteDir"))
restoreFailedDir = Paths.get(config.getProperty("restoreFailedDir"))

def cli = new CliBuilder(usage:'FileWatcher --target=<MongoDB> [--port=number] -f <file> [-u username] [-p password]')
cli.with {
  _  args:1, argName: 'MongoDB Host', longOpt:'target', 'Target IP/Host (=localhost or ip)', optionalArg:true, required: true
  _  args:1, argName: 'port', longOpt:'port', 'Target MongoDB Port', optionalArg:true
  f  args:1, argName: 'file', longOpt:'file', 'REQUIRED, File To Record Oplog To', required: true
  u  args:1, argName: 'username', longOpt:'username', 'username for authentication', optionalArg:true
  p  args:1, argName: 'password', longOpt:'password', 'password for authentication', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
  return
}
  
if(options.arguments()){
  console.println "Cannot Understand ${options.arguments()}"
  cli.usage()
  return
}
fileName = options.f
destMongo = options.target

destPort = 27021
username = ""
password = ""

if(options.port) {
  destPort = Integer.parseInt(options.port)
}

if(options.username) {
  username = options.username
}

if(options.password) {
  password = options.password
}

errorFile = 'fileWatcherError.txt'

Map<WatchKey, Path> keyMap = new HashMap<WatchKey, Path>()

try {
	FileSystem fileSystem = FileSystems.getDefault();
	WatchService watchService = fileSystem.newWatchService()

	keyMap.put(backupDir.register(watchService, ENTRY_CREATE), backupDir)
	keyMap.put(restoreDir.register(watchService, ENTRY_CREATE), restoreDir)

	WatchKey watchKey

	while(true) {
		watchKey = watchService.take();
		final Path eventDir = keyMap.get(watchKey);
		File backup
		
		for (final WatchEvent<?> event : watchKey.pollEvents()) {
			println (eventDir.toString() + ": " + event.kind().toString() + ": " + event.context().toString());
			
			if(eventDir == backupDir) {
				backup = new File("$backupDir/$fileName.1")
				if (backup.exists()){
					targetMover(restoreFailedDir, backup.getName())
					backup.renameTo("$restoreDir/$fileName.1")
				}
			}

			if(eventDir == restoreDir) {
				println "\n\nstarting restore"
				String restore = "restore.bat -d $destMongo --port=$destPort -f $restoreDir/$fileName.1 -u $username -p $password"
				println "\n\n" + restore.execute().text
				println "restore complete"
				println "\n\n"
				targetMover(restoreCompleteDir, "$fileName.1")
				sleep 1000
			}
		}
		watchKey.reset()
	}
} catch (Exception e) {
	File errorFile = new File("$errorFile")
	errorFile.withWriter { it.write e.getStackTrace().toString()}
}

def targetMover(Path destination, String newFileName) {
	File target = new File("$restoreDir/$newFileName")
	if(target.exists()){
		println "MOVING FILE $newFileName TO: $destination"
		String suffix = new Date().toString().replaceAll(" ", "_").replaceAll(":", "_")
		String fileName = "$destination/$newFileName$suffix"
		target.renameTo(fileName)
	}
}