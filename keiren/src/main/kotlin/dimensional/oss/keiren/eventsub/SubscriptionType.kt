package dimensional.oss.keiren.eventsub

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Different types of subscriptions.
 * See [SubscriptionTypes](https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types)
 */
@Serializable(with = SubscriptionType.Companion::class)
public sealed class SubscriptionType(public val name: String) {
    public object ChannelUpdate : SubscriptionType("channel.update")
    public object ChannelFollow : SubscriptionType("channel.follow")
    public object ChannelSubscribe : SubscriptionType("channel.subscribe")
    public object ChannelSubscriptionEnd : SubscriptionType("channel.subscription.end")
    public object ChannelSubscriptionGift : SubscriptionType("channel.subscription.gift")
    public object ChannelSubscriptionMessage : SubscriptionType("channel.subscription.message")
    public object ChannelCheer : SubscriptionType("channel.cheer")
    public object ChannelRaid : SubscriptionType("channel.raid")
    public object ChannelBan : SubscriptionType("channel.ban")
    public object ChannelUnban : SubscriptionType("channel.unban")
    public object ChannelModeratorAdd : SubscriptionType("channel.moderator.add")
    public object ChannelModeratorRemove : SubscriptionType("channel.moderator.remove")
    public object ChannelPointsCustomRewardAdd : SubscriptionType("channel.channel_points_custom_reward.add")
    public object ChannelPointsCustomRewardUpdate : SubscriptionType("channel.channel_points_custom_reward.update")
    public object ChannelPointsCustomRewardRemove : SubscriptionType("channel.channel_points_custom_reward.remove")
    public object ChannelPointsCustomRewardRedemptionAdd :
        SubscriptionType("channel.channel_points_custom_reward_redemption.add")

    public object ChannelPointsCustomRewardRedemptionUpdate :
        SubscriptionType("channel.channel_points_custom_reward_redemption.update")

    public object ChannelPollBegin : SubscriptionType("channel.poll.begin")
    public object ChannelPollProgress : SubscriptionType("channel.poll.progress")
    public object ChannelPollEnd : SubscriptionType("channel.poll.end")
    public object ChannelPredictionBegin : SubscriptionType("channel.prediction.begin")
    public object ChannelPredictionProgress : SubscriptionType("channel.prediction.progress")
    public object ChannelPredictionLock : SubscriptionType("channel.prediction.lock")
    public object ChannelPredictionEnd : SubscriptionType("channel.prediction.end")
    public object DropEntitlementGrant : SubscriptionType("drop.entitlement.grant")
    public object ExtensionBitsTransactionCreate : SubscriptionType("extension.bits_transaction.create")
    public object GoalBegin : SubscriptionType("channel.goal.begin")
    public object GoalProgress : SubscriptionType("channel.goal.progress")
    public object GoalEnd : SubscriptionType("channel.goal.end")
    public object HypeTrainBegin : SubscriptionType("channel.hype_train.begin")
    public object HypeTrainProgress : SubscriptionType("channel.hype_train.progress")
    public object HypeTrainEnd : SubscriptionType("channel.hype_train.end")
    public object StreamOnline : SubscriptionType("stream.online")
    public object StreamOffline : SubscriptionType("stream.offline")
    public object UserAuthorizationGrant : SubscriptionType("user.authorization.grant")
    public object UserAuthorizationRevoke : SubscriptionType("user.authorization.revoke")
    public object UserUpdate : SubscriptionType("user.update")

    public class Unknown(name: String) : SubscriptionType(name) {
        override fun toString(): String = "UnknownSubscriptionType(name=$name)"
    }

    public companion object : KSerializer<SubscriptionType> {
        public val ALL: List<SubscriptionType>
            get() = listOf(ChannelUpdate,
                ChannelFollow,
                ChannelSubscribe,
                ChannelSubscriptionEnd,
                ChannelSubscriptionGift,
                ChannelSubscriptionMessage,
                ChannelCheer,
                ChannelRaid,
                ChannelBan,
                ChannelUnban,
                ChannelModeratorAdd,
                ChannelModeratorRemove,
                ChannelPointsCustomRewardAdd,
                ChannelPointsCustomRewardUpdate,
                ChannelPointsCustomRewardRemove,
                ChannelPointsCustomRewardRedemptionAdd,
                ChannelPointsCustomRewardRedemptionUpdate,
                ChannelPollBegin,
                ChannelPollProgress,
                ChannelPollEnd,
                ChannelPredictionBegin,
                ChannelPredictionProgress,
                ChannelPredictionLock,
                ChannelPredictionEnd,
                DropEntitlementGrant,
                ExtensionBitsTransactionCreate,
                GoalBegin,
                GoalProgress,
                GoalEnd,
                HypeTrainBegin,
                HypeTrainProgress,
                HypeTrainEnd,
                StreamOnline,
                StreamOffline,
                UserAuthorizationGrant,
                UserAuthorizationRevoke,
                UserUpdate)

        public fun valueOf(name: String): SubscriptionType =
            ALL.firstOrNull { type -> type.name == name } ?: Unknown(name)

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SubscriptionType", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): SubscriptionType {
            val name = decoder.decodeString()
            return ALL.find { it.name == name } ?: Unknown(name)
        }

        override fun serialize(encoder: Encoder, value: SubscriptionType) {
            encoder.encodeString(value.name)
        }
    }
}
