package com.app.ui.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.R
import com.app.util.showToast

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // "取消"的点击事件：销毁本活动，返回上一级
        val searchCancelButton = findViewById<TextView>(R.id.search_cancel_button)
        searchCancelButton.setOnClickListener { finish() }
        // 搜索框
        val searchEditText = findViewById<EditText>(R.id.home_edit_text)
        searchEditText.setOnEditorActionListener { _, keyCode, _ ->
            // 如果点击了回车键，即搜索键，就弹出一个toast
            if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                "你输入了${searchEditText.text}".showToast()
                true
            } else {
                false
            }
        }


    }
}