package dimensional.oss.keiren.credentials

public interface TwitchCredentials {
    public val clientId: String

    public suspend fun refresh()

    public suspend fun getHeader(): String?
}
