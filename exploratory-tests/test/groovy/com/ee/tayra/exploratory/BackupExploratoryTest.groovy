package com.ee.tayra.exploratory

import com.ee.tayra.command.backup.Backup

class BackupExploratoryTest extends RequiresExploratoryTestSupport {

  private def context

  def setup() {
    context = new Binding()
  }

  def excludesDocumentsBelongingToMultipleNamespaces() {
    given:'arguments contains -s, --port, -f, --sExclude options'
      context.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile, '--sExclude', '--sNs=DL,EELab.thing'])

    when: 'backup runs with above args'
      new Backup(context).run()
      def backupFileContent = new File(backupFile)

    then: 'the backupFile should contain documents having only ns as DL'
      backupFileContent.eachLine { line ->
        !line.contains('"ns" : "DL.')
        !line.contains('"ns" : "EELab.thing"')
      }
  }
}
