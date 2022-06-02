package me.liuli.fluidity

import me.liuli.fluidity.command.CommandManager
import me.liuli.fluidity.config.ConfigManager
import me.liuli.fluidity.event.EventManager
import me.liuli.fluidity.module.ModuleManager
import me.liuli.fluidity.module.special.DiscordRPC
import me.liuli.fluidity.util.client.logError
import me.liuli.fluidity.util.client.logInfo
import me.liuli.fluidity.util.client.setTitle
import java.util.*
import kotlin.concurrent.thread

object Fluidity {

    val gitInfo = Properties().also {
        val inputStream = Fluidity::class.java.classLoader.getResourceAsStream("git.properties")
        if(inputStream != null) {
            it.load(inputStream)
        } else {
            throw RuntimeException("git.properties not found")
        }
    }

    @JvmField
    val NAME = "Fluidity"
    @JvmField
    val COLORED_NAME = "§3F§bluidity"
    @JvmField
    val VERSION = gitInfo["git.commit.id.abbrev"]?.let { "git-$it" } ?: "unknown"

    lateinit var eventManager: EventManager
    lateinit var configManager: ConfigManager
    lateinit var commandManager: CommandManager
    lateinit var moduleManager: ModuleManager

    fun init() {
        logInfo("Initialize $NAME v$VERSION")
        eventManager = EventManager()
    }

    fun load() {
        logInfo("Loading $NAME v$VERSION")
        setTitle("LoadClient")

        configManager = ConfigManager()
        eventManager.registerListener(configManager)

        commandManager = CommandManager()

        moduleManager = ModuleManager()
        eventManager.registerListener(moduleManager)

        configManager.loadDefault()

        thread {
            try {
                DiscordRPC.run()
            } catch (e: Throwable) {
                logError("Failed to load DiscordRPC.", e)
            }
        }

        setTitle("HaveFun")
    }

    fun shutdown() {
        configManager.save()
    }
}