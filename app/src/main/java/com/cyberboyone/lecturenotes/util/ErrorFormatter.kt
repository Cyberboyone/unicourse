package com.cyberboyone.lecturenotes.util

/**
 * Converts technical exceptions / messages (NullPointer, SocketTimeout, decryption errors, etc.)
 * into clear, helpful, student-friendly messages.
 */
object ErrorFormatter {

    fun format(rawMessage: String?, fallback: String = "Unable to complete request. Please try again."): String {
        if (rawMessage.isNullOrBlank() || rawMessage.equals("null", ignoreCase = true)) {
            return "Unable to connect or load document. Please check your internet connection and try again."
        }

        val lower = rawMessage.lowercase()

        return when {
            lower.contains("decrypt") || lower.contains("badtag") || lower.contains("aead") || lower.contains("keystore") ->
                "This downloaded note needs a quick refresh. Tap 'Re-download' to update its security key."

            lower.contains("timeout") || lower.contains("timed out") ->
                "The connection timed out while loading the document. Please check your data or Wi-Fi and retry."

            lower.contains("unable to resolve host") || lower.contains("no address associated") || lower.contains("network") || lower.contains("connect") ->
                "No active internet connection found. Connect to Wi-Fi or mobile data to continue."

            lower.contains("404") || lower.contains("not found") ->
                "This lecture material is currently being updated on the server. Please check back shortly."

            lower.contains("enospc") || lower.contains("no space left") ->
                "Your device storage is full. Please free up some storage space to download notes."

            lower.contains("missing or incomplete") ->
                "The note file was partially downloaded. Please tap 'Re-download' to get a fresh copy."

            else -> {
                // If it looks like a raw Java/Kotlin exception stack trace or internal null message
                if (lower.startsWith("java.") || lower.startsWith("kotlin.") || lower.contains("exception:")) {
                    "An unexpected error occurred while processing the document. Please retry or re-download."
                } else {
                    rawMessage.trim()
                }
            }
        }
    }

    fun format(throwable: Throwable, fallback: String = "Something went wrong"): String {
        return format(throwable.message ?: throwable.localizedMessage, fallback)
    }
}
