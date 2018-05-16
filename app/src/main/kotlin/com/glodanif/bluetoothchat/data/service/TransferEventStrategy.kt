package com.glodanif.bluetoothchat.data.service

import com.glodanif.bluetoothchat.utils.isNumber

class TransferEventStrategy : DataTransferThread.EventsStrategy {

    private val generalMessageRegex = Regex("\\d+#\\d+#\\d+#*")
    private val fileStartRegex = Regex("6#\\d+#0#*")

    override fun isMessage(message: String?) = message != null && generalMessageRegex.containsMatchIn(message)

    override fun isFileStart(message: String?): DataTransferThread.FileInfo? =

            if (message != null && fileStartRegex.containsMatchIn(message)) {

                val info = fileStartRegex.replace(message, "")
                val uid = message.substring(2).substringBefore("#").toLong()

                if (info.isEmpty()) {
                    null
                } else {
                    val size = info.substringAfter("#").substringBefore("#")
                    if (size.isNumber()) {
                        DataTransferThread.FileInfo(
                                uid,
                                info.substringBefore("#"),
                                size.toLong()
                        )
                    } else {
                        null
                    }
                }
            } else {
                null
            }

    override fun isFileCanceled(message: String?): DataTransferThread.CancelInfo? =

            if (message != null && (message.contains("8#0#0#") || message.contains("8#0#1#"))) {
                val byPartner = message
                        .substringAfter("8#0#")
                        .replace("8#0#", "")
                        .substringBefore("#")
                DataTransferThread.CancelInfo(byPartner == "1")
            } else {
                null
            }

    override fun isFileFinish(message: String?) = message != null && message.contains("7#0#0#")
}
