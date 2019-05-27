package com.example.junyoung.culivebus.repository;

import com.example.junyoung.culivebus.api.CumtdService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class BusStopRepositoryTest {
  private CumtdService cumtdService;

  @Rule
  public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  @Before
  public void init() {
    cumtdService = mock(CumtdService.class);
  }

  @Test
  public void test() throws Exception {

  }
}
