package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_find_password.*
import javax.security.auth.login.LoginException

class FindPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        findPasswordSubmit_button.setOnClickListener {
            // TODO : 이메일로 패스워드 보내기
            // TODO : 재전송으로 버튼 변경하기
            // TODO : 로그인 하기 버튼 보여주기
        }
    }
}
