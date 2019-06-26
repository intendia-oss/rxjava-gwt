package io.reactivex;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import java.util.concurrent.atomic.AtomicInteger;

public class RxGwtTest extends GWTTestCase {
    private static final int TEST_THRESHOLD = 1000;
    private Consumer<Throwable> onError = new Consumer<Throwable>() {
        @Override public void accept(Throwable throwable) {
            reportUncaughtException(throwable);
        }
    };

    @Override
    public String getModuleName() {
        return "io.reactivex.RxJava";
    }

    public void test_compatible_operators() {
        delayTestFinish(TEST_THRESHOLD);
        Scheduler computation = Schedulers.computation();
        GWT.log("computation scheduler: " + computation);
        Observable.range(1, 10)
                .observeOn(computation)
                .singleOrError().toObservable()
                .onErrorResumeNext(Observable.<Integer>empty().concatWith(Observable.just(10)))
                .doOnError(onError)
                .skip(1)
                .last(0)
                .subscribe(new Consumer<Integer>() {
                    public void accept(Integer x) { assertEquals(0, x.intValue()); finishTest(); }
                });
    }

    public void test_that_range_filter_reduce_works() {
        delayTestFinish(TEST_THRESHOLD);
        Observable.range(1, 10)
                .observeOn(Schedulers.io())
                .doOnError(onError)
                .filter(new Predicate<Integer>() {
                    public boolean test(Integer x) { return x % 2 == 0; }
                })
                .reduce(0, new BiFunction<Integer, Integer, Integer>() {
                    public Integer apply(Integer acc, Integer x) { return acc + x; }
                })
                .subscribe(new Consumer<Number>() {
                    public void accept(Number x) { assertEquals(30, x.intValue()); finishTest(); }
                });
    }

    public void test_that_interval_works() {
        delayTestFinish(TEST_THRESHOLD);
        Observable.interval(2, MILLISECONDS)
                .doOnError(onError)
                .take(2)
                .map(new Function<Long, Long>() {
                    public Long apply(Long aLong) { return aLong + 1; }
                })
                .lastOrError()
                .subscribe(new Consumer<Number>() {
                    public void accept(Number x) { assertEquals(2, x.intValue()); finishTest(); }
                });
    }

    public void test_that_async_compiles() {
        delayTestFinish(TEST_THRESHOLD);
        final AsyncSubject<String> subject = AsyncSubject.create();
        Observable.just("unexpected", "expected").subscribe(new Consumer<String>() {
            public void accept(String n) { subject.onNext(n); }
        });
        subject.subscribe(new Consumer<String>() {
            public void accept(String n) { assertEquals("expected", n); finishTest(); }
        });
        subject.onComplete();
    }

    public void test_that_behaviour_compiles() {
        delayTestFinish(TEST_THRESHOLD);
        final BehaviorSubject<String> subject = BehaviorSubject.create();
        Observable.just("expected").subscribe(new Consumer<String>() {
            public void accept(String n) { subject.onNext(n); }
        });
        subject.subscribe(new Consumer<String>() {
            public void accept(String n) { assertEquals("expected", n); finishTest(); }
        });
    }

    public void test_that_retry_and_trampoline_works() {
        final Observable<String> source = Observable.just("error", "success");
        final AtomicInteger onSubscription = new AtomicInteger();
        final AtomicInteger onSuccess = new AtomicInteger();
        final AtomicInteger onError = new AtomicInteger();

        final ConnectableObservable<String> publish = source.publish();
        publish.doOnSubscribe(new Consumer<Disposable>() {
            @Override public void accept(Disposable d) throws Exception {
                onSubscription.incrementAndGet();
            }
        }).map(new Function<String, String>() {
            @Override public String apply(String n) {
                if (n.equals("error")) {
                    throw new RuntimeException();
                } else {
                    return n;
                }
            }
            // retry uses trampoline scheduler, so func must be called synchronous
        }).retry(new BiPredicate<Integer, Throwable>() {
            @Override public boolean test(Integer integer, Throwable throwable) {
                onError.incrementAndGet();
                return true;
            }
        }).subscribe(new Consumer<String>() {
            @Override public void accept(String s) {
                onSuccess.incrementAndGet();
            }
        });
        publish.connect();

        assertEquals(2, onSubscription.get());
        assertEquals(1, onError.get());
        assertEquals(1, onSuccess.get());
    }

    public void test_that_single_works() {
        delayTestFinish(TEST_THRESHOLD);
        Single.just("o")
                .zipWith(Single.create(new SingleOnSubscribe<String>() {
                    @Override public void subscribe(SingleEmitter<String> e) throws Exception {
                        e.onSuccess("_");
                    }
                }), new BiFunction<String, String, String>() {
                    @Override public String apply(String a, String b) {
                        return a + b;
                    }
                })
                .flatMap(new Function<String, SingleSource<String>>() {
                    @Override public Single<String> apply(final String a) {
                        return Observable.timer(10, MILLISECONDS)
                                .singleOrError()
                                .map(new Function<Long, String>() {
                                    @Override public String apply(Long b) {
                                        return a + b;
                                    }
                                });
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override public void accept(String x) {
                        assertEquals("o_0", x);
                        finishTest();
                    }
                });
    }
}
