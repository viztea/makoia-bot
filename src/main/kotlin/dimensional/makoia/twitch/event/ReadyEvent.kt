package dimensional.makoia.twitch.event

import dimensional.makoia.twitch.Twitch

data class ReadyEvent(override val twitch: Twitch) : TwitchEvent()
