package com.ee.tayra.exploratory

import com.ee.tayra.command.backup.Backup

class BackupExploratoryTest extends ExploratoryTestSupport {

  private def context

  def setup() {
    context = new Binding()
  }

  def excludesDocumentsbelongingToMultipleNsWithSExcludeWhileBackup() {
    given:'arguments contains -s, -f, -u and -p options'
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


//  private void addDataTo(MongoClient mongoNode) {
//    DB DLDb = mongoNode.getDB("DL")
//    DB TayraDb = mongoNode.getDB("Tayra")
//    DB EELabDb = mongoNode.getDB("EELab")
////    insert(getDocument('name', 'One'), DLDb.getCollection('profile'))
////    insert(getDocument('name', 'One'), DLDb.getCollection('thing'))
////    insert(getDocument('name', 'One'), TayraDb.getCollection('profile'))
////    insert(getDocument('name', 'One'), TayraDb.getCollection('thing'))
////    insert(getDocument('name', 'One'), EELabDb.getCollection('profile'))
////    insert(getDocument('name', 'One'), EELabDb.getCollection('thing'))
//    run('db.profile.insert({name:"One"})', DLDb)
//    run('db.thing.insert({name:"One"})', DLDb)
//    run('db.profile.insert({name:"One"})', TayraDb)
//    run('db.thing.insert({name:"One"})', TayraDb)
//    run('db.profile.insert({name:"One"})', EELabDb)
//    run('db.thing.insert({name:"One"})', EELabDb)
//  }
//
////  private getDocument(String field, String value) {
////    def documentOne = new BasicDBObjectBuilder()
////        .start()
////          .add(field, value)
////        .get()
////  }