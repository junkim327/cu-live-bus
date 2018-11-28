package com.example.junyoung.culivebus;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class ArrayListTest {
  @Test
  public void swapElements_isCorrect() {
    ArrayList<Integer> intList = new ArrayList<>();
    intList.add(3);
    intList.add(5);
    intList.add(7);
    Collections.swap(intList, 0, 2);

    assertEquals(new BigDecimal(intList.get(0)), new BigDecimal(7));
  }
}
