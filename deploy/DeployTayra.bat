REM taskkill /fi "WINDOWTITLE eq Generating Data"
REM taskkill /fi "WINDOWTITLE eq Auto Restore"
taskkill /fi "WINDOWTITLE eq Backup Running*"

SET backupFiles=backup
SET notRestoredFiles=notRestored
SET restoredFiles=restored
SET toBeRestoredFiles=toBeRestored
SET deployFiles=deploy

SET backupFile=test.out
SET target=localhost
SET target_port=27020
SET source=localhost
SET source_port=27017
SET username=admin
SET password=admin

ECHO "Cleaning existing directories"
rmdir ..\%backupFiles% ..\%notRestoredFiles% ..\%restoredFiles% ..\%toBeRestoredFiles% /s /q

ECHO "Making new directories"
mkdir ..\%backupFiles% ..\%notRestoredFiles% ..\%restoredFiles% ..\%toBeRestoredFiles%

START "Generating Data" groovy -cp %CD%\libs\* .\DataGenerator.groovy --source=%source% --port=%source_port% -u %username% -p %password%
ECHO "DataGenerator Started"

START "Auto Restore" groovy .\FileWatcher.groovy -f %backupFile% --target=%target% --port=%target_port% -u %username% -p %password%
ECHO "FileWatcher Started"

START "Backup Running" backup.bat -s %source% --port=%source_port% -f %CD%\..\%backupFiles%\%backupFile% -u %username% -p %password% -t --fSize=500KB --fMax=1