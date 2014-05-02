package com.calber.hailo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by calber on 8/7/13.
 */


public class HailoMapFragment extends Fragment {

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfrag, null);

        FragmentManager sf = getFragmentManager();
        mMap = ((SupportMapFragment) sf.findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);

        return view;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

}
