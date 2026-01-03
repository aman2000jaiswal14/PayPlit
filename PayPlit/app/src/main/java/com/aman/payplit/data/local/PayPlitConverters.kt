package com.aman.payplit.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PayPlitConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String = gson.toJson(value ?: emptyList<String>())

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromDoubleList(value: List<Double>?): String = gson.toJson(value ?: emptyList<Double>())

    @TypeConverter
    fun toDoubleList(value: String): List<Double> {
        val type = object : TypeToken<List<Double>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String = gson.toJson(value ?: emptyMap<String, String>())

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, type) ?: emptyMap()
    }

    @TypeConverter
    fun fromNestedMap(value: Map<String, Map<String, Double>>?): String = gson.toJson(value ?: emptyMap<String, Map<String, Double>>())

    @TypeConverter
    fun toNestedMap(value: String): Map<String, Map<String, Double>> {
        val type = object : TypeToken<Map<String, Map<String, Double>>>() {}.type
        return gson.fromJson(value, type) ?: emptyMap()
    }

}