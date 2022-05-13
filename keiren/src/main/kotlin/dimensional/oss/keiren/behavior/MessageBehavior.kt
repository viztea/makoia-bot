package dimensional.oss.keiren.behavior

import dimensional.oss.keiren.TwitchObject

public interface MessageBehavior : TwitchObject {
    /**
     * The name of the channel this message was sent in.
     */
    public val channelName: String

    /**
     * The [ChannelBehavior] that this message belongs to.
     */
    public val channel: ChannelBehavior
        get() = ChannelBehavior(twitch, channelName)
}
