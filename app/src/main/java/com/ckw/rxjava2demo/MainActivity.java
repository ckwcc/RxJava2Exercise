package com.ckw.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 这个Demo用于深入学习RxJava2
 *
 * 先假设有两根水管

 上面一根水管为事件产生的水管，叫它上游吧，下面一根水管为事件接收的水管叫它下游吧。

 两根水管通过一定的方式连接起来，使得上游每产生一个事件，下游就能收到该事件。注意这里和官网的事件图是反过来的,
 这里的事件发送的顺序是先1,后2,后3这样的顺序, 事件接收的顺序也是先1,后2,后3的顺序,
 我觉得这样更符合我们普通人的思维, 简单明了.

 这里的上游和下游就分别对应着RxJava中的Observable和Observer，它们之间的连接就对应着subscribe()

 ObservableEmitter： Emitter是发射器的意思，那就很好猜了，这个就是用来发出事件的，它可以发出三种类型的事件，通过调用emitter的onNext(T value)、onComplete()和onError(Throwable error)就可以分别发出next事件、complete事件和error事件。

 但是，请注意，并不意味着你可以随意乱七八糟发射事件，需要满足一定的规则：

 上游可以发送无限个onNext, 下游也可以接收无限个onNext.
 当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送, 而下游收到onComplete事件之后将不再继续接收事件.
 当上游发送了一个onError后, 上游onError之后的事件将继续发送, 而下游收到onError事件之后将不再继续接收事件.
 上游可以不发送onComplete或onError.
 最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete, 也不能发多个onError,
 也不能先发一个onComplete, 然后再发一个onError, 反之亦然
 注: 关于onComplete和onError唯一并且互斥这一点, 是需要自行在代码中进行控制,
 如果你的代码逻辑中违背了这个规则, **并不一定会导致程序崩溃. **
 比如发送多个onComplete是可以正常运行的, 依然是收到第一个onComplete就不再接收了,
 但若是发送多个onError, 则收到第二个onError事件会导致程序会崩溃.

 介绍了ObservableEmitter, 接下来介绍Disposable, 这个单词的字面意思是一次性用品,用完即可丢弃的.
 那么在RxJava中怎么去理解它呢, 对应于上面的水管的例子, 我们可以把它理解成两根管道之间的一个机关,
 当调用它的dispose()方法时, 它就会将两根管道切断, 从而导致下游收不到事件.

 注意: 调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件.


 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        //1
//        //创建一个上游
//        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(1);
//                e.onNext(2);
//                e.onNext(3);
//                e.onComplete();
//            }
//        });
//        //创建一个下游
//        Observer<Integer> observer = new Observer<Integer>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.d("----", "onSubscribe: ");
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//                Log.d("----", "onNext: "+integer);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d("----", "onError: "+e);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d("----", "onComplete: ");
//            }
//        };
//        //建立连接
//        observable.subscribe(observer);

        //2
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d("----", "subscribe: 1");
                e.onNext(1);
                Log.d("----", "subscribe: 2");
                e.onNext(2);
                Log.d("----", "subscribe: 3");
                e.onNext(3);
                Log.d("----", "subscribe: complete");
                e.onComplete();
                Log.d("----", "subscribe: 4");
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private int i;
            private Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("----", "onSubscribe: ");
                disposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d("----", "onNext: "+integer);
                i++;
                if(i == 2){
                    Log.d("----", "onNext: disposable");
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("----", "onError: "+e);
            }

            @Override
            public void onComplete() {
                Log.d("----", "onComplete: ");
            }
        });

    }
}
