package fr.isen.guillaume.mobilesecurity.lib

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class Emulator {

    private val knownPipes = arrayOf("/dev/socket/qemud", "/dev/qemu_pipe")
    private val knownFiles = arrayOf("/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props")
    private val knownGenyFiles = arrayOf("/dev/socket/genyd", "/dev/socket/baseband_genyd")
    private val knownQemuDrivers = arrayOf("goldfish")

    private fun hasPipes(): Boolean {
        for (pipe in knownPipes) {
            val qemuSocket = File(pipe)
            if (qemuSocket.exists()) {
                return true
            }
        }
        return false
    }

    private fun hasQEmuFiles(): Boolean {
        for (pipe in knownFiles) {
            val qemuFile = File(pipe)
            if (qemuFile.exists()) {
                return true
            }
        }
        return false
    }

    private fun hasGenyFiles(): Boolean {
        for (file in knownGenyFiles) {
            val genyFile = File(file)
            if (genyFile.exists()) {
                return true
            }
        }
        return false
    }

    private fun hasQEmuDrivers(): Boolean {
        for (drivers_file in arrayOf(File("/proc/tty/drivers"), File("/proc/cpuinfo"))) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                val data = ByteArray(1024)
                try {
                    val `is`: InputStream = FileInputStream(drivers_file)
                    `is`.read(data)
                    `is`.close()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                val driverData = String(data)
                for (known_qemu_driver in knownQemuDrivers) {
                    if (driverData.indexOf(known_qemu_driver) != -1) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun hasEmulatorBuild(): Boolean {
        val board = Build.BOARD
        val brand = Build.BRAND
        val device = Build.DEVICE
        val hardware = Build.HARDWARE
        val model = Build.MODEL
        val product = Build.PRODUCT
        return (board.compareTo("unknown") == 0 || brand.compareTo("generic") == 0 || device.compareTo("generic") == 0 || model.compareTo("sdk") == 0 || product.compareTo("sdk") == 0 || hardware.compareTo("goldfish") == 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun isQEmuEnvDetected(): Boolean {
        return hasEmulatorBuild() || hasPipes() || hasQEmuDrivers() || hasQEmuFiles() || hasGenyFiles()
    }
}