package com.example.francesca.labyrinth;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.example.francesca.labyrinth.data.RetainedData;

/**
 * Created by francesca on 04/02/2016.
 */
public class RetainedFragment extends Fragment {
    // data object we want to retain
    private RetainedData data;
    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }
    public void setData(RetainedData data) {
        this.data = data;
    }
    public RetainedData getData() {
        return data;
    }
}
