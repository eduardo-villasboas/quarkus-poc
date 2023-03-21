package quarkus.consumer

import io.smallrye.reactive.messaging.annotations.Blocking
import kotlinx.coroutines.*
import org.eclipse.microprofile.reactive.messaging.*
import org.slf4j.LoggerFactory
import quarkus.Quote
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import javax.enterprise.context.ApplicationScoped

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

    private val executor = Executors.newFixedThreadPool(10)

    @Incoming("requests")
    @Acknowledgment(Acknowledgment.Strategy.NONE)
    @Blocking
    @Throws(
        InterruptedException::class
    )
    fun process(quoteRequest: Message<String>): CompletionStage<Void>? {
        logger.info("Starting process [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")

        executor.submit {
            Thread.sleep(3000)
            val value = random.nextInt(100)

            logger.info("Finishing process [thread: ${threadIdentification()}, message: ${quoteRequest.payload}]")
            quoteResultEmitter.send(Quote(quoteRequest.payload, value))
        }

        return quoteRequest.ack()
    }

    private fun threadIdentification() =
        "Thread.id=${Thread.currentThread().id},Thread.name=${Thread.currentThread().name}"

}