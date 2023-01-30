package com.umc.approval.data.repository.login

import com.umc.approval.data.dto.login.get.ReturnBasicLoginDto
import com.umc.approval.data.dto.login.get.ReturnEmailCheckDto
import com.umc.approval.data.dto.login.get.ReturnPhoneAuthDto
import com.umc.approval.data.dto.login.get.ReturnPhoneAuthRequestDto
import com.umc.approval.data.dto.login.post.BasicJoinDto
import com.umc.approval.data.dto.login.post.BasicLoginDto
import com.umc.approval.data.dto.login.post.PhoneAuthDto
import com.umc.approval.data.dto.login.post.SocialJoinDto
import com.umc.approval.data.retrofit.instance.RetrofitInstance.serverApi
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * Login Fragment Repository
 * */
class LoginFragmentRepository() {

    /**
     * Email Check API
     * */
    fun email_check(email: String): Call<ReturnEmailCheckDto> {
        return serverApi.email_check(email)
    }

    /**
     * Phone Auth Request API
     * */
    fun phone_auth_request(phoneNumber: String): Call<ReturnPhoneAuthRequestDto> {
        return serverApi.phone_auth_request(phoneNumber)
    }

    /**
     * Phone Check API
     * */
    fun phone_auth(auth: PhoneAuthDto): Call<ReturnPhoneAuthDto> {
        return serverApi.phone_auth(auth)
    }

    /**
     * Basic Join API
     * */
    fun basic_join(user : BasicJoinDto): Call<ResponseBody> {
        return serverApi.basic_join(user)
    }

    /**
     * Social Join API
     * */
    fun social_join(accessToken: String, user : SocialJoinDto): Call<ResponseBody> {
        return serverApi.social_join(accessToken, user)
    }

    /**
     * Basic Login API
     * */
    fun basic_login(email: String, password: String): Call<ReturnBasicLoginDto> {
        return serverApi.basic_login(email, password)
    }

    /**
     * Social Join API
     * */
    fun social_login(accessToken: String): Call<ResponseBody> {
        return serverApi.social_login(accessToken)
    }

    /**
     * Password Change API
     * */
    fun password_change(email: String, password: String): Call<ResponseBody> {
        return serverApi.password_change(email, password)
    }

    /**
     * Logout API
     * */
    fun logout(accessToken: String): Call<ResponseBody> {
        return serverApi.logout(accessToken)
    }

    /**
     * my spring server login
     * */
    fun login(accessToken: String, case: String): Call<ResponseBody> {
        return serverApi.login(accessToken,case)
    }

    /**
     * Connect to Server API
     * */
    fun connectServer(accessToken: String): Call<ResponseBody> {
        return serverApi.connect_server(accessToken)
    }
}