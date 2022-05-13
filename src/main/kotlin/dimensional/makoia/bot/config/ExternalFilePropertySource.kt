package dimensional.makoia.bot.config

import com.sksamuel.hoplite.*
import com.sksamuel.hoplite.fp.invalid
import com.sksamuel.hoplite.fp.valid
import java.io.File

/**
 * An implementation of [PropertySource] that loads values from a file outside the classpath.
 *
 * @param optional if true, a missing file would be skipped. If false, then a missing
 * file will cause the config to fail. Defaults to false.
 */
public class ExternalFilePropertySource(
    // for external files not on the classpath
    private val filepath: String,
    private val optional: Boolean = false
) : PropertySource {
    override fun source(): String {
        return "External File"
    }

    // first we validate if file exists
    override fun node(context: PropertySourceContext): ConfigResult<Node> {
        return if (!File(filepath).exists()) {
            if (optional) Undefined.valid()
            else ConfigFailure.UnknownSource(filepath).invalid()
        } else {
            File(filepath).let { configFile ->
                context.parsers.locate(configFile.extension).map {
                    it.load(configFile.inputStream(), filepath)
                }
            }
        }
    }
}
