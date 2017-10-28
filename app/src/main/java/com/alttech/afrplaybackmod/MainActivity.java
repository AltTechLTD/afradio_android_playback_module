package com.alttech.afrplaybackmod;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alttech.afrsdk.Config;
import com.alttech.afrsdk.views.PlaybackFragment;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Config config = new Config();

    config.setAppId("tKvHWTnKAXLfxjAYm");
    config.setResId("NFR3cG4sEqLT5pnk3");

    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.main, PlaybackFragment.Companion.newInstance(config))
        .commit();

  }
}
