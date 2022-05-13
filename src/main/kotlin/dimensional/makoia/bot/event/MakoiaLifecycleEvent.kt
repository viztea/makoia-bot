package dimensional.makoia.bot.event

sealed class MakoiaLifecycleEvent : MakoiaEvent {
    object Initializing : MakoiaLifecycleEvent()

    object Initialized : MakoiaLifecycleEvent()

    object ShuttingDown : MakoiaLifecycleEvent()
}
