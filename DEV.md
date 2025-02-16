# Developing

## Local Setup

We use Intellij Idea for which launch configs are found in the [.run](.run) folder.
You should be able to use other IDEs since our build is a simple multiproject Gradle build.
Please note that we require Java 21 to run.

### First step

After checkout please build the whole project with `gradlew build` to generate all openapi generated classes.

### Frontend only setup

If you only want to develop the frontend ([publisher-event-html](boudicca.base/publisher-event-html)) then you can use the `OnlineHtmlPublisher`
run config.

This run configuration starts the frontend which uses the online boudicca.events website as backend.

### Full setup

Run the compound launch config `Local Setup (without collectors)` which runs `LocalEventDB`,`LocalSearch`
and `LocalHtmlPublisher` (aka the frontend) all at once.

Then you can fill up the database with one of two options:

- (recommended) Run `LocalFetchFromOnlineBoudiccaKt` to fetch the events from the online boudicca.events website.
- (slow option): Run `BoudiccaEventCollectorsKt` to scrape all current events from the websites with the help of the
  event collectors

After the database is filled up, call http://localhost:8080 to see the application.

The local EventDB saves its data into the file `boudicca.store` in the root folder of the project. So if you want to
clean our EventDB, stop it, delete the file and restart it.

## Developing your own Collector

Most work currently open is creating new `EventCollectors` for collecting new events from new sites. To do this the
workflow is following:

1. In our [eventcollectors project](boudicca.events/eventcollectors/src/main/kotlin/events/boudicca/eventcollector/collectors) create a
   new EventCollector subclass which collects some events.
    1. A good starting point is always to look at existing EventCollectors and copy one of them.
    2. Take a look at our [Semantic Conventions](SEMANTIC_CONVENTIONS.md) overview where you can see the existing keys
       and their meanings you should adhere to.
2. Add your new collector
   in [LocalCollectorDebug.kt](boudicca.events/eventcollectors/src/main/kotlin/events/boudicca/eventcollector/LocalCollectorDebug.kt)
   and run the `LocalCollectorDebugKt` launch config (or class) to dry-run your test.
    1. A dry-run means events will be collected but not ingested somewhere. This is to make sure that the data looks
       sane before acutally sending it to a backend.
    2. The `LocalCollectorDebugKt` also starts the collectors overview at http://localhost:8083 where you can have a
       easier look at errors and what happened during your collection.
    3. You can also test the collection with enrichment by starting your own local enricher or using our hosted one
       at https://enricher.boudicca.events. For that uncomment one of the `boudicca.enricher.url=...` lines
       in [application.propertes](boudicca.events/eventcollectors/src/main/resources/application.properties)
3. After your dry-run was successful it is time to test it for real.
    1. First, add your new EventCollector to the
       class [BoudiccaEventCollectors.kt](boudicca.events/eventcollectors/src/main/kotlin/events/boudicca/eventcollector/BoudiccaEventCollectors.kt) (
       for testing you can/should comment out all other EventCollectors)
    2. Then start your local [Full Setup](#full-setup)
    3. You can follow the collection progress at http://localhost:8083
    4. After that is done have a look at http://localhost:8080 and see if everything works as designed (names, dates,
       pictures, links, ...)

### How a EventCollectors works

All EventCollectors are subclasses of the
interface [EventCollector](boudicca.base/eventcollector-api/src/main/kotlin/base/boudicca/api/eventcollector/EventCollector.kt).
In its simplest for the interface has only two methods you need to implement.

1. getName() which returns a simple name for the collector. The convention is all lowercase without any special
   characters nor spaces.
2. collectEvents() which returns a List
   of [Events](boudicca.base/eventcollector-api/src/main/kotlin/base/boudicca/api/eventcollector/Event.kt)

and this is all you need for EventCollector!

### Helper classes

But of course a lot of functionality is common to multiple EventCollectors, so we provide a lot of existing
functionality to help you.

#### TwoStepEventCollector

The [TwoStepEventCollector.kt](boudicca.base/eventcollector-api/src/main/kotlin/base/boudicca/api/eventcollector/TwoStepEventCollector.kt)
splits the work up into two steps:

1. getAllUnparsedEvents() which collects and returns a list of "unparsed events", whatever this is. This could for
   example be a list of URLs where the detailed event data is found.
2. parseMultipleEvents() or parseEvent(), which will be called for each item returned in the first step. they allow you
   to return a single or multiple events for one input.

One big advantage of the TwoStepCollector is automatic failure handling if one parseMultipleEvents() or parseEvent()
throws an exception, meaning that other events will still be processed. This also means you should do as little as
possible in the getAllUnparsedEvents() methods.

#### Fetcher

The [Fetcher.kt](boudicca.base/eventcollector-api/src/main/kotlin/base/boudicca/api/eventcollector/Fetcher.kt)
utility class allows you to easily fetch websites via http(s). It is highly recommended that you use this fetcher
because it also provides automatic delays in queries so that we do not overwhelm the server we scrape and it also sets
the User-Agent to `boudicca.events collector`.

#### Collections diagnostic data

TODO
