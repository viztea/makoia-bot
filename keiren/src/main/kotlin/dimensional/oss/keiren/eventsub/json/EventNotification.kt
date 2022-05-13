package dimensional.oss.keiren.eventsub.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

public data class EventNotification(val subscription: Subscription, val event: Event) {
    public companion object Serializer : KSerializer<EventNotification> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EventNotification") {
            element<JsonObject>("subscription")
            element<JsonObject>("event")
        }

        override fun deserialize(decoder: Decoder): EventNotification {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: EventNotification) {
            TODO("Not yet implemented")
        }
    }
}
