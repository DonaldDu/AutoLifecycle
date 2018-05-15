package com.wingjay.autolifecycle.library;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;


/**
 * ILifecycleProvider
 *
 * @author wingjay
 * @date 2017/08/09
 */
public interface ILifecycleProvider {
    /**
     * Binds a source until a specific event occurs.
     *
     * @param event the event that triggers unsubscription
     */
    <T> ObservableTransformer<T, T> bindUntilEvent(@NonNull IContextLifecycle event);

    /**
     * Binds a default event, normally DESTROY event
     */
    <T> ObservableTransformer<T, T> bindDefault();

    /**
     * Execute an Observable once event appears.
     */
    void executeWhen(@NonNull Observable observable, @NonNull IContextLifecycle event);
}
