if "%1"=="" (SET DEPLOY_HOME=D:\tayra\data_files) else (SET DEPLOY_HOME=%1)

SET backupFiles=%DEPLOY_HOME%\backup
SET notRestoredFiles=%DEPLOY_HOME%\notRestored
SET restoredFiles=%DEPLOY_HOME%\restored
SET toBeRestoredFiles=%DEPLOY_HOME%\toBeRestored

SET backupFile=test.out
SET target=localhost
SET target_port=27020
SET source=localhost
SET source_port=27017
SET username=admin
SET password=admin

ECHO "Cleaning existing directories"

rmdir %backupFiles% %notRestoredFiles% %restoredFiles% %toBeRestoredFiles% /S /Q

ECHO "Making new directories"
mkdir %backupFiles% %notRestoredFiles% %restoredFiles% %toBeRestoredFiles%

START "Generator" groovy -cp %CD%\libs\* .\DataGenerator.groovy --source=%source% --port=%source_port% -u %username% -p %password% --feedInterval=1
ECHO "DataGenerator Started"

START "Watcher" groovy .\FileWatcher.groovy -f %backupFile% --target=%target% --port=%target_port% -u %username% -p %password% --watch=%DEPLOY_HOME%
ECHO "FileWatcher Started"

START "Backup" backup.bat -s %source% --port=%source_port% -f %backupFiles%\%backupFile% -u %username% -p %password% -t --fSize=5MB --fMax=1