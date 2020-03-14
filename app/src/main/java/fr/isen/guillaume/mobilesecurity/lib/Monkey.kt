package fr.isen.guillaume.mobilesecurity.lib

import android.app.ActivityManager

class Monkey {

    fun isUserAMonkey(): Boolean {
        return ActivityManager.isUserAMonkey()
    }
}