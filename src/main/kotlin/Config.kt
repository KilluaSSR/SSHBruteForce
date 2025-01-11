/*
 * Coded by KilluaDev.kt
 */

package killua.dev

data class Config(
    var host: String,
    var port: Int = 22,
    var username: String,
    var wordlistPath: String,
    var thread: Int = 1,
    var timeout: Int = 30
)