package com.wingjay.autolifecycle.library;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


/**
 * LifecycleProviderDelegate
 *
 * @author wingjay
 * @date 2017/10/17
 */
public class LifecycleProviderDelegate {

    public <T> ObservableTransformer<T, T> bindUntilEvent(@NonNull final PublishSubject<IContextLifecycle> lifecycleSubject, @NonNull final IContextLifecycle event) {
        ALog.i("bindUntilEvent " + event);
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.takeUntil(lifecycleSubject.skipWhile(new Predicate<IContextLifecycle>() {
                    @Override
                    public boolean test(IContextLifecycle lifecycle) {
                        ALog.i(event + " appears!");
                        return !lifecycle.equals(event);
                    }
                }));
            }
        };
    }

    public void executeWhen(@NonNull final PublishSubject<IContextLifecycle> lifecycleSubject, @NonNull final Observable observable, @NonNull final IContextLifecycle event) {
        final String tag = event.toString() + " executeWhen ";
        lifecycleSubject.skipWhile(new Predicate<IContextLifecycle>() {
            @Override
            public boolean test(IContextLifecycle lifecycle) {
                return !lifecycle.equals(event);
            }
        }).subscribe(new Observer<IContextLifecycle>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                this.disposable = d;
                ALog.i(tag + " onSubscribe");
            }

            @Override
            public void onNext(IContextLifecycle lifecycle) {
                ALog.i(tag + " onNext>>>> " + lifecycle);
                if (disposable != null) disposable.dispose();
                observable.subscribe();
            }

            @Override
            public void onError(Throwable e) {
                ALog.i(tag + " onError");
            }

            @Override
            public void onComplete() {
                ALog.i(tag + " onComplete");
            }
        });
    }
}
