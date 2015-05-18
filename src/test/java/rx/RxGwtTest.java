package rx;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.gwt.junit.client.GWTTestCase;
import java.util.List;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

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

    public void test_switchOnNext() {
        final PublishSubject<String> searchText = PublishSubject.create();
        final Observable<List<String>> searchResult = Observable
                .switchOnNext(searchText.map(new Func1<String, Observable<List<String>>>() {
                    @Override public Observable<List<String>> call(String text) {
                        return Observable.just(text + " response").take(10).toList();
                    }
                }))
                .share();
    }
}
