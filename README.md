# RxJava GWT [![Build Status](https://travis-ci.org/intendia-oss/rxjava-gwt.svg)](https://travis-ci.org/intendia-oss/rxjava-gwt)

[![Join the chat at https://gitter.im/intendia-oss/rxjava-gwt](https://badges.gitter.im/intendia-oss/rxjava-gwt.svg)](https://gitter.im/intendia-oss/rxjava-gwt?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Currently this project is just a patch over [RxJava](https://github.com/ReactiveX/RxJava) to made it 
works in GWT (client side).

## Download

Releases are deployed to [the Central Repository][releases]

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

 [releases]: https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.intendia.gwt%22%20AND%20a%3A%22rxjava-gwt%22
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/

## Goals

 * Use same RxJava API in the client side
 * Improve RxJava GWT performance (emulate classes)
 * GWT tools (observe from EventBus, HasValueChangeHandler, ...)
 
## Why I start this project?

My primary goal to start this project was to have a shared (between client and server) type safe Promise 
implementation. This promises most of the time are server request calls, and most of this request returns 
a collection of items (zero or one are collections too :grimacing:). So I end up with the conclusion that Observables 
was an elegant solution to my problem. With Observables you can define your shared API and provides a Promise like API 
to handle and compose responses (composition was the primary motivation to use promises, if you don't need composition 
callbacks are probably enough). So you can define service like this:  
```
public interface FilesService extends DirectRestService {
    @GET @Path("/files") public Observable<FileVO> files(@QueryParam("matches") String matches);
}
```
And can be used like:
```
// simple: request one file and show
filesService.files("/one.file").subscribe(resultBox::showFile);

// advanced: request 10 files each time search box changes, auto canceling previous request and showing the results
Observable<String> searchText = GwtObservable.fromHasValue(searchBox);
Observable<List<String>> searchResult = Observable
    .switchOnNext(searchText.map(matches -> filesService.files(matches).take(10).toList())
    .share();  // uniform API for single/multi responses and pagination --^
searchResult.subscribe(resultBox::showFiles);    
``` 
So, beside the shared and composable reactive API, Observables unify single/collection and paginated responses, i.e.
you don't need the common `getUser/getUsers` paired request and the `first/max` pagination params because you can just
use a single `observeUsers(query).take(max)`. This may not fit all situations, but looks good anyway.

**Note:** [AutoREST GWT](https://github.com/intendia-oss/autorest-gwt) implements 
the shareable JAX-RS interface using RxJava in the client side and [RxGWT](https://github.com/intendia-oss/rxgwt)
exposes a lot of common tools like the 'GwtObservable.fromHasValue' in the code example.  

 
## Source code folders 

 * `src/main/java` GWT specific code
 * `src/main/super` GWT super source to made RxJava classes works
 * `src/main/replaced` Same as super but generated using regex expressions to replace incompatible expressions
  
## Expressions used in replaced code

```groovy
task gwtResources(type: Copy) {
    from 'src/main/java';
    into 'build/super'

    include '**/*.java'
    exclude '**/schedulers/**' // GWT has only one custom scheduler
    exclude '**/RxThreadFactory.java' // uses Thread
    exclude '**/OperatorObserveOn.java' // uses unsafe and references ImmediateScheduler and TrampolineScheduler
    exclude '**/OnSubscribeCombineLatest.java' // uses BitSet
    exclude '**/Blocking*.java' // GWT unsupported
    exclude '**/BackpressureDrainManager.java' // Custom GWT version
    exclude { details -> details.file.isFile() && !(
                         details.file.text.contains('FieldUpdater.newUpdater') ||
                         details.file.text.contains('Collections.synchronized') ||
                         details.file.text.contains('ArrayDeque') ||
                         details.file.text.contains('Thread.currentThread().interrupt') ||
                         details.file.text.contains('Array.newInstance')) }

    includeEmptyDirs = false

    filter { line -> line
            .replaceAll('AtomicIntegerFieldUpdater.newUpdater\\((.*?).class, \"(.*?)\"\\);',
                'new rx.internal.util.GwtIntegerFieldUpdater<\$1>() {' +
                ' @Override protected int getter(\$1 obj) { return obj.\$2; }' +
                ' @Override protected void setter(\$1 obj, int update) { obj.\$2 = update; } };')

            .replaceAll('AtomicLongFieldUpdater.newUpdater\\((.*?).class, \"(.*?)\"\\);',
                'new rx.internal.util.GwtLongFieldUpdater<\$1>() {' +
                ' @Override protected long getter(\$1 obj) { return obj.\$2; }' +
                ' @Override protected void setter(\$1 obj, long update) { obj.\$2 = update; } };')

            .replaceAll('AtomicReferenceFieldUpdater.newUpdater\\((.*?).class, (.*?).class, \"(.*?)\"\\);',
                'new rx.internal.util.GwtReferenceFieldUpdater<\$1,\$2>() {' +
                ' @Override protected \$2 getter(\$1 obj) { return obj.\$3; }' +
                ' @Override protected void setter(\$1 obj, \$2 update) { obj.\$3 = update; } };')

            .replaceAll('Collections.synchronized(.*?)\\((.*?)\\);', '\$2;')
            .replaceAll('ArrayDeque(.*?)\\((.*?)\\)', 'LinkedList\$1()')
            .replaceAll('ArrayDeque', 'LinkedList')
            .replaceAll('Thread.currentThread\\(\\)\\.interrupt\\(\\);', '')
            .replaceAll('Array.newInstance\\([^,]*, (.*?)\\)', 'new Object[\$1]')
    }
}
```

## Profiling with d8

Install V8 and create aliases for d8 and xxx-tick-processor.

```
mvn -Dd8 package
cd target/d8/perf
d8 -prof --log-timer-events perf.nocache.js
tick-processor --source-map=../../gwt/deploy/perf/symbolMaps/<HASH>_sourceMap0.json v8.log
```

[Install V8 on Mac](https://gist.github.com/kevincennis)
[Profiling GWT applications with v8 and d8](http://blog.daniel-kurka.de/2014/01/profiling-gwt-applications-with-v8-and.html)
[Performance Tips for JavaScript in V8](http://www.html5rocks.com/en/tutorials/speed/v8/)


