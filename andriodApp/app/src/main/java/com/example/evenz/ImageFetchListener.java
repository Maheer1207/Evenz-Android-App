package com.example.evenz;

import java.util.ArrayList;

/**
 * interface for method after fetching all image paths.
 */
public interface ImageFetchListener {
    void onImagePathsFetched(ArrayList<String> imgPathList);
}
