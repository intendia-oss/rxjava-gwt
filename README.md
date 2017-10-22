# RxJava GWT

[![Maven Central][mavenbadge-svg]][mavenbadge]
[![Build Status][cibadge-svg]][cibadge]
[![Join the chat][chatbadge-svg]][chatbadge]

Compatibility pack and minimal GWT dependant code to make [RxJava](https://github.com/ReactiveX/RxJava) works in GWT.
This includes, some missing JDK emulation (most of them from java.util.concurrent). Some minor changes from RxJava
that might be eventually applied directly to RxJava. And finally some super-sources and a JavaScript friendly Scheduler
to adapt RxJava to the browser.

**WARNING** GWT version uses JDT 3.11 which contains a [bug](https://bugs.eclipse.org/bugs/show_bug.cgi?id=521438) 
that makes rxjava 2.x compilation super-slow. This has been fixed but should be integrated first in JDT and later on in
the GWT compiler, in the mean time this repo includes a patched version of the last GWT stable version. To use this 
patched version you need to add the next repository definition and use the GWT version `2.8.2-rx1`.
```
<repository>
    <id>rxjava-gwt-repo</id>
    <url>https://raw.githubusercontent.com/intendia-oss/rxjava-gwt/mvn-repo/</url>
</repository>
```

## Getting started

The best way to getting started with RxJava GWT is to download and run some of the examples like 
[RxSnake](https://github.com/ibaca/rxsnake-gwt), [RxBreakout](https://github.com/ibaca/rxbreakout-gwt), 
[RxCanvas](https://github.com/ibaca/rxcanvas-gwt) or just finding for 
[rxjava+gwt in github](https://github.com/search?utf8=%E2%9C%93&q=topic%3Arxjava+topic%3Agwt+&type=Repositories). 
Most of them can be tried out cloning and executing `mvn gwt:devmode`.                                    

You should note that most of the GWT specific utilities are in [RxGWT](https://github.com/intendia-oss/rxgwt). So you 
probably want to depend on RxGWT instead of RxJava GWT, which includes common utils like event handlers adapter, 
request adapters, more advanced schedulers, etc. All those examples depends on RxGWT.

RxGWT contains basic utilities to make arbitrary XHR request, but if you want a typed API or you want to communicate 
between client and server you should take a look to 
[AutoREST](https://github.com/intendia-oss/autorest), a REST API lib with RxJava support. 
[Nominatim](https://github.com/ibaca/autorest-nominatim-example) is a example using 
the same JAX-RS and RxJava friendly API in JRE (client), JEE (server), Android and GWT. 

If you use GWT RPC, you can use [AutoRPC](https://github.com/intendia-oss/autorpc-gwt) to generate RxJava friendly 
async interfaces automatically. 

## Download

Releases are deployed to [the Central Repository][releases]

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

 [releases]: https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.intendia.gwt%22%20AND%20a%3A%22rxjava-gwt%22
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/

## Goals

 * Use same RxJava API in the client side
 * Improve RxJava GWT performance (emulate classes)
 * Make original RxJava project GWT compatible
 
## Source code folders 

 * `src/main/java` GWT specific code
 * `src/main/super` GWT JDK emulations required by RxJava
 * `src/main/modified` Required changes on original RxJava (https://github.com/ibaca/RxJava/tree/2.x-gwt)
 
 The 'super' and 'modified' folders are both super-sources, but they are separated because the 'super' contains 
 emulations that can be eventually merged into GWT and 'modified' contains RxJava changes that can be eventually
 merged into RxJava project. Leaving this project with only GWT specific code and just a few super sources to 
 fix scheduling incompatibilities and reasonable performance improvements.   

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

## Why I start this project?

>This is an old story not useful to understand anything about the project, hehe but I like to remember why I started 
it :bowtie:

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

// advanced: request 10 files each time search box changes, canceling previous and showing the results
Observable<String> searchText = RxUser.bindValueChange(searchBox);
Observable<List<String>> searchResult = searchText
    .switchMap(matches -> filesService
        .files(matches).observeOn(Schedulers.compute())
        .map(Tools::heavyWeightOperation).take(10).toList())
    .share();
searchResult.subscribe(resultBox::showFiles);    
```
This is pretty awesome and powerful, as RxJava requires subscription and notify un-subscriptions makes request 
composition easy and safer as you can clean up resources without an annoying API if the request is canceled before
it end up. Also, if you have some heavy weight operation, you can process it progressively without locking up the 
main loop (in the example above done using the `observeOn` operator), and finally reduce the result into a list to 
show the final result in a `resultBox`. 

**Note:** [AutoREST GWT](https://github.com/intendia-oss/autorest) implements 
the shareable JAX-RS interface using RxJava in the client side and [RxGWT](https://github.com/intendia-oss/rxgwt)
exposes a lot of common tools like the 'RxUser.bindValueChange' in the code example. 


 [mavenbadge]: https://maven-badges.herokuapp.com/maven-central/com.intendia.gwt/rxjava2-gwt
 [mavenbadge-svg]: https://maven-badges.herokuapp.com/maven-central/com.intendia.gwt/rxjava2-gwt/badge.svg
 [cibadge]: https://travis-ci.org/intendia-oss/rxjava-gwt
 [cibadge-svg]: https://travis-ci.org/intendia-oss/rxjava-gwt.svg
 [chatbadge]: https://gitter.im/intendia-oss/rxjava-gwt?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
 [chatbadge-svg]: https://badges.gitter.im/intendia-oss/rxjava-gwt.svg
