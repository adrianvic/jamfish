package org.adrianvictor.geleia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.TwoStatePreference;

import org.adrianvictor.geleia.databinding.ActivitySettingsBinding;
import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.views.shortcuts.DynamicShortcutManager;
import org.adrianvictor.geleia.dialogs.preferences.CategoryPreferenceDialog;
import org.adrianvictor.geleia.dialogs.preferences.NowPlayingPreferenceDialog;
import org.adrianvictor.geleia.activities.base.AbsBaseActivity;
import org.adrianvictor.geleia.util.PreferenceUtil;

import java.io.File;

public class SettingsActivity extends AbsBaseActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setBackgroundColor(PreferenceUtil.getInstance(this).getPrimaryColor());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        } else {
            SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null) fragment.invalidateSettings();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private ActivityResultLauncher<Intent> dirPickerLauncher;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            dirPickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri uri = data.getData();
                                requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
                                editor.putString(PreferenceUtil.LOCATION_DOWNLOAD, uri.toString());
                                editor.apply();
                                invalidateSettings();
                            }
                        }
                    });
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_library);
            addPreferencesFromResource(R.xml.pref_interface);
            addPreferencesFromResource(R.xml.pref_notification);
            addPreferencesFromResource(R.xml.pref_now_playing);
            addPreferencesFromResource(R.xml.pref_lock_screen);
            addPreferencesFromResource(R.xml.pref_playback);
            addPreferencesFromResource(R.xml.pref_cache);
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getListView().setPadding(0, 0, 0, 0);
            setDivider(null);
            invalidateSettings();
            PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PreferenceUtil.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
        }

        @SuppressWarnings("ConstantConditions")
        private void invalidateSettings() {
            final TwoStatePreference classicNotification = findPreference(PreferenceUtil.CLASSIC_NOTIFICATION);
            final TwoStatePreference coloredNotification = findPreference(PreferenceUtil.COLORED_NOTIFICATION);
            final TwoStatePreference colorAppShortcuts = findPreference(PreferenceUtil.COLORED_SHORTCUTS);
            final Preference categoryPreference = findPreference(PreferenceUtil.CATEGORIES);
            final Preference nowPlayingPreference = findPreference(PreferenceUtil.NOW_PLAYING_SCREEN);
            final Preference downloadLocationPreference = findPreference(PreferenceUtil.LOCATION_DOWNLOAD);
            final Preference showAlbumCoverPreference = findPreference(PreferenceUtil.SHOW_ALBUM_COVER);
            final Preference blurAlbumCoverPreference = findPreference(PreferenceUtil.BLUR_ALBUM_COVER);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                classicNotification.setEnabled(false);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                coloredNotification.setEnabled(false);
                colorAppShortcuts.setEnabled(false);
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                blurAlbumCoverPreference.setEnabled(false);
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                // custom notification layouts were removed entirely in Android 12
                classicNotification.setEnabled(false);
                coloredNotification.setEnabled(false);
            }

            categoryPreference.setOnPreferenceClickListener(preference -> {
                CategoryPreferenceDialog.create().show(getParentFragmentManager(), CategoryPreferenceDialog.TAG);
                return false;
            });

            nowPlayingPreference.setOnPreferenceClickListener(preference -> {
                NowPlayingPreferenceDialog.create().show(getParentFragmentManager(), NowPlayingPreferenceDialog.TAG);
                return false;
            });

            downloadLocationPreference.setOnPreferenceClickListener(preference -> {
                openDirectoryPicker();
                return true;
            });

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            String downloadLocation = preferences.getString(PreferenceUtil.LOCATION_DOWNLOAD, null);
            if (downloadLocation != null) {
                Uri uri = Uri.parse(downloadLocation);
                File file = new File(uri.getPath());
                downloadLocationPreference.setSummary(file.getPath());
            } else {
                downloadLocationPreference.setSummary(R.string.pref_title_download_location);
            }


            // use this to set default state for playback screen and notification style
            onSharedPreferenceChanged(preferences, PreferenceUtil.NOW_PLAYING_SCREEN);
            onSharedPreferenceChanged(preferences, PreferenceUtil.CLASSIC_NOTIFICATION);
        }

        private void openDirectoryPicker() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            dirPickerLauncher.launch(intent);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferenceUtil.COLORED_SHORTCUTS:
                    new DynamicShortcutManager(requireContext()).updateDynamicShortcuts();
                    break;
                case PreferenceUtil.PRIMARY_COLOR:
                case PreferenceUtil.ACCENT_COLOR:
                case PreferenceUtil.GENERAL_THEME:
                    // apply theme before reloading shortcuts to apply the new icon colors
                    requireActivity().setTheme(PreferenceUtil.getInstance(getContext()).getTheme().style);
                    new DynamicShortcutManager(requireContext()).updateDynamicShortcuts();

                    requireActivity().recreate();
                    break;
                case PreferenceUtil.NOW_PLAYING_SCREEN:
                    Preference preference = findPreference(PreferenceUtil.NOW_PLAYING_SCREEN);

                    preference.setSummary(PreferenceUtil.getInstance(getActivity()).getNowPlayingScreen().titleRes);
                    break;
                case PreferenceUtil.CLASSIC_NOTIFICATION:
                    TwoStatePreference colorNotification = findPreference(PreferenceUtil.COLORED_NOTIFICATION);

                    colorNotification.setEnabled(sharedPreferences.getBoolean(key, false));
                    colorNotification.setChecked(colorNotification.isEnabled());
                    break;
            }
        }
    }
}
