package edu.calpoly.isstracker;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class IssStreamActivity extends DrawerActivity {

    public static final String VIDEO_ID = "ddFvjfvPnqk";

    private YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateContentAndInitNavDrawer(R.layout.activity_iss_stream);

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
    protected void onResume() {
        super.onResume();
        getNavigationView().setCheckedItem(R.id.stream_activity);
        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.iss_live_stream_menu, menu);
        //color info icon
        menu.getItem(0).getIcon().setColorFilter(ContextCompat.getColor(this,
                R.color.text_color_secondary), PorterDuff.Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iss_live_stream_info:
                showInfoDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showInfoDialog() {
        String message = "Video is streamed from:\nhttps://www.youtube.com/watch?v=" + VIDEO_ID;

        AlertDialog dialog = new AlertDialog.Builder(this)
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
}