package org.adrianvictor.geleia.fragments.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.adrianvictor.geleia.databinding.FragmentMiniPlayerBinding;
import org.adrianvictor.geleia.model.Song;
import org.adrianvictor.geleia.util.PreferenceUtil;
import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.helper.MusicPlayerRemote;
import org.adrianvictor.geleia.helper.MusicProgressViewUpdateHelper;
import org.adrianvictor.geleia.helper.PlayPauseButtonOnClickHandler;
import org.adrianvictor.geleia.fragments.AbsMusicServiceFragment;
import org.adrianvictor.geleia.util.ThemeUtil;
import org.adrianvictor.geleia.views.PlayPauseDrawable;

public class MiniPlayerFragment extends AbsMusicServiceFragment implements MusicProgressViewUpdateHelper.Callback {
    private FragmentMiniPlayerBinding binding;

    private PlayPauseDrawable miniPlayerPlayPauseDrawable;

    private MusicProgressViewUpdateHelper progressViewUpdateHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressViewUpdateHelper = new MusicProgressViewUpdateHelper(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMiniPlayerBinding.inflate(inflater);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setOnTouchListener(new FlingPlayBackController(getActivity()));
        setUpMiniPlayer();
    }

    private void setUpMiniPlayer() {
        setUpPlayPauseButton();
        int accentColor = PreferenceUtil.getInstance(requireActivity()).getAccentColor();

        binding.progressBar.setSupportProgressTintList(ColorStateList.valueOf(accentColor));
        binding.progressBar.setSupportIndeterminateTintList(ColorStateList.valueOf(accentColor));
    }

    private void setUpPlayPauseButton() {
        miniPlayerPlayPauseDrawable = new PlayPauseDrawable(requireActivity());

        binding.miniPlayerPlayPauseButton.setImageDrawable(miniPlayerPlayPauseDrawable);
        binding.miniPlayerPlayPauseButton.setColorFilter(ThemeUtil.getColorResource(requireActivity(), R.attr.iconColor), PorterDuff.Mode.SRC_IN);
        binding.miniPlayerPlayPauseButton.setOnClickListener(new PlayPauseButtonOnClickHandler());
    }

    private void updateSongTitle() {
        Song song = MusicPlayerRemote.getCurrentSong();
        if (song != null) {
            binding.miniPlayerTitle.setText(song.title);
        }
    }

    @Override
    public void onServiceConnected() {
        updateSongTitle();
        updatePlayPauseDrawableState(false);
    }

    @Override
    public void onPlayMetadataChanged() {
        updateSongTitle();
    }

    @Override
    public void onPlayStateChanged() {
        updatePlayPauseDrawableState(true);
    }

    @Override
    public void onUpdateProgressViews(int progress, int total) {
        binding.progressBar.setIndeterminate(MusicPlayerRemote.isLoading());
        binding.progressBar.setMax(total);
        binding.progressBar.setProgress(progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressViewUpdateHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        progressViewUpdateHelper.stop();
    }

    private static class FlingPlayBackController implements View.OnTouchListener {
        GestureDetector flingPlayBackController;

        public FlingPlayBackController(Context context) {
            flingPlayBackController = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (Math.abs(velocityX) > Math.abs(velocityY)) {
                        if (velocityX < 0) {
                            MusicPlayerRemote.playNextSong();
                            return true;
                        } else if (velocityX > 0) {
                            MusicPlayerRemote.playPreviousSong();
                            return true;
                        }
                    }

                    return false;
                }
            });
        }

        @Override
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View v, MotionEvent event) {
            return flingPlayBackController.onTouchEvent(event);
        }
    }

    protected void updatePlayPauseDrawableState(boolean animate) {
        if (MusicPlayerRemote.isPlaying()) {
            miniPlayerPlayPauseDrawable.setPause(animate);
        } else {
            miniPlayerPlayPauseDrawable.setPlay(animate);
        }
    }
}
