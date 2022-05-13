package dimensional.makoia.bot.event

sealed class MakoiaTwitchEvent : MakoiaEvent {
    object Connected : MakoiaTwitchEvent()

    object Disconnected : MakoiaTwitchEvent()
}
