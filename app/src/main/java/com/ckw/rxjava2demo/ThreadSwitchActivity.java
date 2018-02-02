package com.ckw.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ckw.rxjava2demo.retrofit.Api;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 线程的调度、切换
 * 简单的来说, subscribeOn() 指定的是上游发送事件的线程, observeOn() 指定的是下游接收事件的线程.

 多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.

 多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.


 在RxJava中, 已经内置了很多线程选项供我们选择, 例如有

 Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
 Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
 Schedulers.newThread() 代表一个常规的新线程
 AndroidSchedulers.mainThread() 代表Android的主线程
 这些内置的Scheduler已经足够满足我们开发的需求, 因此我们应该使用内置的这些选项, 在RxJava内部使用的是线程池来维护这些线程, 所有效率也比较高.

 */
public class ThreadSwitchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d("----", "subscribe: "+Thread.currentThread().getName());
                e.onNext(1);
            }
        });

        Consumer consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("----", "accept: "+Thread.currentThread().getName());
                Log.d("----", "accept: "+integer);
            }
        };

//        observable.subscribe(consumer);
        observable.subscribeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(consumer);

        //网址原因，测试未通过
//        Api api = create().create(Api.class);
//        api.login("CKW")
//                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
//                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
//                .subscribe(new Observer<String >() {
//                    @Override
//                    public void onSubscribe(Disposable d) {}
//
//                    @Override
//                    public void onNext(String value) {}
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
//                    }
//                });


    }

    private static Retrofit create() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        return new Retrofit.Builder().baseUrl( "http://10.71.33.67:80")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}
