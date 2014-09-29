package rx;

import static java.lang.System.out;

import com.google.gwt.junit.client.GWTTestCase;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class RxGwtTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "rx.Rx";
    }

    public void testHowTo() {
        assertEquals(Observable.just("a", "b").toBlocking().last(), "b");
        Observable.range(1, 10)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer % 2 == 0;
                    }
                })
                .reduce(0, new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        out.println("finally" + integer);
                    }
                })
                .subscribe();
    }
}
