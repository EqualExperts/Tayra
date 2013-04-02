import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.File
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

backupDir = Paths.get("../backup")
restoreDir = Paths.get("../toBeRestored")
restoreCompleteDir = Paths.get("../restored")
restoreFailedDir = Paths.get("../notRestored")
fileName = args[0]
destMongo = args[1]
destPort = args[2]
username = args[3]
password = args[4]
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
		int backupFileCounter = 1
		File backup
		
		for (final WatchEvent<?> event : watchKey.pollEvents()) {
			println (eventDir.toString() + ": " + event.kind().toString() + ": " + event.context().toString());
			
			if(eventDir == backupDir) {
				backup = new File("$backupDir/$fileName.1")
				if (backup.exists()){
					targetMover(restoreFailedDir, backup.getName())
					backup.renameTo("$restoreDir/$fileName.$backupFileCounter")
					//backupFileCounter++
				}
			}

			if(eventDir == restoreDir) {
				println "\n\nstarting restore"
				String restore = "restore.bat -d $destMongo --port=$destPort -f $restoreDir/$fileName.$backupFileCounter -u $username -p $password"
				println "\n\n" + restore.execute().text
				println "restore complete"
				println "\n\n"
				targetMover(restoreCompleteDir, "$fileName.$backupFileCounter")
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