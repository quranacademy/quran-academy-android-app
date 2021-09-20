package org.quranacademy.quran.presentation.systemmanager

import android.app.Activity
import org.quranacademy.quran.domain.commons.SystemManager
import java.util.*

class AndroidSystemManager : SystemManager {

    private var activity: Activity? = null
    private var commandsQueue: Queue<SystemManagerCommand> = LinkedList()
    private var isExecutingCommands: Boolean = false

    override fun attachActivity(activity: Activity) {
        this.activity = activity
        executeQueuedCommands()
    }

    override fun detachActivity() {
        this.activity = null
    }

    override fun copyText(text: String) {
        runCommand(CopyTextCommand(text))
    }

    override fun shareText(text: String) {
        runCommand(ShareTextCommand(text))
    }

    private fun runCommand(command: SystemManagerCommand) {
        commandsQueue.add(command)
        executeQueuedCommands()
    }

    private fun executeQueuedCommands() {
        if (isExecutingCommands) {
            return
        }

        isExecutingCommands = true
        var activity = this.activity
        while (activity != null && hasPendingCommands()) {
            val command = commandsQueue.remove()
            command.execute(activity)
            activity = this.activity
        }
        isExecutingCommands = false
    }

    private fun hasPendingCommands(): Boolean {
        return !commandsQueue.isEmpty()
    }

}
