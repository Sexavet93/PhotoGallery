package com.photogallery.data

import android.content.Context
import androidx.preference.PreferenceManager
import java.lang.RuntimeException

private const val LAST_QUERY_KEY = "searchQuery"
private const val LAST_RESULT_ID_KEY = "lastResultId"
private const val IS_NOTIFICATIONS_ENABLED_KEY = "isPolling"

class QueryPreferences private constructor(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setLastQuery(query:String){
        sharedPreferences.edit().putString(LAST_QUERY_KEY,query).apply()
    }

    fun getLastQuery(): String {
        return sharedPreferences.getString(LAST_QUERY_KEY, "") ?: ""
    }

    fun setLastPhotoId(id: String){
        sharedPreferences.edit().putString(LAST_RESULT_ID_KEY, id).apply()
    }

    fun getLastPhotoId(): String{
        return sharedPreferences.getString(LAST_RESULT_ID_KEY, "") ?: ""
    }

    fun setIsNotificationOn(isOn: Boolean){
        sharedPreferences.edit().putBoolean(IS_NOTIFICATIONS_ENABLED_KEY,isOn).apply()
    }

    fun getIsNotificationOn(): Boolean{
        return sharedPreferences.getBoolean(IS_NOTIFICATIONS_ENABLED_KEY,false)
    }

    companion object{
        private var INSTANCE: QueryPreferences? = null
        fun initInstance(context: Context){
            if(INSTANCE == null){
                INSTANCE = QueryPreferences((context))
            }
        }
        fun getInstance() = INSTANCE ?: throw RuntimeException("uninitialized property Exception: instance is null")
    }
}