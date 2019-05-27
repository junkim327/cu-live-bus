package com.example.junyoung.culivebus;

import android.content.Context;
import android.location.Location;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
  private double latitude;
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        FusedLocationProviderClient fusedLocationClient;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
        fusedLocationClient.getLastLocation()
          .addOnSuccessListener(
            new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                  System.out.print(location.getLatitude());
                    latitude = location.getLatitude();
                }
            });


        Thread.sleep(2000);
        assertEquals(345, latitude, 0.001);
    }
}
