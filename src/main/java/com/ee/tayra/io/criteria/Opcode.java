package com.ee.tayra.io.criteria;

public enum Opcode {

insert {
  @Override
  public String getOpCode() {
    return "i";
  }
},
update {
  @Override
  public String getOpCode() {
    return "u";
  }
},
remove {
  @Override
  public String getOpCode() {
    return "d";
  }
},
No_Op {
  @Override
  public String getOpCode() {
    return "NO_OP";
  }
};

  public static Opcode map(final String operation) {
    if (operation.equalsIgnoreCase("insert")) {
      return Opcode.insert;
    }
    if (operation.equalsIgnoreCase("update")) {
      return Opcode.update;
    }
    if (operation.equalsIgnoreCase("remove")) {
      return Opcode.remove;
    }
    return Opcode.No_Op;
  }

  abstract  String getOpCode();
}
