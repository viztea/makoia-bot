package dimensional.makoia.bot.event

sealed class MakoiaDiscordEvent : MakoiaEvent {
    object Ready : MakoiaDiscordEvent()
}
