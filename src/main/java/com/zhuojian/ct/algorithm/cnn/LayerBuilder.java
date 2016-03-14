package com.zhuojian.ct.algorithm.cnn;

import edu.hitsz.c102c.cnn.Layer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jql on 2016/3/11.
 */
public class LayerBuilder {
    private List<Layer> mLayers = new ArrayList<>();

    public LayerBuilder() {
    }

    public LayerBuilder(Layer layer) {
        mLayers.add(layer);
    }

    public LayerBuilder addLayer(Layer layer) {
        mLayers.add(layer);
        return this;
    }

    public List<Layer> getmLayers() {
        return mLayers;
    }
}