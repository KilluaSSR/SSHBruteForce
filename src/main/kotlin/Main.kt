package killua.dev

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) =  runBlocking {
    val config = Config("",22,"","")
    init(config,args)
    if(!checkValid(config)){
        Exception("Invalid config")
    }
    val wordlist = readWordList(config.wordlistPath)
    config.bruteforce(wordlist)
}