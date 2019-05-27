package com.example.junyoung.culivebus.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "changesetTable")
public class Changeset {
  @NonNull
  @PrimaryKey
  private String keyword;
  private String changesetId;

  public Changeset(String keyword, String changesetId) {
    this.keyword = keyword;
    this.changesetId = changesetId;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public String getChangesetId() {
    return changesetId;
  }

  public void setChangesetId(String changesetId) {
    this.changesetId = changesetId;
  }
}
