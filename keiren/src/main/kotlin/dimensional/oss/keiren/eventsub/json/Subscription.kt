package dimensional.oss.keiren.eventsub.json

import dimensional.oss.keiren.eventsub.SubscriptionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
public data class Subscription(
    val id: String,
    val status: String,
    val type: SubscriptionType,
    val cost: Int,
    val version: String,
    val condition: JsonObject,
    val transport: Transport,
    @SerialName("created_at")
    val createdAt: String
) {
    @Serializable
    public data class Transport(val method: TransportMethod, val callback: String)
}
