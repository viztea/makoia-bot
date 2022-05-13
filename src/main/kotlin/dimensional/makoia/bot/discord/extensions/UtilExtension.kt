package dimensional.makoia.bot.discord.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder

class UtilExtension : Extension() {
    override val name: String = "util"

    override suspend fun setup() {
        publicSlashCommand {
            name = "ping"
            description = "Displays the bots latency"

            action {
                val heartbeat = this@publicSlashCommand.kord.gateway.averagePing?.inWholeMilliseconds
                    ?.let {  "*${it}ms*" }
                    ?: "N/A"

                respond {
                    embeds += buildEmbed {
                        color = Color(0x090914)
                        description = "Pong! **Heartbeat:** $heartbeat"
                    }
                }
            }
        }
    }
}

inline fun buildEmbed(build: EmbedBuilder.() -> Unit): EmbedBuilder {
    return EmbedBuilder()
        .apply(build)
}
