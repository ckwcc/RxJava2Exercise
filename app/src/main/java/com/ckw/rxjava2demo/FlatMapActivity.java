package com.ckw.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ckw.rxjava2demo.retrofit.Api;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 前一个activity了解了线程调度, 并且举了两个实际中的例子, 其中有一个登录的例子,
 * 不知大家有没有想过这么一个问题, 如果是一个新用户, 必须先注册, 等注册成功之后再自动登录该怎么做呢.

 很明显, 这是一个嵌套的网络请求, 首先需要去请求注册, 待注册成功回调了再去请求登录的接口.

 第一种方式：login() register() 方式

 第二种方式：map map是RxJava中最简单的一个变换操作符了,
 它的作用就是对上游发送的每一个事件应用一个函数, 使得每一个事件都按照指定的函数去变化

 flatMap是一个非常强大的操作符, 先用一个比较难懂的概念说明一下:
 FlatMap将一个发送事件的上游Observable变换为多个发送事件的Observables，然后将它们发射的事件合并后放进一个单独的Observable里.
 上游每发送一个事件, flatMap都将创建一个新的水管, 然后发送转换之后的新的事件,
 下游接收到的就是这些新的水管发送的数据. 这里需要注意的是, flatMap并不保证事件的顺序,

 这里也简单说一下concatMap吧, 它和flatMap的作用几乎一模一样, 只是它的结果是严格按照上游发送的顺序来发送的,
 */
public class FlatMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

//        在上游我们发送的是数字类型, 而在下游我们接收的是String类型, 中间起转换作用的就是map操作符
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                Log.d("----", "subscribe: 发送的信息："+1);
//                e.onNext(1);
//                Log.d("----", "subscribe: 发送的信息："+2);
//                e.onNext(2);
//                Log.d("----", "subscribe: 发送的信息："+3);
//                e.onNext(3);
//                Log.d("----", "subscribe: 完成");
//                e.onComplete();
//            }
//        }).map(new Function<Integer, String>() {
//            @Override
//            public String apply(Integer integer) throws Exception {
//                //map的作用就是转化，这里将int类型的数据 转换为 String类型的数据
//                return "this is " + integer;
//            }
//        }).subscribe(new Consumer<String>() {
//
//            @Override
//            public void accept(String s) throws Exception {
//                Log.d("----", "accept: 接收到的信息："+s);
//            }
//        });

        //flatMap 的使用
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(1);
//                e.onNext(2);
//                e.onNext(3);
//                e.onComplete();
//            }
//        }).flatMap(new Function<Integer, ObservableSource<String>>() {
//            @Override
//            public ObservableSource<String> apply(Integer integer) throws Exception {
//                List<String> list = new ArrayList<>();
//                for (int i = 0; i < 3; i++) {
//                    list.add("this is "+integer);
//                }
//                return Observable.fromIterable(list).delay(10,TimeUnit.MILLISECONDS);
//            }
//        }).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                Log.d("----", "accept: 接收到的信息："+s);
//            }
//        });

        //模拟注册之后再登录
        final Api api = create().create(Api.class);
        //之前有说过，只能subscribeOn一次，可以observeOn多次
        api.register("ckw")//发起注册请求
                .subscribeOn(Schedulers.io())//在io线程发起网络请求
                .observeOn(AndroidSchedulers.mainThread())//在主线程处理注册请求结果
                .doOnNext(new Consumer<String>() {//感觉不是必须的，如果不用处理注册结果，就不需要了吧
                    @Override
                    public void accept(String s) throws Exception {
                        //先对注册的结果进行一些处理
                    }
                })
                .observeOn(Schedulers.io())//在io线程处理登录请求
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        //发起登录的网络请求
                        return api.login("ckw");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//在主线程处理登录请求结果
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d("----", "accept: 登录成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("----", "accept: 登录失败");
                    }
                });
    }

    private void login(){
        Api api = create().create(Api.class);
        api.login("ckw")
                .subscribeOn(Schedulers.io())//在io线程开启网络请求
                .observeOn(AndroidSchedulers.mainThread())//在主线程处理网络请求的结果
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //登录成功
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //登录失败
                    }
                });
    }


    private void register(){
        Api api = create().create(Api.class);
        api.register("register")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //注册成功，调用登录方法
                        login();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //注册失败
                    }
                });
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
