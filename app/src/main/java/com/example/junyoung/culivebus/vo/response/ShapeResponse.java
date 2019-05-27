package com.example.junyoung.culivebus.vo.response;

import com.example.junyoung.culivebus.db.entity.Shape;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShapeResponse {
  @SerializedName("changeset_id")
  private String changesetId;
  @SerializedName("new_changeset")
  private boolean isNewChangeset;
  private List<Shape> shapes;

  public String getChangesetId() {
    return changesetId;
  }

  public boolean isNewChangeset() {
    return isNewChangeset;
  }

  public List<Shape> getShapes() {
    return shapes;
  }
}
