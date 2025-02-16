package base.boudicca.api.eventcollector.collections

import base.boudicca.api.eventcollector.EventCollector
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.concurrent.atomic.AtomicReference

object Collections {

    private val currentFullCollection = AtomicReference<FullCollection>()
    private val currentSingleCollections = ThreadLocal<SingleCollection>()
    private val currentHttpCalls = ThreadLocal<HttpCall>()
    private val pastFullCollections = Collections.synchronizedList(mutableListOf<FullCollection>())
    private val LOG = LoggerFactory.getLogger(this::class.java)

    fun startFullCollection() {
        if (currentFullCollection.get() != null) {
            LOG.error("a current full collection is already set, this seems like a bug")
        }

        val fullCollection = FullCollection()
        fullCollection.startTime = System.currentTimeMillis()
        currentFullCollection.set(fullCollection)
    }

    fun endFullCollection() {
        val fullCollection = currentFullCollection.get()
        if (fullCollection == null) {
            LOG.error("no full single collection available, cannot end it")
            return
        }
        fullCollection.endTime = System.currentTimeMillis()
        currentFullCollection.set(null)
        pastFullCollections.add(fullCollection)
    }

    fun startSingleCollection(collector: EventCollector) {
        if (currentSingleCollections.get() != null) {
            LOG.error("a current single collection is already set, this seems like a bug")
        }
        val singleCollection = SingleCollection()
        singleCollection.startTime = System.currentTimeMillis()
        singleCollection.collector = collector
        currentFullCollection.get()?.singleCollections?.add(singleCollection)
        currentSingleCollections.set(singleCollection)
    }

    fun endSingleCollection() {
        val singleCollection = currentSingleCollections.get()
        if (singleCollection == null) {
            LOG.error("no current single collection available, cannot end it")
            return
        }
        singleCollection.endTime = System.currentTimeMillis()
        currentSingleCollections.set(null)
    }

    fun startHttpCall(url: String, postData: String? = null) {
        if (currentHttpCalls.get() != null) {
            LOG.error("a current http call is already set, this seems like a bug")
        }
        val httpCall = HttpCall()
        httpCall.startTime = System.currentTimeMillis()
        httpCall.url = url
        httpCall.postData = postData
        currentSingleCollections.get()?.httpCalls?.add(httpCall)
        currentHttpCalls.set(httpCall)
    }

    fun endHttpCall(responseCode: Int) {
        val httpCall = currentHttpCalls.get()
        if (httpCall == null) {
            LOG.error("no current http call available, cannot end it")
            return
        }
        httpCall.endTime = System.currentTimeMillis()
        httpCall.responseCode = responseCode
        currentHttpCalls.set(null)
    }

    fun resetHttpTiming() {
        val httpCall = currentHttpCalls.get()
        if (httpCall == null) {
            LOG.error("no current http call available, cannot reset timing")
            return
        }
        httpCall.startTime = System.currentTimeMillis()
    }

    fun getAllPastCollections(): List<FullCollection> {
        return pastFullCollections.toList()
    }

    fun getCurrentFullCollection(): FullCollection? {
        return currentFullCollection.get()
    }

    fun getCurrentSingleCollection(): SingleCollection? {
        return currentSingleCollections.get()
    }
}