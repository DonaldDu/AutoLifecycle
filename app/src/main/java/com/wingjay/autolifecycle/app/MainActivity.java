package com.wingjay.autolifecycle.app;

import android.os.Bundle;

import com.wingjay.autolifecycle.library.ALog;
import com.wingjay.autolifecycle.library.AutoLifecycleEvent;
import com.wingjay.autolifecycle.library.lifecycle.ActivityLifecycle;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * `adb logcat | grep 'AutoLifecycle'` to see log.
 * author: wingjay
 * http://wingjay.com
 */
public class MainActivity extends BaseActivity {
    //AutoLifecycleEvent
    IKnowLifecycle iKnowLifecycle = new IKnowLifecycle(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityLifecycle.STOP))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        ALog.e("auto-stop when Activity onDestroy: interval " + aLong);
                    }
                });

        Observable loadDataObservable = Observable.defer(new Callable<ObservableSource<?>>() {
            @Override
            public ObservableSource<?> call() {
                ALog.e("auto-execute when Activity RESUME: loadData()");
                return Observable.empty();
            }
        });
        executeWhen(loadDataObservable, ActivityLifecycle.RESUME);

        setContentView(R.layout.activity_main);
    }

    @AutoLifecycleEvent(activity = ActivityLifecycle.DESTROY)
    void testAuto() {
        ALog.i("auto-execute AutoLifecycleEvent");
    }
}
