package com.gzsll.hupu.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.example.agc.aigoucai.activity.MainWebviewActivity;
import com.example.agc.aigoucai.util.IntentUtil;
import com.example.agc.aigoucai.util.LogUtil;
import com.example.agc.aigoucai.util.SharePreferencesUtil;
import com.example.agc.aigoucai.util.TrustAllCerts;
import com.google.gson.Gson;
import com.gzsll.hupu.R;
import com.gzsll.hupu.ui.BaseActivity;
import com.gzsll.hupu.ui.main.MainActivity;
import com.gzsll.hupu.ui.messagelist.MessageActivity;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by sll on 2016/3/11.
 */
public class SplashActivity extends BaseActivity implements SplashContract.View {
    @BindView(R.id.iv_welcome)
    ImageView ivWelcome;
    @BindView(R.id.iv_weihu)
    ImageView ivWeihu;
    private DataInfo dataInfo;
    private Gson gson = new Gson();
    public static final String ACTION_NOTIFICATION_MESSAGE =
            "com.gzsll.hupu.ACTION_NOTIFICATION_MESSAGE";


    @Inject
    SplashPresenter mPresenter;

    @Override
    public int initContentView() {
        return R.layout.activity_splash;
    }

    @Override
    public void initInjector() {
        DaggerSplashComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .splashModule(new SplashModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        mPresenter.initUmeng();
        mPresenter.initHuPuSign();


    }

    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return true;
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return false;
    }

    @Override
    public void showMainUi() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(9, TimeUnit.SECONDS)
                            .writeTimeout(9, TimeUnit.SECONDS)
                            .sslSocketFactory(createSSLSocketFactory())
                            .hostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String hostname, SSLSession session) {
                                    return true;
                                }
                            })
                            .build();
                    Request request = new Request.Builder()
                            .url("https://hk1.android.jrapp.me/switch/1")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String s = response.body().string();
                        LogUtil.e("=================" + s);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        dataInfo = gson.fromJson(s, DataInfo.class);
                        if (dataInfo.getData().getApp_status().equals("1")) {
                            ivWelcome.setVisibility(View.VISIBLE);
                            ivWeihu.setVisibility(View.GONE);
                            Looper.prepare();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (dataInfo.getData().getApp_status().equals("0")) {
                                        MainActivity.startActivity(SplashActivity.this);
                                        String action = getIntent().getAction();
                                        if (TextUtils.equals(action, ACTION_NOTIFICATION_MESSAGE)) {
                                            MessageActivity.startActivity(SplashActivity.this);
                                        }
                                        finish();
                                    } else {
                                        Bundle bundleTab = new Bundle();
                                        bundleTab.putString("url", dataInfo.getData().getApp_url());
                                        SharePreferencesUtil.addString(SplashActivity.this, "main_url", dataInfo.getData().getApp_url());
                                        IntentUtil.gotoActivity(SplashActivity.this, MainWebviewActivity.class, bundleTab, false);

                                    }

                                    finish();
                                }
                            }, 1000);

                            Looper.loop();

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 提示維護中
                                    ivWelcome.setVisibility(View.GONE);
                                    ivWeihu.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 提示維護中
                                ivWelcome.setVisibility(View.GONE);
                                ivWeihu.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("=============" + e);
                }
            }
        }).start();


    }


    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation

    }
}
