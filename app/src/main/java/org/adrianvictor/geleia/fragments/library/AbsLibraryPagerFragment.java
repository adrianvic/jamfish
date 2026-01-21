package org.adrianvictor.geleia.fragments.library;

import org.adrianvictor.geleia.fragments.AbsMusicServiceFragment;
import org.adrianvictor.geleia.fragments.main.LibraryFragment;

public class AbsLibraryPagerFragment extends AbsMusicServiceFragment {
    public LibraryFragment getLibraryFragment() {
        return (LibraryFragment) getParentFragment();
    }
}
