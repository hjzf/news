package com.app.custom

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

// 自定义一个viewPager : 给setCurrentItem方法设置一个默认参数false, 解决切换时的多页闪烁问题
class NewsViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun setCurrentItem(item: Int) {
        // smoothScroll=false 这个参数能解决切换时的多页闪烁问题
        super.setCurrentItem(item, false)
    }
}