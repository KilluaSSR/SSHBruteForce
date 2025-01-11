/*
 * Coded by KilluaDev.kt
 */

package killua.dev

import java.io.File

fun init(config: Config, args: Array<String>): Config {
    args.indices.forEach { i ->
        when(args[i]) {
            "-h" -> args.getOrNull(i + 1)?.also { config.host = it }
            "-p" -> args.getOrNull(i + 1)?.toIntOrNull()?.also { config.port = it }
            "-u" -> args.getOrNull(i + 1)?.also { config.username = it }
            "-w" -> args.getOrNull(i + 1)?.also { config.wordlistPath = it }
            "-th" -> args.getOrNull(i + 1)?.toIntOrNull()?.also { config.thread = it }
            "-t" -> args.getOrNull(i + 1)?.toIntOrNull()?.also { config.timeout = it }
        }
    }
    return config
}
fun checkValid(config: Config) = with(config) {
    listOf(host, username, wordlistPath).none { it.isBlank() }
}
fun readWordList(wordlistPath: String): List<String> = File(wordlistPath).useLines {
    it.filter ( String::isNotBlank ).toList()
}