package com.ee.beaver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class GreeterSpecs {
  
  @Test
  public void itGreets() {
      Greeter greeter = new Greeter();
      assertThat(greeter.greet("me"), is("Hello me"));
  }
}
