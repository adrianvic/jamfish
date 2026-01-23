package org.adrianvictor.geleia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import org.adrianvictor.geleia.App;
import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.activities.base.AbsBaseActivity;
import org.adrianvictor.geleia.model.User;
import org.adrianvictor.geleia.service.LoginService;
import org.adrianvictor.geleia.util.NavigationUtil;
import org.adrianvictor.geleia.util.PreferenceUtil;

import java.util.List;

public class SplashActivity extends AbsBaseActivity {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            switch (intent.getAction()) {
                case LoginService.STATE_ONLINE:
                    NavigationUtil.startMain(context);
                    finish();
                    break;
                case LoginService.STATE_OFFLINE:
                    NavigationUtil.startUnreachable(context);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(LoginService.STATE_ONLINE);
        filter.addAction(LoginService.STATE_OFFLINE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }

        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, R.anim.fade_delay);
    }

    @Override
    protected void onResume() {
        super.onResume();

        User user = App.getDatabase().userDao().getUser(PreferenceUtil.getInstance(this).getUser());
        List<User> available = App.getDatabase().userDao().getUsers();

        if (user == null && !available.isEmpty()) {
            NavigationUtil.startSelect(this);
            finish();
        } else if (user == null) {
            NavigationUtil.startLogin(this);
            finish();
        } else {
            startService(new Intent(this, LoginService.class));
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
