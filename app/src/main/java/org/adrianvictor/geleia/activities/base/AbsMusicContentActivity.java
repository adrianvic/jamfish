package org.adrianvictor.geleia.activities.base;

import static org.adrianvictor.geleia.adapter.CustomFragmentStatePagerAdapter.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import org.adrianvictor.geleia.App;
import org.adrianvictor.geleia.fragments.OfflineFragment;
import org.adrianvictor.geleia.interfaces.StateListener;
import org.adrianvictor.geleia.service.LoginService;
import org.adrianvictor.geleia.util.NavigationUtil;

public abstract class AbsMusicContentActivity extends AbsMusicPanelActivity implements StateListener {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if (intent.getAction() == null) return;

            switch(intent.getAction()) {
                case LoginService.STATE_ONLINE:
                    onStateOnline();
                    break;
                case LoginService.STATE_OFFLINE:
                    onStateOffline();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(LoginService.STATE_POLLING);
        filter.addAction(LoginService.STATE_ONLINE);
        filter.addAction(LoginService.STATE_OFFLINE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }

        if (App.getApiClient() == null) {
            startService(new Intent(this, LoginService.class));
        } else {
            onStateOnline();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onStatePolling() {}
}
