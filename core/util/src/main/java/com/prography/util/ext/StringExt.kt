package com.prography.util.ext

import com.prography.ui.BaseViewModel
import org.json.JSONObject


fun String?.parseErrorMsg(event: BaseViewModel? = null): String {
    return if(this == "") {
        ""
    } else if(this == "NetworkError") {
        "네트워크에 연결되어 있지 않거나 원활하지 않습니다."
    } else if(this == "unKnownError") {
        "예상 못한 에러 발생!"
    } else {
        val jsonObject = JSONObject(this)

        if(jsonObject.getString("message").equals("만료된 토큰입니다")) {
            event?.baseEvent(BaseViewModel.Event.ExpiredToken)
            ""
        } else {
            val msg = jsonObject.getString("message")
            if(msg.substring(msg.length - 1, msg.length) == ".") {
                msg
            } else {
                "$msg."
            }
        }
    }
}
