taskkill /fi "WINDOWTITLE eq Generating Data"
taskkill /fi "WINDOWTITLE eq Auto Restore"
taskkill /fi "WINDOWTITLE eq Backup Running*"

SET backupFiles=backup
SET notRestoredFiles=notRestored
SET restoredFiles=restored
SET toBeRestoredFiles=toBeRestored
SET deployFiles=deploy

ECHO "Cleaning existing directories"
rmdir ..\%backupFiles% ..\%notRestoredFiles% ..\%restoredFiles% ..\%toBeRestoredFiles% /s /q

ECHO "Making new directories"
mkdir ..\%backupFiles% ..\%notRestoredFiles% ..\%restoredFiles% ..\%toBeRestoredFiles%

START "Generating Data" groovy -cp %CD%\libs\* .\DataGenerator.groovy
ECHO "DataGenerator Started"

START "Auto Restore" groovy .\FileWatcher.groovy
ECHO "FileWatcher Started"

START "Backup Running" backup.bat -s localhost -f %CD%\..\%backupFiles%\test.out -u admin -p admin -t --fSize=500KB --fMax=1