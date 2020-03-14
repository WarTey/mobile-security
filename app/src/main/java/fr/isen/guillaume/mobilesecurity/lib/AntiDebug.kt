package fr.isen.guillaume.mobilesecurity.lib

import android.os.Debug
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

class AntiDebug {

    private val tracerPid = "TracerPid"

    private fun isBeingDebugged(): Boolean {
        return Debug.isDebuggerConnected()
    }

    @Throws(IOException::class)
    private fun hasTracerPid(): Boolean {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(FileInputStream("/proc/self/status")), 1000)
            var line: String
            while (reader.readLine().also { line = it } != null) {
                if (line.length > tracerPid.length) {
                    if (line.substring(0, tracerPid.length).equals(tracerPid, ignoreCase = true)) {
                        if (Integer.decode(line.substring(tracerPid.length + 1).trim { it <= ' ' }) > 0) {
                            return true
                        }
                        break
                    }
                }
            }
        } catch (exception: java.lang.Exception) { } finally {
            reader?.close()
        }
        return false
    }

    fun isDebugged(): Boolean {
        var tracer = false
        try {
            tracer = hasTracerPid()
        } catch (exception: Exception) { }
        return isBeingDebugged() || tracer
    }
}