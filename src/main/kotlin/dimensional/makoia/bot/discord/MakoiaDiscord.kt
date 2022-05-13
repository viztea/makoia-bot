package dimensional.makoia.bot.discord

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import com.kotlindiscord.kord.extensions.commands.application.ApplicationCommandRegistry
import com.kotlindiscord.kord.extensions.commands.chat.ChatCommandRegistry
import com.kotlindiscord.kord.extensions.components.ComponentRegistry
import com.kotlindiscord.kord.extensions.components.callbacks.ComponentCallbackRegistry
import com.kotlindiscord.kord.extensions.i18n.TranslationsProvider
import com.kotlindiscord.kord.extensions.sentry.SentryAdapter
import com.kotlindiscord.kord.extensions.utils.loadModule
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dimensional.makoia.bot.Makoia
import dimensional.makoia.bot.config.Config
import dimensional.makoia.bot.discord.extensions.UtilExtension
import dimensional.makoia.bot.event.MakoiaDiscordEvent
import dimensional.makoia.bot.event.MakoiaLifecycleEvent
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.bind
import kotlin.reflect.full.declaredMemberProperties

class MakoiaDiscord : KoinComponent {
    companion object {
        val log = KotlinLogging.logger {  }
    }

    private val makoia by inject<Makoia>()
    private val config by inject<Config>()

    lateinit var bot: ExtensibleBot

    init {
        makoia.on<MakoiaLifecycleEvent.Initializing> {
            log.info { "* initializing discord bot..." }

            val botBuilder = ExternalExtensibleBotBuilder()
            with(botBuilder) {
                applicationCommands {
                    enabled = true
                    defaultGuild(958199310636437534uL)
                }

                extensions {
                    add { UtilExtension() }
                }

                hooks {
                    setup {
                        val kord = get<Kord>()
                        kord.on<ReadyEvent> {
                            log.info { "* makoia discord has logged in as ${self.tag}" }
                            makoia.events.emit(MakoiaDiscordEvent.Ready)
                        }
                    }
                }
            }

            bot = botBuilder.buildWithoutKoin(config.makoia.discord.token)
            bot.start()
        }
    }

    class ExternalExtensibleBotBuilder : ExtensibleBotBuilder() {
        suspend fun buildWithoutKoin(token: String): ExtensibleBot {
            hooksBuilder.beforeKoinSetup {
                if (pluginBuilder.enabled) {
                    loadPlugins()
                }

                deferredExtensionsBuilders.forEach { it(extensionsBuilder) }
            }


            hooksBuilder.runBeforeKoinSetup()

            loadModule { single { this@ExternalExtensibleBotBuilder } bind ExtensibleBotBuilder::class }

            /* fuck kordex and its grandmother */
            loadModule {
                single {
                    val property = I18nBuilder::class.declaredMemberProperties.first { it.name == "translationsProvider" }
                    property.get(i18nBuilder) as TranslationsProvider
                } bind TranslationsProvider::class
            }

            loadModule { single { chatCommandsBuilder.registryBuilder() } bind ChatCommandRegistry::class }

            loadModule { single { componentsBuilder.registryBuilder() } bind ComponentRegistry::class }

            loadModule { single { componentsBuilder.callbackRegistryBuilder() } bind ComponentCallbackRegistry::class }

            loadModule {
                single {
                    applicationCommandsBuilder.applicationCommandRegistryBuilder()
                } bind ApplicationCommandRegistry::class
            }

            loadModule {
                single {
                    val adapter = extensionsBuilder.sentryExtensionBuilder.builder()

                    if (extensionsBuilder.sentryExtensionBuilder.enable) {
                        extensionsBuilder.sentryExtensionBuilder.setupCallback(adapter)
                    }

                    adapter
                } bind SentryAdapter::class
            }

            hooksBuilder.runAfterKoinSetup()

            val bot = ExtensibleBot(this, token)
            loadModule {
                single { bot } bind ExtensibleBot::class
            }

            hooksBuilder.runCreated(bot)
            bot.setup()
            hooksBuilder.runSetup(bot)
            hooksBuilder.runBeforeExtensionsAdded(bot)

            extensionsBuilder.extensions.forEach {
                try {
                    bot.addExtension(it)
                } catch (e: Exception) {
                    logger.error(e) { "Failed to set up extension: $it" }
                }
            }

            if (pluginBuilder.enabled) {
                startPlugins()
            }

            hooksBuilder.runAfterExtensionsAdded(bot)
            return bot
        }
    }
}
