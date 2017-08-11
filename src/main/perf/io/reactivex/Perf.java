package io.reactivex;

import com.google.gwt.core.client.EntryPoint;

public class Perf implements EntryPoint {
    @Override public void onModuleLoad() {
        Observable.range(1, 1_000_000).count().subscribe();
    }
}
