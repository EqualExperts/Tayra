package com.ee.tayra.exploratory
	
import com.ee.tayra.command.backup.Backup
import com.ee.tayra.command.restore.Restore
import com.mongodb.DB
import com.mongodb.MongoClient
	
	class RestoreExploratoryTest extends ExploratoryResource {
	
	  private def context
	
	  def setupSpec() {
		addDataTo(src)
	  }
	
	  def cleanupSpec() {
		deleteDataFrom(src)
	  }
	
	  private void setup() {
		context = new Binding()
	  }
	
	  private void cleanup() {
		deleteDataFrom(tgt)
	  }
	
	  private void addDataTo(MongoClient mongoNode) {
		DB TayraDb = mongoNode.getDB("Tayra")
		run('db.profile.insert({name:"One"})', TayraDb)
		run('db.profile.insert({name:"Two"})', TayraDb)
	  }
	
	  private void deleteDataFrom(MongoClient mongoNode) {
		mongoNode.getDB("Tayra").dropDatabase()
	  }
	
	  def restoresOnNonAuthenticatedStandaloneWhenCredentialsAreGiven() {
		given:'Backup is taken'
			Binding backupContext = new Binding()
			backupContext.setVariable('args', ['-s', srcHOST, "--port=$srcPORT", '-f', backupFile])
			new Backup(backupContext).run()
	
		and:'arguments for restore contains -d, --port, -f, --sExclude, --sNs options'
			context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '-u', USERNAME, '-p', PASSWORD])
	
		when: 'backup runs with above args'
			new Restore(context).run()
	
		then: 'the target Node should contain documents having only ns as DL'
			tgt.getDatabaseNames().contains("Tayra")
			tgt.getDB("Tayra").getCollection("profile").count() == 2
	  }
}