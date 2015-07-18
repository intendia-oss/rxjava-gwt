/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rx.internal.operators;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Returns an observable sequence that stays connected to the source as long as
 * there is at least one subscription to the observable sequence.
 *
 * @param <T>
 *            the value type
 */
public final class OnSubscribeRefCount<T> implements OnSubscribe<T> {

    private final ConnectableObservable<? extends T> source;
    private volatile CompositeSubscription baseSubscription = new CompositeSubscription();
    private int subscriptionCount = 0;

    /**
     * Constructor.
     *
     * @param source
     *            observable to apply ref count to
     */
    public OnSubscribeRefCount(ConnectableObservable<? extends T> source) {
        this.source = source;
    }

    @Override
    public void call(final Subscriber<? super T> subscriber) {

        if (++subscriptionCount == 1) {
            source.connect(onSubscribe(subscriber));
        } else {
            // ready to subscribe to source so do it
            doSubscribe(subscriber, baseSubscription);
        }

    }

    private Action1<Subscription> onSubscribe(final Subscriber<? super T> subscriber) {
        return new Action1<Subscription>() {
            @Override
            public void call(Subscription subscription) {
                baseSubscription.add(subscription);
                // ready to subscribe to source so do it
                doSubscribe(subscriber, baseSubscription);
            }
        };
    }

    void doSubscribe(final Subscriber<? super T> subscriber, final CompositeSubscription currentBase) {
        // handle unsubscribing from the base subscription
        subscriber.add(disconnect(currentBase));

        source.unsafeSubscribe(new Subscriber<T>(subscriber) {
            @Override public void onError(Throwable e) {
                cleanup();
                subscriber.onError(e);
            }
            @Override public void onNext(T t) {
                subscriber.onNext(t);
            }
            @Override public void onCompleted() {
                cleanup();
                subscriber.onCompleted();
            }
            void cleanup() {
                // on error or completion we need to unsubscribe the base subscription
                // and set the subscriptionCount to 0 
                if (baseSubscription == currentBase) {
                    baseSubscription.unsubscribe();
                    baseSubscription = new CompositeSubscription();
                    subscriptionCount = 0;
                }
            }
        });
    }

    private Subscription disconnect(final CompositeSubscription current) {
        return Subscriptions.create(new Action0() {
            @Override
            public void call() {
                if (baseSubscription == current) {
                    if (--subscriptionCount == 0) {
                        baseSubscription.unsubscribe();
                        // need a new baseSubscription because once
                        // unsubscribed stays that way
                        baseSubscription = new CompositeSubscription();
                    }
                }
            }
        });
    }
}
