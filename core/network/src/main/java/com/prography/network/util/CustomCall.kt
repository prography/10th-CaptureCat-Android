package com.prography.network.util

import com.prography.network.entity.ApiResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class CustomCall<T : Any>(private val call: Call<T>) : Call<NetworkState<T>> {

    override fun enqueue(callback: Callback<NetworkState<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                val code = response.code()
                val error = response.errorBody()?.string()

                if (response.isSuccessful) {
                    if (body != null) {
                        // ApiResponse인 경우 result 필드로 성공/실패 판단
                        if (body is ApiResponse<*>) {
                            when (body.result) {
                                "SUCCESS" -> {
                                    callback.onResponse(
                                        this@CustomCall,
                                        Response.success(NetworkState.Success(body))
                                    )
                                }

                                "ERROR" -> {
                                    val errorCode = body.error?.code ?: "UNKNOWN_ERROR"
                                    val errorMessage = body.error?.message ?: "알 수 없는 오류가 발생했습니다."
                                    callback.onResponse(
                                        this@CustomCall,
                                        Response.success(
                                            NetworkState.Failure(
                                                code,
                                                "$errorCode: $errorMessage"
                                            )
                                        )
                                    )
                                }

                                else -> {
                                    callback.onResponse(
                                        this@CustomCall,
                                        Response.success(
                                            NetworkState.UnknownError(
                                                IllegalStateException("알 수 없는 result: ${body.result}"),
                                                "알 수 없는 result"
                                            )
                                        )
                                    )
                                }
                            }
                        } else {
                            // 일반 응답인 경우 기존 로직 사용
                            callback.onResponse(
                                this@CustomCall, Response.success(NetworkState.Success(body))
                            )
                        }
                    } else {
                        callback.onResponse(
                            this@CustomCall,
                            Response.success(
                                NetworkState.UnknownError(
                                    IllegalStateException("body값이 null로 넘어옴"),
                                    "body값이 null로 넘어옴"
                                )
                            )
                        )
                    }
                } else {
                    callback.onResponse(
                        this@CustomCall,
                        Response.success(NetworkState.Failure(code, error))
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Timber.e("t ${t.message}")
                val errorResponse = when (t) {
                    is IOException -> NetworkState.NetworkError(t)
                    else -> NetworkState.UnknownError(t, "onFailure에 진입,IoException 이외의 에러")
                }
                callback.onResponse(this@CustomCall, Response.success(errorResponse))
            }
        })
    }

    override fun clone(): Call<NetworkState<T>> = CustomCall(call.clone())

    override fun execute(): Response<NetworkState<T>> {
        throw UnsupportedOperationException("커스텀한 callAdapter에서는 execute를 사용하지 않습니다 ")
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}