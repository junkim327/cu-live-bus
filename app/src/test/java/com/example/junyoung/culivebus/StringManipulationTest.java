package com.example.junyoung.culivebus;

import org.junit.Test;

public class StringManipulationTest {
  @Test
  public void trim_address_isSuccessful() throws Exception {
    String address = "jax, james, Urbana, IL 61801, USA";
    int i = address.lastIndexOf(',');
    int thirdCommanOccurrenceIndex = address.lastIndexOf(',', address.lastIndexOf(',', i - 1) - 1);
    String jax = null;
    if (thirdCommanOccurrenceIndex == -1) {
      jax = address.substring(0, i);
    } else {
      jax = address.substring(thirdCommanOccurrenceIndex + 2, i);
    }
    System.out.print(jax);
  }
}
