package com.partymaker.meloman;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;


/**
 * Created by X550V on 19.10.2016.
 */

public class App extends Application {
   public HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
       // return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
        return app.proxy = app.newProxy();
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)
                .cacheDirectory(getCacheDir())
                .build();
    }
}
