package edu.calpoly.isstracker;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class IssStreamActivity extends DrawerActivity {

    public static final String VIDEO_ID = "ddFvjfvPnqk";

    private YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_iss_stream);
        inflateContentAndInitNavDrawer(R.layout.activity_iss_stream);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_player_fragment, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize(getString(R.string.api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    IssStreamActivity.this.player = player;
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                    player.loadVideo(VIDEO_ID);
                    player.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {

            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        colorToolbarBackButton((Toolbar) findViewById(R.id.toolbar),
                ContextCompat.getColor(this, R.color.text_color_secondary));
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && getResources().getBoolean(R.bool.landscape)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.play();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.iss_live_stream_menu, menu);
        //color info icon
        menu.getItem(0).getIcon().setColorFilter(ContextCompat.getColor(this, R.color.text_color_secondary), PorterDuff.Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                try {
                    onBackPressed();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iss_live_stream_info:
                showInfoDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showInfoDialog() {
        String message = "Video is streamed from:\nhttps://www.youtube.com/watch?v=" + VIDEO_ID;

        AlertDialog dialog = new AlertDialog.Builder(this) //, R.style.Theme_AppCompat_Dialog_Alert_dark
                .setTitle(getString(R.string.iss_live_stream_info))
                .setMessage(message)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(getResources().getBoolean(R.bool.landscape)){
                            //hide nav- and statusBar
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE |
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
                        }
                    }
                })
                .create();
        dialog.show();

        //making the link clickable
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        assert textView != null;
        final SpannableString s = new SpannableString(textView.getText());
        Linkify.addLinks(s, Linkify.WEB_URLS);
        textView.setText(s);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void colorToolbarBackButton(Toolbar toolbar, int color) {
        //back button
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ImageView) {
                ImageView drawerIcon = (ImageView) toolbar.getChildAt(i);
                drawerIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}