package com.app.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

// 原生的ViewPager可以通过触摸左右切换fragment

// 这是一个自定义的NoScrollViewPager, 用户不可以通过触摸左右切换fragment，只能通过点击底部导航栏切换fragment
class NoScrollViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    // 禁用两个关键的触摸事件即可
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}