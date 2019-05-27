package com.example.junyoung.culivebus.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import timber.log.Timber;

import com.example.junyoung.culivebus.util.AppExecutors;
import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.api.ApiResponse;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.db.CuLiveBusDb;
import com.example.junyoung.culivebus.db.dao.ChangesetDao;
import com.example.junyoung.culivebus.db.dao.ShapeDao;
import com.example.junyoung.culivebus.db.entity.Changeset;
import com.example.junyoung.culivebus.db.entity.Shape;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.response.ShapeResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RouteRepository {
  private final CuLiveBusDb db;
  private final ShapeDao shapeDao;
  private final ChangesetDao changesetDao;
  private final CumtdService cumtdService;
  private final AppExecutors appExecutors;

  @Inject
  public RouteRepository(CuLiveBusDb db, ShapeDao shapeDao, ChangesetDao changesetDao,
                         CumtdService cumtdService, AppExecutors appExecutors) {
    this.db = db;
    this.shapeDao = shapeDao;
    this.changesetDao = changesetDao;
    this.cumtdService = cumtdService;
    this.appExecutors = appExecutors;
  }

  public LiveData<Resource<List<Shape>>> loadShape(String shapeId) {
    return new NetworkBoundResource<List<Shape>, ShapeResponse>(appExecutors) {
      @Override
      protected void saveCallResult(@NonNull ShapeResponse item) {
        for (Shape shape : item.getShapes()) {
          shape.setShapeId(shapeId);
        }

        db.beginTransaction();
        try {
          shapeDao.insertAll(item.getShapes());
          changesetDao.insertChangeset(new Changeset(shapeId, item.getChangesetId()));
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }

        Timber.d("Saved shapeResponse to the database");
      }

      @Override
      protected boolean shouldFetch(@Nullable List<Shape> data) {
        String changesetId = changesetDao.loadChangeset(shapeId).getValue();
        if (changesetId != null) {
          ApiResponse<ShapeResponse> shapeResponse =
            cumtdService.getShapes(Constants.API_KEY, shapeId, changesetId).getValue();
          if (shapeResponse.isSuccessful() && shapeResponse.body != null) {
            Timber.d("Found changeset_id in the database");
            return shapeResponse.body.isNewChangeset();
          }
        }

        if (data == null || data.isEmpty()) {
          return true;
        }

        return false;
      }

      @NonNull
      @Override
      protected LiveData<List<Shape>> loadFromDb() {
        return shapeDao.loadShape(shapeId);
      }

      @NonNull
      @Override
      protected LiveData<ApiResponse<ShapeResponse>> createCall() {
        return cumtdService.getShapes(Constants.API_KEY, shapeId, null);
      }
    }.asLiveData();
  }
}
