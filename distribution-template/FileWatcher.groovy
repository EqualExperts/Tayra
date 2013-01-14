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

Path backupDir = Paths.get("D:/MongoBeaverDataFiles/backup")
Path targetDir = Paths.get("D:/MongoBeaverDataFiles/toBeRestored")

Map<WatchKey, Path> keyMap = new HashMap<WatchKey, Path>()

try {
	FileSystem fileSystem = FileSystems.getDefault();
	WatchService watchService = fileSystem.newWatchService()

	keyMap.put(backupDir.register(watchService, ENTRY_CREATE), backupDir)
	keyMap.put(targetDir.register(watchService, ENTRY_CREATE), targetDir)

	WatchKey watchKey

	while(true) {
		watchKey = watchService.take();
		final Path eventDir = keyMap.get(watchKey);
		for (final WatchEvent<?> event : watchKey.pollEvents()) {
			println (eventDir.toString() + ": " + event.kind().toString() + ": " + event.context().toString());

			if(eventDir == backupDir) {
				File backup = new File("D:/MongoBeaverDataFiles/backup/test.out.1")
				if (backup.exists()){
					targetMover('notRestored')
					backup.renameTo("D:/MongoBeaverDataFiles/toBeRestored/test.out.1")
				}
			}

			if(eventDir == targetDir) {
				"D:/MongoBeaverDataFiles/distributions/executeRestore.bat".execute().text
				sleep 3000
				targetMover('restored')
			}
		}
		watchKey.reset()
	}
} catch (Exception e) {
	System.out.println("Error: " + e.toString());
}

def targetMover(String destination) {
	File target = new File("D:/MongoBeaverDataFiles/toBeRestored/test.out.1")
	if(target.exists()){
		String suffix = new Date().toString().replaceAll(" ", "_").replaceAll(":", "_")
		String fileName = "D:/MongoBeaverDataFiles/" + destination + "/test.out.1" + suffix
		target.renameTo(fileName)
	}
}