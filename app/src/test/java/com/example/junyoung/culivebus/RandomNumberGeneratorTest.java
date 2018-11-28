package com.example.junyoung.culivebus;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class RandomNumberGeneratorTest {
  @Test
  public void generate_random_integer_number_between_1_and_3_isCorrect() throws Exception {
    int max = 3;
    int min = 1;
    Random random = new Random();
    int generatedNum = random.nextInt(max - min + 1) + min;
    System.out.print(generatedNum);
    assertTrue(generatedNum == 1 || generatedNum == 2 || generatedNum == 3);
  }
}
