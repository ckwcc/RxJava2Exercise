package com.ckw.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 之前我们说的上游和下游分别是Observable和Observer,
 * 这次不一样的是上游变成了Flowable, 下游变成了Subscriber,
 * 但是水管之间的连接还是通过subscribe()
 *
 * FLowable中也有这种方法, 对应的就是BackpressureStrategy.DROP和BackpressureStrategy.LATEST这两种策略.
 从名字上就能猜到它俩是干啥的, Drop就是直接把存不下的事件丢弃,Latest就是只保留最新的事件
 */
public class FlowableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowable);
            //同一个线程
//        Flowable upStream = Flowable.create(new FlowableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
//                Log.d("----", "subscribe: 上游发送1 ");
//                e.onNext(1);
//                Log.d("----", "subscribe: 上游发送2 ");
//                e.onNext(2);
//                Log.d("----", "subscribe: 上游发送3 ");
//                e.onNext(3);
//                Log.d("----", "subscribe: 上游发送完成");
//                e.onComplete();
//            }
//        }, BackpressureStrategy.ERROR);
//
//        Subscriber downStream = new Subscriber<Integer>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//                Log.d("----", "onSubscribe: 下游订阅");
//                //如果没有这句话，上游不知道下游的处理能力，会出现(onError方法里会出现)
//               //onError: io.reactivex.exceptions.MissingBackpressureException: create: could not emit value due to lack of requests
//                s.request(Long.MAX_VALUE);
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//                Log.d("----", "onNext:下游接收 "+integer);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                Log.d("----", "onError: "+t);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d("----", "onComplete: 下游接收完毕");
//            }
//        };
//
//        upStream.subscribe(downStream);

//        //不同线程
//        Flowable.create(new FlowableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
//                Log.d("----", "subscribe: 上游发送1");
//                e.onNext(1);
//                Log.d("----", "subscribe: 上游发送2");
//                e.onNext(2);
//                Log.d("----", "subscribe: 上游发送3");
//                e.onNext(3);
//                Log.d("----", "subscribe: 上游发送完成");
////                下游如果没有申明接收上游事件的能力
//                // 上游会将事件存入上游的水缸里，上游默认的水缸大小是128，超过128，会报错
////                for (int i = 0; i < 129; i++) {
////                    Log.d("----", "subscribe: 上游发送："+i);
////                    e.onNext(i);
////                }
//                e.onComplete();
//            }
//        },BackpressureStrategy.ERROR)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Integer>() {
//                    @Override
//                    public void onSubscribe(Subscription s) {
////                        s.request(3);//这里代表下游处理事件的能力
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        Log.d("----", "onNext: 下游接收到："+integer);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        Log.d("----", "onError: 下游报错："+t);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d("----", "onComplete: 下游接收完成");
//                    }
//                });

        //我们可以自己申明一个新的水缸
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                long requested = e.requested();//这是下游的处理事件的能力

                for (int i = 0; i < 100; i++) {
                    Log.d("----", "subscribe: 上游发送事件："+i);
                    e.onNext(i);
                }
            }
        },BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d("----", "onSubscribe: 下游订阅");
                s.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d("----", "onNext: 下游接收到："+integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.d("----", "onError: 下游报错："+t);
            }

            @Override
            public void onComplete() {
                Log.d("----", "onComplete: 下游接收完成");
            }
        });
    }
}
