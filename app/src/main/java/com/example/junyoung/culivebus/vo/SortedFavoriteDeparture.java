package com.example.junyoung.culivebus.vo;

import com.example.junyoung.culivebus.db.entity.FavoriteStop;

public class SortedFavoriteDeparture {
  private boolean isDeparture;
  private SortedDeparture sortedDeparture;
  private FavoriteStop favoriteStop;

  public SortedFavoriteDeparture(FavoriteStop favoriteStop) {
    this.isDeparture = false;
    this.favoriteStop = favoriteStop;
  }

  public SortedFavoriteDeparture(SortedDeparture sortedDeparture) {
    this.isDeparture = true;
    this.sortedDeparture = sortedDeparture;
  }

  public boolean isDeparture() {
    return isDeparture;
  }

  public void setDeparture(boolean departure) {
    isDeparture = departure;
  }

  public SortedDeparture getSortedDeparture() {
    return sortedDeparture;
  }

  public void setSortedDeparture(SortedDeparture sortedDeparture) {
    this.sortedDeparture = sortedDeparture;
  }

  public FavoriteStop getFavoriteStop() {
    return favoriteStop;
  }

  public void setFavoriteStop(FavoriteStop favoriteStop) {
    this.favoriteStop = favoriteStop;
  }
}