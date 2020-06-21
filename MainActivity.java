package com.example.hglas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login()
    }

    fun login(){
        auto_login.setOnCheckedChangeListener { _, b ->
            if(b) {
                id_editText.setText("ssollock")
                pw_editText.setText("1q2w3e4r")
                Toast.makeText(applicationContext, "자동입력 설정", Toast.LENGTH_SHORT).show()
            }else{
                id_editText.setText("")
                pw_editText.setText("")
                Toast.makeText(applicationContext, "자동입력 해제", Toast.LENGTH_SHORT).show()
            }
        }

        id_editText.setOnClickListener{
            id_editText.setText("")
        }

        pw_editText.setOnClickListener{
            pw_editText.setText("")
        }

        login_bt.setOnClickListener{
            if(id_editText.text.toString().equals("ssollock") && pw_editText.text.toString().equals("1q2w3e4r")){
                val intent = Intent(this, LoginActivity::class.java);
                intent.putExtra("manager",false);
                startActivity(intent)
            }
            else if(id_editText.text.toString().equals("ssollock") && pw_editText.text.toString().equals("master")){
                val intent = Intent(this, LoginActivity::class.java);
                intent.putExtra("manager", true);
                startActivity(intent)
            }else if(id_editText.text.toString() == "")
                Toast.makeText(applicationContext, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
            else if(pw_editText.text.toString() == "")
                Toast.makeText(applicationContext, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            else {
                Toast.makeText(applicationContext, "유효하지 않은 계정입니다.\n다시 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            id_editText.setText("")
            pw_editText.setText("")
        }
    }
}
