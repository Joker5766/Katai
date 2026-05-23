package com.jokerdev.katai.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jokerdev.katai.data.model.ChatSession
import java.io.File

object ChatStorage {
    private const val FILE_NAME = "chats_backup.json"

    fun saveChats(context: Context, chats: List<ChatSession>) {
        try {
            val gson = Gson()
            val jsonString = gson.toJson(chats)
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadChats(context: Context): List<ChatSession> {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) return emptyList()
            val jsonString = file.readText()
            val gson = Gson()
            val type = object : TypeToken<List<ChatSession>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
