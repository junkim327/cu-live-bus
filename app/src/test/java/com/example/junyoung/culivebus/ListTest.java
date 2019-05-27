package com.example.junyoung.culivebus;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ListTest {
  @Test
  public void swapElements_isCorrect() {
    ArrayList<Integer> intList = new ArrayList<>();
    intList.add(3);
    intList.add(5);
    intList.add(7);
    Collections.swap(intList, 0, 2);

    assertEquals(new BigDecimal(intList.get(0)), new BigDecimal(7));
  }

  @Test
  public void sublist_isCorrect() {
    List<Integer> integerList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      integerList.add(i);
    }

    List<Integer> sublist = integerList.subList(0, 5);
    for (int i : sublist) {
      System.out.println(i);
    }
  }
}
