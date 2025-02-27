package com.zmdev.protoplus.db.Converters;


import androidx.room.TypeConverter;

import com.zmdev.protoplus.db.Entities.Parameter;
import com.google.gson.Gson;

public class Param2JsonConverter {

    @TypeConverter
    public Parameter[] toParamArray(String json) {
        return new Gson().fromJson(json, Parameter[].class);
    }

    @TypeConverter
    public String toJSON(Parameter[] params) {
        return new Gson().toJson(params);
    }
}
