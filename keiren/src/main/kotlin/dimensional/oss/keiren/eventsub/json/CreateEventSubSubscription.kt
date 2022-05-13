package dimensional.oss.keiren.eventsub.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
public data class CreateEventSubSubscription(
    val type: String,
    val version: String = "1",
    val condition: JsonObject,
    val transport: Transport
) {
    @Serializable
    public data class Transport(
        val callback: String,
        val secret: String,
        val method: TransportMethod = TransportMethod.Webhook,
    )
}

@Serializable
public enum class TransportMethod {
    @SerialName("webhook")
    Webhook,
}
