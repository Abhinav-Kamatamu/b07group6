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
 * Catalog screen showing only saved artifacts. Uses {@link CatalogType#SAVED} to indicate that
 * {@link CatalogFragment} should load only the saved artifact list for the current user.
 */
public class SavedFragment extends CatalogFragment {
    /** Creates a new saved-artifacts catalog fragment. */
    public SavedFragment() {
        super(CatalogType.SAVED);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }
}