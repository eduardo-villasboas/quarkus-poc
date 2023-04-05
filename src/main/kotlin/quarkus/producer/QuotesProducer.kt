package quarkus.producer

import io.smallrye.mutiny.Multi
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.jboss.resteasy.reactive.RestStreamElementType
import org.slf4j.LoggerFactory
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.sse.OutboundSseEvent
import javax.ws.rs.sse.Sse


@Path("/quotes")
class QuotesProducer {

    @Channel("quote-requests")
    lateinit var quoteRequestEmitter: Emitter<String>

    @Channel("returned-quotes")
    lateinit var quotes: Multi<Any>

    /**
     * Endpoint to generate a new quote request id and send it to "quote-requests" channel (which
     * maps to the "quote-requests" RabbitMQ exchange) using the emitter.
     */
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @POST
    @Path("/request")
    @Produces(MediaType.TEXT_PLAIN)
    fun createRequest(): String {
        val uuid = UUID.randomUUID()
        logger.info("Sending quote [thread: ${threadIdentification()}, message: ${uuid}]")
        quoteRequestEmitter.send(uuid.toString())
        logger.info("Quote sent [thread: ${threadIdentification()}, message: ${uuid}]")
        return uuid.toString()
    }

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    fun stream(@Context sse: Sse): Multi<OutboundSseEvent> {
        return quotes.map {
            sse.newEventBuilder()
                .data(it)
                .name("update")
                .build()
        }
    }

    private fun threadIdentification() =
        "Thread.id=${Thread.currentThread().id},Thread.name=${Thread.currentThread().name}"
}