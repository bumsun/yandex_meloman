package com.partymaker.meloman;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private String proxyUrl;
    String radioUrl = "http://ic7.101.ru:8000/c17_27?userid=0&setst=anej52rmkqmvts4h2c2hf2a3d0&tok=25422394qrfrVY2A%2Br3cXzoYmJQM0g%3D%3D1";
    private HttpProxyCacheServer proxy;
    private MediaPlayer mediaPlayer;
    private EditText radioAdressET;
    private ImageView playIV;
    private Button clearCacheBTN;

    private boolean isPlay = false;
    private RelativeLayout progressRL;
    private RelativeLayout contentRL;
    private String lastStation = radioUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioAdressET = (EditText) findViewById(R.id.radioAdressET);
        playIV = (ImageView) findViewById(R.id.playIV);
        clearCacheBTN = (Button) findViewById(R.id.clearCacheBTN);
        radioAdressET.setText(radioUrl);

        updateProxy(null);

        playIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay){
                    mediaPlayer.pause();
                    playIV.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_play_arrow_white_24dp));
                    isPlay = !isPlay;
                }else{
                    if(lastStation.equals(radioAdressET.getText().toString())){
                        playRadio();
                    }else{
                        clearCache(new ClearCacheListener() {
                            @Override
                            public void onChachCleared() {
                                playRadio();
                            }
                        });
                    }

                }
            }

            public void playRadio() {
                try {
                    lastStation = radioAdressET.getText().toString();
                    mediaPlayer.start();
                    playIV.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_pause_white_24dp));
                    isPlay = !isPlay;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clearCacheBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearCache(null);
            }
        });

    }

    interface ClearCacheListener{
        public void onChachCleared();
    }

    private void clearCache(ClearCacheListener clearCacheListener){
        proxy.shutdown();
        try {
            Util.cleanDirectory(getCacheDir());

        } catch (IOException e) {
            e.printStackTrace();
        }
        updateProxy(clearCacheListener);
    }

    public void updateProxy(final ClearCacheListener clearCacheListener) {
        initProgress();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                proxy = App.getProxy(MainActivity.this);
                proxyUrl = proxy.getProxyUrl(radioAdressET.getText().toString());

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(proxyUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            contentLoaded();
                        }
                    });

                    if(clearCacheListener != null){
                        clearCacheListener.onChachCleared();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initProgress(){
        progressRL = (RelativeLayout) findViewById(R.id.progressRL);
        contentRL = (RelativeLayout) findViewById(R.id.contentRL);
        contentProgress();
    }

    private void contentProgress(){
        progressRL.setVisibility(View.VISIBLE);
        contentRL.setVisibility(View.GONE);
    }

    private void contentLoaded(){
        progressRL.setVisibility(View.GONE);
        contentRL.setVisibility(View.VISIBLE);
    }
}
