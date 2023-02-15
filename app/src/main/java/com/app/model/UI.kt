package com.app.model

import com.bin.david.form.annotation.SmartColumn
import com.bin.david.form.annotation.SmartTable

@SmartTable(name="网络请求统计表")
class RequestCount{
    @SmartColumn(id =1,name = "新闻类别")
    var newsType2:String = ""
    @SmartColumn(id =2,name = "参数")
    var newsType1:String = ""
    @SmartColumn(id =3,name = "今日请求次数")
    var count:Int = 0
}