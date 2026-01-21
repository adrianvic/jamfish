package org.adrianvictor.geleia.fragments.player.card;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import org.adrianvictor.geleia.util.ThemeUtil;
import org.adrianvictor.geleia.service.QueueManager;
import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.helper.MusicPlayerRemote;
import org.adrianvictor.geleia.databinding.FragmentCardPlayerPlaybackControlsBinding;
import org.adrianvictor.geleia.helper.MusicProgressViewUpdateHelper;
import org.adrianvictor.geleia.helper.PlayPauseButtonOnClickHandler;
import org.adrianvictor.geleia.interfaces.base.SimpleOnSeekbarChangeListener;
import org.adrianvictor.geleia.fragments.AbsMusicServiceFragment;
import org.adrianvictor.geleia.util.MusicUtil;
import org.adrianvictor.geleia.views.PlayPauseDrawable;

public class CardPlayerPlaybackControlsFragment extends AbsMusicServiceFragment implements MusicProgressViewUpdateHelper.Callback {

    public FragmentCardPlayerPlaybackControlsBinding binding;

    private PlayPauseDrawable playerFabPlayPauseDrawable;

    private int lastPlaybackControlsColor;
    private int lastDisabledPlaybackControlsColor;

    private MusicProgressViewUpdateHelper progressViewUpdateHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressViewUpdateHelper = new MusicProgressViewUpdateHelper(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCardPlayerPlaybackControlsBinding.inflate(inflater);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpMusicControllers();
        updateProgressTextColor();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    @Override
    public void onServiceConnected() {
        updatePlayPauseDrawableState(false);
        updateRepeatState();
        updateShuffleState();
    }

    @Override
    public void onPlayStateChanged() {
        updatePlayPauseDrawableState(true);
    }

    @Override
    public void onRepeatModeChanged() {
        updateRepeatState();
    }

    @Override
    public void onShuffleModeChanged() {
        updateShuffleState();
    }

    public void setDark(boolean dark) {
        if (dark) {
            lastPlaybackControlsColor = ThemeUtil.getSecondaryTextColor(requireActivity(), true);
            lastDisabledPlaybackControlsColor = ThemeUtil.getColorAlpha(requireActivity(), R.color.color_text_secondary_light, 180);
        } else {
            lastPlaybackControlsColor = ThemeUtil.getPrimaryTextColor(requireActivity(), false);
            lastDisabledPlaybackControlsColor = ThemeUtil.getColorAlpha(requireActivity(), R.color.color_text_primary_dark, 180);
        }

        updateRepeatState();
        updateShuffleState();
        updatePrevNextColor();
        updateProgressTextColor();
    }

    private void setUpPlayPauseFab() {
        playerFabPlayPauseDrawable = new PlayPauseDrawable(requireActivity());

        binding.playerPlayPauseFab.setImageDrawable(playerFabPlayPauseDrawable);
        binding.playerPlayPauseFab.setColorFilter(ThemeUtil.getPrimaryTextColor(requireContext(), Color.WHITE), PorterDuff.Mode.SRC_IN);
        binding.playerPlayPauseFab.setOnClickListener(new PlayPauseButtonOnClickHandler());
        binding.playerPlayPauseFab.post(() -> {
            binding.playerPlayPauseFab.setPivotX(binding.playerPlayPauseFab.getWidth() / 2f);
            binding.playerPlayPauseFab.setPivotY(binding.playerPlayPauseFab.getHeight() / 2f);
        });
    }

    protected void updatePlayPauseDrawableState(boolean animate) {
        if (MusicPlayerRemote.isPlaying()) {
            playerFabPlayPauseDrawable.setPause(animate);
        } else {
            playerFabPlayPauseDrawable.setPlay(animate);
        }
    }

    public void updateBufferingIndicatorColor(int color) {
        binding.playerBufferingIndicator.setProgressBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.transparent)));
        binding.playerBufferingIndicator.setIndeterminateTintList(ColorStateList.valueOf(color));
    }

    private void setUpMusicControllers() {
        setUpPlayPauseFab();
        setUpPrevNext();
        setUpRepeatButton();
        setUpShuffleButton();
        setUpProgressSlider();
    }

    private void setUpPrevNext() {
        updatePrevNextColor();
        binding.playerNextButton.setOnClickListener(v -> MusicPlayerRemote.playNextSong());
        binding.playerPrevButton.setOnClickListener(v -> MusicPlayerRemote.back());
    }

    private void updateProgressTextColor() {
        int color = ThemeUtil.getPrimaryTextColor(requireContext(), false);
        binding.playerSongTotalTime.setTextColor(color);
        binding.playerSongCurrentProgress.setTextColor(color);
    }

    private void updatePrevNextColor() {
        binding.playerNextButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
        binding.playerPrevButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
    }

    private void setUpShuffleButton() {
        binding.playerShuffleButton.setOnClickListener(v -> MusicPlayerRemote.toggleShuffleMode());
    }

    private void updateShuffleState() {
        switch (MusicPlayerRemote.getShuffleMode()) {
            case QueueManager.SHUFFLE_MODE_SHUFFLE:
                binding.playerShuffleButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
            case QueueManager.SHUFFLE_MODE_NONE:
            default:
                binding.playerShuffleButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    private void setUpRepeatButton() {
        binding.playerRepeatButton.setOnClickListener(v -> MusicPlayerRemote.cycleRepeatMode());
    }

    private void updateRepeatState() {
        switch (MusicPlayerRemote.getRepeatMode()) {
            case QueueManager.REPEAT_MODE_NONE:
                binding.playerRepeatButton.setImageResource(R.drawable.ic_repeat_white_24dp);
                binding.playerRepeatButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
            case QueueManager.REPEAT_MODE_ALL:
                binding.playerRepeatButton.setImageResource(R.drawable.ic_repeat_white_24dp);
                binding.playerRepeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
            case QueueManager.REPEAT_MODE_THIS:
                binding.playerRepeatButton.setImageResource(R.drawable.ic_repeat_one_white_24dp);
                binding.playerRepeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    public void show() {
        binding.playerPlayPauseFab.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void hide() {
        binding.playerPlayPauseFab.setScaleX(0f);
        binding.playerPlayPauseFab.setScaleY(0f);
        binding.playerPlayPauseFab.setRotation(0f);
    }

    private void setUpProgressSlider() {
        int color = ThemeUtil.getPrimaryTextColor(requireContext(), false);
        binding.playerProgressSlider.getThumb().mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        binding.playerProgressSlider.getProgressDrawable().mutate().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);

        binding.playerProgressSlider.setOnSeekBarChangeListener(new SimpleOnSeekbarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress);
                    onUpdateProgressViews(MusicPlayerRemote.getSongProgressMillis(), MusicPlayerRemote.getSongDurationMillis());
                }
            }
        });
    }

    @Override
    public void onUpdateProgressViews(int progress, int total) {
        binding.playerBufferingIndicator.setVisibility(MusicPlayerRemote.isLoading() ? View.VISIBLE : View.GONE);

        binding.playerProgressSlider.setMax(total);
        binding.playerProgressSlider.setProgress(progress);

        binding.playerSongTotalTime.setText(MusicUtil.getReadableDurationString(total));
        binding.playerSongCurrentProgress.setText(MusicUtil.getReadableDurationString(progress));
    }
}
