package quarkus.consumer

import io.quarkus.vertx.ConsumeEvent
import io.smallrye.reactive.messaging.annotations.Blocking
import io.smallrye.reactive.messaging.annotations.Broadcast
import kotlinx.coroutines.*
import org.eclipse.microprofile.reactive.messaging.Acknowledgment
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.Outgoing
import org.slf4j.LoggerFactory
import quarkus.Quote
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.ObservesAsync
import javax.ws.rs.Consumes

/**
 * A bean consuming data from the "quote-requests" RabbitMQ queue and giving out a random quote.
 * The result is pushed to the "quotes" RabbitMQ exchange.
 */
@ApplicationScoped
class QuoteConsumer {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val random = Random()

    private val threadPool = Executors.newFixedThreadPool(4)
    @Incoming("requests")
    @Outgoing("quotes")
    @Acknowledgment(Acknowledgment.Strategy.NONE)
    @Throws(
        InterruptedException::class
    )
    fun process(quoteRequest: Message<String>): Quote {
        // simulate some hard-working task
/*
        logger.info("1 - Starting thread [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
        val future: Future<Int> = threadPool.submit(Callable {
            logger.info("Initiate thread [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
            Thread.sleep(1000)
            logger.info("Finalizing thread [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
            random.nextInt(100)
        })
        logger.info("Processing [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
        quoteRequest.ack()
        val value = future.get()
        logger.info("Processing finished [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
*/
        logger.info("Starting process [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
        Thread.sleep(1000)
        val value = random.nextInt(100)
        logger.info("Finishing process [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
        return Quote(quoteRequest.payload, value).also {
            quoteRequest.ack()
        }
    }

    private fun threadIdentification() = "Thread.id=${Thread.currentThread().id},Thread.name=${Thread.currentThread().name}"

}