package com.example.junyoung.culivebus.db;

import org.junit.After;
import org.junit.Before;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

abstract public class DbTest {
  protected CuLiveBusDb db;

  @Before
  public void initDb() {
    db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
      CuLiveBusDb.class).build();
  }

  @After
  public void closeDb() {
    db.close();
  }
}
