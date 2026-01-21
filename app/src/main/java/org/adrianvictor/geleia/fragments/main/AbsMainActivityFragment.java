package org.adrianvictor.geleia.fragments.main;

import androidx.fragment.app.Fragment;

import org.adrianvictor.geleia.activities.MainActivity;

public abstract class AbsMainActivityFragment extends Fragment {
    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
