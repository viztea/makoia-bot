package dimensional.makoia.twitch.behavior

import dimensional.makoia.twitch.TwitchObject

interface MessageBehavior : TwitchObject {
    /**
     * The name of the channel this message was sent in.
     */
    val channelName: String

    /**
     * The [ChannelBehavior] that this message belongs to.
     */
    val channel: ChannelBehavior
        get() = ChannelBehavior(twitch, channelName)
}
