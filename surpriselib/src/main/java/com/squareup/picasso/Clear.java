package com.squareup.picasso;

public class Clear {

    public static void clearCache(Picasso p) {
        p.cache.clear();
    }
}