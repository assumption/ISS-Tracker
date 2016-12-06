package edu.calpoly.isstracker;

import com.google.android.gms.common.GoogleApiAvailability;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView version = (TextView) findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                ((View) toolbar.getParent()).setSelected(scrollView.getScrollY() != 0);
            }
        });

        //Glide license
        View license_item_1 = findViewById(R.id.license_item_1);
        ((TextView) license_item_1.findViewById(R.id.text)).setText(R.string.glide);
        license_item_1.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/bumptech/glide/blob/master/LICENSE")));
            }
        });
        license_item_1.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/bumptech/glide")));
            }
        });

        //Google Play Service Attribution
        View license_item_2 = findViewById(R.id.license_item_2);
        ((TextView) license_item_2.findViewById(R.id.text)).setText(R.string.google_play_servcie_attribution);
        license_item_2.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView webView = new WebView(AboutActivity.this);
                webView.loadData(GoogleApiAvailability.getInstance()
                        .getOpenSourceSoftwareLicenseInfo(AboutActivity.this), "text/plain", "utf-8");

                new AlertDialog.Builder(AboutActivity.this)
                        .setTitle("Google Play Servcie Attribution")
                        .setView(webView)
                        .setPositiveButton("Ok", null)
                        .create().show();
            }
        });
        license_item_2.findViewById(R.id.button).setVisibility(View.GONE);

        //Retrofit license
        View license_item_3 = findViewById(R.id.license_item_3);
        ((TextView) license_item_3.findViewById(R.id.text)).setText(R.string.retrofit);
        license_item_3.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/square/retrofit/blob/master/LICENSE.txt")));
            }
        });
        license_item_3.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/square/retrofit")));
            }
        });

        //Libgdx license
        View license_item_4 = findViewById(R.id.license_item_4);
        ((TextView) license_item_4.findViewById(R.id.text)).setText(R.string.libgdx);
        license_item_4.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/libgdx/libgdx/blob/master/LICENSE")));
            }
        });
        license_item_4.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/libgdx/libgdx")));
            }
        });

        //Libgdx-CardBoard-Extension license
        View license_item_5 = findViewById(R.id.license_item_5);
        ((TextView) license_item_5.findViewById(R.id.text)).setText(R.string.libgdx_cardBoard_extension);
        license_item_5.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("")));*/
            }
        });
        license_item_5.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/yangweigbh/Libgdx-CardBoard-Extension")));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
