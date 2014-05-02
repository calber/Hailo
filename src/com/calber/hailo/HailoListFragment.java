package com.calber.hailo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by calber on 8/7/13.
 */
public class HailoListFragment extends Fragment {
    RestaurantAdapter adapter;
    LayoutInflater inflater;
    ImageLoader imageLoader;

    public HailoListFragment(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list, null);
        this.inflater = inflater;

        adapter = new RestaurantAdapter();
        ((ListView) view.findViewById(R.id.list)).setAdapter(adapter);

        return view;
    }

    public RestaurantAdapter getAdapter() {
        return adapter;
    }

    public void setItems(ArrayList<Restaurant> items) {
        adapter.setItems(items);
    }

    private class RestaurantAdapter extends BaseAdapter {
        ArrayList<Restaurant> items = new ArrayList<Restaurant>();

        public void setItems(ArrayList<Restaurant> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.row, parent, false);
            }

            ((TextView) view.findViewById(R.id.name)).setText(items.get(position).getName());
            ((TextView) view.findViewById(R.id.vicinity)).setText(items.get(position).getVicinity());

            ((NetworkImageView) view.findViewById(R.id.image)).setImageUrl(items.get(position).getIcon(), imageLoader);
            return view;
        }
    }
}
