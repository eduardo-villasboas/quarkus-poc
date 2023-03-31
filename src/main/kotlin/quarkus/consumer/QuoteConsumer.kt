package quarkus.consumer

import io.reactivex.rxjava3.schedulers.Schedulers
import io.smallrye.reactive.messaging.annotations.Blocking
import io.vertx.core.VertxException
import io.vertx.rxjava3.core.Vertx
import org.eclipse.microprofile.reactive.messaging.*
import org.slf4j.LoggerFactory
import quarkus.Quote
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Produces

/**
 * A bean consuming data from the "quote-requests" RabbitMQ queue and giving out a random quote.
 * The result is pushed to the "quotes" RabbitMQ exchange.
 */
@ApplicationScoped
class QuoteConsumer {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val random = Random()

    @Channel("quotes")
    lateinit var quoteResultEmitter: Emitter<Quote>

    @Incoming("requests")
    @Blocking(value = "quote-request-thread-pool", ordered = false)
    @Throws(
        InterruptedException::class
    )
    fun process(quoteRequest: String) {

        logger.info("Starting process [thread: ${threadIdentification()}, message: ${quoteRequest}]")
        logger.info("Heavy process start [thread: ${threadIdentification()}, message: ${quoteRequest}]")
        Thread.sleep(1000)
        logger.info("Heavy process finish [thread: ${threadIdentification()}, message: ${quoteRequest}]")
        val value = random.nextInt(100)
        logger.info("Finishing process [thread: ${threadIdentification()}, message: ${quoteRequest}]")
        quoteResultEmitter.send(Quote(quoteRequest, value))

    }

    private fun threadIdentification() =
        "Thread.id=${Thread.currentThread().id},Thread.name=${Thread.currentThread().name}"

}