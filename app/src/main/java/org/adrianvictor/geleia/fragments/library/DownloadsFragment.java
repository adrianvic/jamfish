package org.adrianvictor.geleia.fragments.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.adrianvictor.geleia.R;

public class DownloadsFragment extends Fragment {
    public static DownloadsFragment newInstance() {
        return new DownloadsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.downloads_fragment, container, false);
    }

}
