package com.ckw.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ckw.rxjava2demo.retrofit.Api;
import com.ckw.rxjava2demo.retrofit.RetrofitUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Zip通过一个函数将多个Observable发送的事件结合到一起，然后发送这些组合到一起的事件.
 * 它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。
 *
 * 组合的过程是分别从 两根水管里各取出一个事件 来进行组合, 并且一个事件只能被使用一次,
 * 组合的顺序是严格按照事件发送的顺利 来进行的, 也就是说不会出现圆形1 事件和三角形B 事件进行合并,
 * 也不可能出现圆形2 和三角形A 进行合并的情况.
 最终下游收到的事件数量 是和上游中发送事件最少的那一根水管的事件数量
 相同. 这个也很好理解, 因为是从每一根水管 里取一个事件来进行合并,
 最少的 那个肯定就最先取完 , 这个时候其他的水管尽管还有事件 ,
 但是已经没有足够的事件来组合了, 因此下游就不会收到剩余的事件了.


 */
public class ZipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);

        /*
        学习了Zip的基本用法, 那么它在Android有什么用呢, 其实很多场景都可以用到Zip. 举个例子.
        比如一个界面需要展示用户的一些信息, 而这些信息分别要从两个服务器接口中获取,
        而只有当两个都获取到了之后才能进行展示, 这个时候就可以用Zip了:
        * */
//        Api api = RetrofitUtils.create().create(Api.class);
//        Observable<String> userBaseInfo = api.getUserBaseInfo("").subscribeOn(Schedulers.io());
//        Observable<String> userExtraInfo = api.getUserExtraInfo("").subscribeOn(Schedulers.io());
//
//        Observable.zip(userBaseInfo, userExtraInfo, new BiFunction<String, String, String>() {
//            @Override
//            public String apply(String s, String s2) throws Exception {
//                StringBuffer stringBuffer = new StringBuffer();
//                stringBuffer.append(s);
//                stringBuffer.append(s2);
//                return stringBuffer.toString();
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        Log.d("----", "accept: 最后拿到的信息");
//                    }
//                });

        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d("----", "subscribe: 发送：1");
                e.onNext(1);
                Thread.sleep(1000);

                Log.d("----", "subscribe: 发送：2");
                e.onNext(2);
                Thread.sleep(1000);

                Log.d("----", "subscribe: 发送：3");
                e.onNext(3);
                Thread.sleep(1000);

                Log.d("----", "subscribe1:完成 ");
                e.onComplete();
            }
        })
         //例子二，切换到io线程
        .subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d("----", "subscribe: 发送one");
                e.onNext("one");
                Thread.sleep(1000);

                Log.d("----", "subscribe: 发送two");
                e.onNext("two");
                Thread.sleep(1000);

                Log.d("----", "subscribe: 发送three");
                e.onNext("three");
                Thread.sleep(1000);

                Log.d("----", "subscribe: 发送four");
                e.onNext("four");
                Thread.sleep(1000);

                Log.d("----", "subscribe2: 完成");
                e.onComplete();
            }
        })
        //例子二，切换到io线程
        .subscribeOn(Schedulers.io());;

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + ":" +s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("----", "onSubscribe: 订阅");
            }

            @Override
            public void onNext(String s) {
                Log.d("----", "onNext: 接收到的信息："+s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("----", "onError: ");
            }

            @Override
            public void onComplete() {
                Log.d("----", "onComplete: ");
            }
        });

       /*
        log打印的信息
        通过log可以发现，在用一个线程里，会将第一个observable的东西发送完，再发送另一个的
        02-02 11:33:21.979 29267-29267/com.ckw.rxjava2demo D/----: onSubscribe:
        02-02 11:33:21.979 29267-29267/com.ckw.rxjava2demo D/----: subscribe: 发送：1
        02-02 11:33:21.979 29267-29267/com.ckw.rxjava2demo D/----: subscribe: 发送：2
        02-02 11:33:21.979 29267-29267/com.ckw.rxjava2demo D/----: subscribe: 发送：3
        02-02 11:33:21.979 29267-29267/com.ckw.rxjava2demo D/----: subscribe1:完成
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: subscribe: 发送one
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：1:one
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: subscribe: 发送two
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：2:two
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: subscribe: 发送three
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：3:three
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: onComplete:
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: subscribe: 发送four
        02-02 11:33:21.980 29267-29267/com.ckw.rxjava2demo D/----: subscribe2: 完成

        */

       /*
        02-02 11:42:33.205 32358-32358/com.ckw.rxjava2demo D/----: onSubscribe: 订阅
        02-02 11:42:33.209 32358-32407/com.ckw.rxjava2demo D/----: subscribe: 发送one
        02-02 11:42:33.209 32358-32406/com.ckw.rxjava2demo D/----: subscribe: 发送：1
        02-02 11:42:33.210 32358-32406/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：1:one
        02-02 11:42:34.210 32358-32407/com.ckw.rxjava2demo D/----: subscribe: 发送two
        02-02 11:42:34.210 32358-32406/com.ckw.rxjava2demo D/----: subscribe: 发送：2
        02-02 11:42:34.210 32358-32406/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：2:two
        02-02 11:42:35.210 32358-32407/com.ckw.rxjava2demo D/----: subscribe: 发送three
        02-02 11:42:35.210 32358-32406/com.ckw.rxjava2demo D/----: subscribe: 发送：3
        02-02 11:42:35.211 32358-32406/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：3:three
        02-02 11:42:36.211 32358-32407/com.ckw.rxjava2demo D/----: subscribe: 发送four
        02-02 11:42:36.211 32358-32406/com.ckw.rxjava2demo D/----: subscribe1:完成
        02-02 11:42:36.211 32358-32407/com.ckw.rxjava2demo D/----: onComplete:
        02-02 11:42:37.211 32358-32407/com.ckw.rxjava2demo D/----: subscribe2: 完成
       * */
    }
}
