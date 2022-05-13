package dimensional.oss.keiren.eventsub.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


public interface RequestCondition {
    @Serializable
    public data class BroadcasterId(@SerialName("broadcaster_user_id") val id: String) : RequestCondition

    @Serializable
    public data class ChannelRaid(
        @SerialName("from_broadcaster_user_id")
        val fromBroadcasterId: String? = null,
        @SerialName("to_broadcaster_user_id")
        val toBroadcasterId: String? = null,
    ) : RequestCondition

    @Serializable
    public data class ChannelPointsCustomReward(
        @SerialName("broadcaster_user_id")
        val broadcasterId: String,
        @SerialName("reward_id")
        val rewardId: String? = null
    )

    @Serializable
    public data class DropEntitlementGrant(
        @SerialName("organization_id")
        val organizationId: String,
        @SerialName("category_id")
        val categoryId: String? = null,
        @SerialName("campaign_id")
        val campaignId: String? = null,
    )
}
