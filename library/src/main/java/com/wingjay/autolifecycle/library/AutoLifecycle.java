package com.wingjay.autolifecycle.library;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.wingjay.autolifecycle.library.lifecycle.ActivityLifecycle;
import com.wingjay.autolifecycle.library.lifecycle.FragmentLifecycle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;


/**
 * AutoLifecycle
 *
 * @author wingjay
 * @date 2017/10/22
 */
public class AutoLifecycle {

    private static AutoLifecycle instance;

    private AutoLifecycle() {
    }

    public synchronized static AutoLifecycle getInstance() {
        if (instance == null) instance = new AutoLifecycle();
        return instance;
    }

    public void init(@NonNull final Object object, @NonNull ILifecycleProvider lifecycleProvider) {
        Class clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (final Method m : methods) {
            AutoLifecycleEvent event = m.getAnnotation(AutoLifecycleEvent.class);
            if (event == null) {
                continue;
            }
            m.setAccessible(true);

            registerFunctionOnLifecycle(lifecycleProvider, new Callable<Object>() {
                @Override
                public Object call() {
                    try {
                        m.invoke(object);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }, event.activity(), event.fragment());
        }
    }

    private void registerFunctionOnLifecycle(ILifecycleProvider lifecycleProvider, final Callable executable, ActivityLifecycle activityLifecycle, FragmentLifecycle fragmentLifecycle) {
        Observable wrapper = Observable.defer(new Callable<ObservableSource<?>>() {
            @Override
            public ObservableSource<?> call() throws Exception {
                executable.call();
                return Observable.empty();
            }
        });
        if (lifecycleProvider instanceof AppCompatActivity && activityLifecycle != ActivityLifecycle.NULL) {
            lifecycleProvider.executeWhen(wrapper, activityLifecycle);
        } else if (lifecycleProvider instanceof Fragment && fragmentLifecycle != FragmentLifecycle.NULL) {
            lifecycleProvider.executeWhen(wrapper, fragmentLifecycle);
        }
    }
}
