import dimensional.oss.keiren.chat.irc.IrcMessageParser
import dimensional.oss.keiren.chat.irc.IrcMessagePart

suspend fun main() {
    /*test("@badges=staff/1,broadcaster/1,turbo/1;color=#FF0000;display-name=PetsgomOO;emote-only=1;emotes=33:0-7;flags=0-7:A.6/P.6,25-36:A.1/I.2;id=c285c9ed-8b1b-4702-ae1c-c64d76cc74ef;mod=0;room-id=81046256;subscriber=0;turbo=0;tmi-sent-ts=1550868292494;user-id=81046256;user-type=staff :petsgomoo!petsgomoo@petsgomoo.tmi.twitch.tv PRIVMSG #petsgomoo :DansGame")
    test(":lovingt3s!lovingt3s@lovingt3s.tmi.twitch.tv PRIVMSG #lovingt3s :!dilly")
    test("PING :tmi.twitch.tv")
    test("@historical=1;badge-info=subscriber/4;" +
        "badges=subscriber/3,sub-gifter/1;color=#492F2F;" +
        "display-name=Billy_Bones_U;emotes=;flags=;id=d3805a32-df90-4844-a3ab" +
        "-4ea116fcf1c6;mod=0;room-id=11148817;subscriber=1;tmi-sent-ts=15656850" +
        "67248;turbo=0;user-id=411604091;user-type= :billy_bones_u!billy_bones_" +
        "u@billy_bones_u.tmi.twitch.tv PRIVMSG #pajlada :FeelsDankMan ...")
    test("PING :tmi.twitch.tv")
    test("@badge-info=;badges=broadcaster/1;client-nonce=997dcf443c31e258c1d32a8da47b6936;color=#0000FF;display-name=abc;emotes=;first-msg=0;flags=0-6:S.7;id=eb24e920-8065-492a-8aea-266a00fc5126;mod=0;room-id=713936733;subscriber=0;tmi-sent-ts=1642786203573;turbo=0;user-id=713936733;user-type= :abc!abc@abc.tmi.twitch.tv PRIVMSG #xyz :HeyGuys")
    test("@+example=raw+:=,escaped\\:\\s\\\\ :irc.example.com NOTICE #channel :Message")*/

    val parser = IrcMessageParser.parse("@badges=staff/1,broadcaster/1,turbo/1;color=#FF0000;display-name=PetsgomOO;emote-only=1;emotes=33:0-7;flags=0-7:A.6/P.6,25-36:A.1/I.2;id=c285c9ed-8b1b-4702-ae1c-c64d76cc74ef;mod=0;room-id=81046256;subscriber=0;turbo=0;tmi-sent-ts=1550868292494;user-id=81046256;user-type=staff :petsgomoo!petsgomoo@petsgomoo.tmi.twitch.tv PRIVMSG #petsgomoo :DansGame")
    val tags = parser.find { it is IrcMessagePart.Tags } as IrcMessagePart.Tags
    println(tags.tags.entries.joinToString("\n") { (k, v) -> "$k\n$v\n" })
}

suspend fun test(content: String) {
    println("\n$content")
    val parts = IrcMessageParser.parse(content)
    println(parts.joinToString("\n"))
}
