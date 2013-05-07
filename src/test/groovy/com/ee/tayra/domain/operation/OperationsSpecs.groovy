package com.ee.tayra.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

public class OperationsSpecs extends RequiresMongoConnection {

  def operations

  def setup() {
    operations = new Operations(standalone)
  }

  def producesCorrectOperationInstances() {

    expect: 'operations to be creating appropriate instances'
      assert operations.get(opCode), instanceOf(klass)

    where: 'create, insert, update and delete opcodes are provided'
        opCode | klass
        'c'    | DefaultSchemaOperation
        'i'    | InsertDocument
        'u'    | UpdateDocument
        'd'    | DeleteDocument
  }

  def producesaNoOperationWhenItCannotIdentifyOperationCode() throws Exception {
    when: 'unidentifiable opcode is provided'
      Operation operation = operations.get('unindentifiableOpcode')

    then: 'No operation is performed'
      operation == Operation.NO_OP
  }


}
