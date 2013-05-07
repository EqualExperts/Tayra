package com.ee.tayra.io.listener.timestamp

import spock.lang.Specification

class FileBasedTimestampRepositorySpecs extends Specification {

  private FileBasedTimestampRepository repository
  private static File testFile
  private PrintWriter mockConsole
  private String tstamp = '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'

  def setup() {
    testFile = new File('testTimestamp.out')
    mockConsole = Mock(PrintWriter)
    repository = new FileBasedTimestampRepository(testFile,mockConsole)
  }

  def cleanupSpec() {
    testFile.delete()
  }

  def itSavesToAFile() {
    when:'timestamp is saved to file'
      repository.save(tstamp)

    then:'file should be created with the given timestamp'
      testFile.exists()
      testFile.text == tstamp
  }

  def itRetrievesFromFile() {
    when:'timestamp is read from file'
      def retrievedTimestamp = repository.retrieve()

    then:'it should return correct timestamp'
      retrievedTimestamp == tstamp
  }

  def itReturnsEmptyTimestampIfFileDoesNotExist() {
    given:'timestamp file given does not exist'
      File nonExistingFile = new File('nonExistingFile')
      repository = new FileBasedTimestampRepository(nonExistingFile, mockConsole)

    when:'timestamp is read from file'
      def retrievedTimestamp = repository.retrieve()

    then:'it should return empty timestamp'
      retrievedTimestamp == ''
  }

  def itNotifiesWhenNoTimestampIsPresentInFile() {
    given:'timestamp file is empty'
      File emptyFile = new File('emptyFile.out')
      FileWriter writer = new FileWriter(emptyFile)
      writer.write('')

    and:'timestamp repository with the file'
      repository = new FileBasedTimestampRepository(emptyFile, mockConsole)

    when:'timestamp is read from file'
      repository.retrieve()

    then:'it should output message to console'
      1 * mockConsole.println("Unable to read " + emptyFile.getName())

    cleanup:'delete file'
      writer.close()
      emptyFile.delete()
  }
}
