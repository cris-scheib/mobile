package com.google.codelabs.buildyourfirstmap

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CustomDeserializer : JsonDeserializer<Alerts> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Alerts {
        return if (json?.isJsonArray == true) {
            val array = json.asJsonArray
            Alerts(
                array.get(0).asInt,
                if (!array.get(1).isJsonNull()) array.get(1).asString else null,
                if (!array.get(2).isJsonNull()) array.get(2).asString else null,
                if (!array.get(3).isJsonNull()) array.get(3).asString else null,
                if (!array.get(4).isJsonNull()) array.get(4).asString else null,
                if (!array.get(5).isJsonNull()) array.get(5).asString else null,
                if (!array.get(6).isJsonNull()) array.get(6).asString else null,
                if (!array.get(7).isJsonNull()) array.get(7).asString else null,
                if (!array.get(8).isJsonNull()) array.get(8).asString else null)
        } else {
            Alerts(0, "", "", "", "", "", "False", "0", "0")
        }
    }
}