package org.adrianvictor.geleia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.adrianvictor.geleia.App;
import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.activities.base.AbsBaseActivity;
import org.adrianvictor.geleia.model.User;
import org.adrianvictor.geleia.service.LoginService;
import org.adrianvictor.geleia.util.NavigationUtil;
import org.adrianvictor.geleia.util.PreferenceUtil;

import java.util.List;

public class SplashActivity extends AbsBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (user == null && available.size() != 0) {
            NavigationUtil.startSelect(this);
        } else if (user == null) {
            NavigationUtil.startLogin(this);
        } else {
            startService(new Intent(this, LoginService.class));
            new Handler().postDelayed(() -> NavigationUtil.startMain(this), 1000);
        }
    }
}
