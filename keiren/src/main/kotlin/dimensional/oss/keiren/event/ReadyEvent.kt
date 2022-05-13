package dimensional.oss.keiren.event

import dimensional.oss.keiren.Twitch

public data class ReadyEvent(override val twitch: Twitch) : TwitchEvent()
