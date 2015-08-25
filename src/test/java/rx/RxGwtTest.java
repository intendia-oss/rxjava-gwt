package rx;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.gwt.junit.client.GWTTestCase;
import java.util.concurrent.atomic.AtomicInteger;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class RxGwtTest extends GWTTestCase {

    private Action1<Throwable> onError = new Action1<Throwable>() {
        @Override public void call(Throwable throwable) {
            reportUncaughtException(throwable);
        }
    };

    @Override
    public String getModuleName() {
        return "rx.Rx";
    }

    public void test_compatible_operators() {
        delayTestFinish(1000);
        Observable.range(1, 10)
                .observeOn(Schedulers.computation())
                .single()
                .onErrorResumeNext(Observable.<Integer>empty().concatWith(Observable.just(10)))
                .doOnError(onError)
                .skip(1)
                .lastOrDefault(0)
                .subscribe(new Action1<Integer>() {
                    @Override public void call(Integer x) {
                        assertEquals(0, x.intValue());
                        finishTest();
                    }
                });
    }

    public void test_that_range_filter_reduce_works() {
        delayTestFinish(1000);
        Observable.range(1, 10)
                .doOnError(onError)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer x) {
                        return x % 2 == 0;
                    }
                })
                .reduce(0, new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer acc, Integer x) {
                        return acc + x;
                    }
                })
                .subscribe(new Action1<Number>() {
                    @Override public void call(Number x) {
                        assertEquals(30, x.intValue());
                        finishTest();
                    }
                });
    }

    public void test_that_interval_works() {
        delayTestFinish(1000);
        Observable.interval(10, MILLISECONDS)
                .doOnError(onError)
                .take(10)
                .map(new Func1<Long, Long>() {
                    @Override public Long call(Long aLong) {
                        return aLong + 1;
                    }
                })
                .last()
                .subscribe(new Action1<Number>() {
                    @Override public void call(Number x) {
                        assertEquals(10, x.intValue());
                        finishTest();
                    }
                });
    }

    public void test_that_retry_and_trampoline_works() {
        final Observable<String> source = Observable.just("error", "success");
        final AtomicInteger onSubscription = new AtomicInteger();
        final AtomicInteger onSuccess = new AtomicInteger();
        final AtomicInteger onError = new AtomicInteger();

        final ConnectableObservable<String> publish = source.publish();
        publish.doOnSubscribe(new Action0() {
            @Override public void call() {
                onSubscription.incrementAndGet();
            }
        }).map(new Func1<String, String>() {
            @Override public String call(String n) {
                if (n.equals("error")) {
                    throw new RuntimeException();
                } else {
                    return n;
                }
            }
            // retry uses trampoline scheduler, so func must be called synchronous
        }).retry(new Func2<Integer, Throwable, Boolean>() {
            @Override public Boolean call(Integer integer, Throwable throwable) {
                onError.incrementAndGet();
                return true;
            }
        }).subscribe(new Action1<String>() {
            @Override public void call(String s) {
                onSuccess.incrementAndGet();
            }
        });
        publish.connect();

        assertEquals(2, onSubscription.get());
        assertEquals(1, onError.get());
        assertEquals(1, onSuccess.get());
    }
}
