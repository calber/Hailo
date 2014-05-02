package com.calber.hailo;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "com.calber.hailo";
    private RequestQueue queue;
    private LocationManager locationManager;
    HailoPagerAdapter pager;
    private ObjectMapper mapper;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        pager = new HailoPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pager);
        mapper = new ObjectMapper();
        queue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(queue, new BitmapCache(40));

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                searchRestaurants();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void searchRestaurants() {
        final ArrayList<Restaurant> items = new ArrayList<Restaurant>();
        
        Location lkl = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lkl.getLatitude(), lkl.getLongitude())).bearing(lkl.getBearing()).zoom(14)
                .tilt(45).build();
        pager.getMapFragment().getmMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        Uri.Builder uri = new Uri.Builder();
        uri.scheme("https");
        uri.authority("maps.googleapis.com");
        uri.path("maps/api/place/search/json");

        uri.appendQueryParameter("location", String.format(Locale.ENGLISH, "%f,%f", (float) lkl.getLatitude(), (float) lkl.getLongitude()));
        uri.appendQueryParameter("radius", "5000");
        uri.appendQueryParameter("types", "restaurant");
        uri.appendQueryParameter("sensor", "false");
        uri.appendQueryParameter("key", "AIzaSyDbZ1pjwKRtPaqodZBXmYl3WLFYz7Ex85A");

        String url = uri.build().toString();
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JsonNode root = mapper.readTree(response);
                    for (JsonNode node : root.path("results")) {
                        items.add(new Restaurant(node));
                    }
                } catch (Exception e) {
                    Log.e(TAG, this.getClass().toString(), e);
                }
                
                setMarkers(items);
                setListItems(items);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        }
        );
        queue.add(request);
    }

    private void setListItems(ArrayList<Restaurant> items) {
        ((HailoListFragment) pager.getListFragment()).setItems(items);
    }

    private void setMarkers(ArrayList<Restaurant> items) {
        for (Restaurant item : items) {
            pager.getMapFragment().getmMap().addMarker(new MarkerOptions().position(new LatLng(item.getLat(), item.getLng())).title(item
                    .getName()));
        }

    }


    public class HailoPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fm;
        private HailoMapFragment mf;
        private HailoListFragment lm;

        public HailoPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public HailoMapFragment getMapFragment() {
            return mf;
        }

        public HailoListFragment getListFragment() {
            return lm;
        }



        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    mf = new HailoMapFragment();
                    return mf;
                case 1:
                    lm =  new HailoListFragment(imageLoader);
                return lm;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "map";
                case 1:
                    return "list";
            }
            return null;
        }
    }

    public class BitmapCache extends LruCache implements ImageLoader.ImageCache {
        public BitmapCache(int maxSize) {
            super(maxSize);
        }

        @Override
        public Bitmap getBitmap(String url) {
            Log.d(TAG, "Get: " + url);
            return (Bitmap) get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            Log.d(TAG, "Put: " + url);
            put(url, bitmap);
        }
    }

}
