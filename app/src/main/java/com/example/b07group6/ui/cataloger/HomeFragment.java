package com.example.b07group6.ui.cataloger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.b07group6.R;
import com.example.b07group6.ui.cataloger.base.CatalogFragment;

/**
 * Catalog screen showing all artifacts. Uses {@link CatalogType#HOME} to indicate that
 * {@link CatalogFragment} should load the full artifact list.
 */
public class HomeFragment extends CatalogFragment {
    /** Creates a new home catalog fragment configured to display all artifacts. */
    public HomeFragment() {
        super(CatalogType.HOME);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}