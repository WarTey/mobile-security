package fr.isen.guillaume.mobilesecurity.lib

class Runtime {

    fun isHooked(): Boolean {
        try {
            throw Exception()
        } catch (e: Exception) {
            var zygoteInitCallCount = 0
            for (stackTraceElement in e.stackTrace) {
                if (stackTraceElement.className == "com.android.internal.os.ZygoteInit") {
                    zygoteInitCallCount++
                    if (zygoteInitCallCount == 2)
                        return true
                }
                if ((stackTraceElement.className == "com.saurik.substrate.MS$2" && stackTraceElement.methodName == "invoked"))
                    return true
                if ((stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "main"))
                    return true
                if ((stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "handleHookedMethod"))
                    return true

            }
        }
        return false
    }
}