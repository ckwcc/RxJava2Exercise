package com.ckw.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

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

        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("one");
                e.onNext("two");
                e.onNext("three");
                e.onNext("four");
                e.onComplete();
            }
        });

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + ":" +s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("----", "onSubscribe: ");
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
        02-02 11:22:13.210 29267-29267/com.ckw.rxjava2demo D/----: onSubscribe:
        02-02 11:22:13.212 29267-29267/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：1:one
        02-02 11:22:13.212 29267-29267/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：2:two
        02-02 11:22:13.212 29267-29267/com.ckw.rxjava2demo D/----: onNext: 接收到的信息：3:three
        02-02 11:22:13.212 29267-29267/com.ckw.rxjava2demo D/----: onComplete:
        */
    }
}
