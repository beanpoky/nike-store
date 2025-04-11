package com.example.nikestore.common

import android.util.Log
import com.example.nikestore.R
import org.json.JSONObject
import retrofit2.HttpException

object NikeExceptionMapper {
    fun map(throwable: Throwable): NikeException {
        if (throwable is HttpException) {
            try {
                val errorJsonObject = JSONObject(throwable.response()?.errorBody()!!.string())
                val errorMessage = errorJsonObject.getString("message")
                return NikeException(
                    type = if (throwable.code() == 401) NikeException.Type.AUTH else NikeException.Type.SIMPLE,
                    serverMessage = errorMessage
                )
            } catch (exception: Exception) {
                Log.e("TAG", "map: ${exception.message}")
            }
        }

        return NikeException(
            type = NikeException.Type.SIMPLE,
            userFriendlyMessage = R.string.unknown_error
        )
    }
}