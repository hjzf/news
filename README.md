# News App
```
git clone https://github.com/hjzf/news.git
```
## 项目描述:
一个Android开发基础学习阶段的新闻App实战项目

![](./screenshots/test.png)    
![](./screenshots/test_1.png)

## 开发思路:
### 设计的缺陷1: 只能显示一条新闻(即写在布局文件中的那一条特定的新闻)

解决方案: 用 "一个RecyclerView标签" 代替 "ScrollView-LinearLayout-include这个复杂的嵌套结构"

|效果               |之前的实现方法                |现在的实现方法                                                   |
|                  -|                            -|                                                              -|
|滚动               | ScrollView                  |   RecyclerView                                                |
|垂直方向线性排列    | LinearLayout                |   newsRecyclerView.layoutManager = LinearLayoutManager(this)  |
|展示新闻            | include                    |    newsRecyclerView.adapter = MyAdapter(newsList)             |



### 设计的缺陷2: 新闻数据被硬编码在kotlin代码中

解决方案: 调用 聚合数据 https://www.juhe.cn/ 免费提供的新闻API, 通过网络请求获取实时的新闻数据    

**步骤一：**  添加网络权限  
```xml
<!--在AndroidManifest.xml文件中添加下面这个标签-->
<uses-permission android:name="android.permission.INTERNET"></uses-permission>
```
**步骤二：**  添加http支持(高版本安卓默认仅支持https)  

```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```
```
<!--在AndroidManifest.xml文件中的Application标签添加下面这个属性-->
android:networkSecurityConfig="@xml/network_security_config"
```
**步骤三：**  使用Okhttp库和Gson库发送网络请求，并解析返回的JSON数据     

```
dependencies {
    .......
    implementation  'com.squareup.okhttp3:okhttp:4.9.0'
    implementation  'com.google.code.gson:gson:2.8.6'
    
    .......
}
```


## 遇到的问题:
###  **1.  Context类型参数怎么填？**  
方案一 ，MainActivity 本身就是一个Context，直接填  this 或者  this@MainActivity   
方案二 ， parent.context  (在Adapter中使用这个方案，parent是一个viewGroup)    
方案三 ， MyApplication.context  ，这是最终的解决方案               







### **2.  ViewPager是能够通过触摸左右切换页面的，如何禁用这个功能呢？**  
方案一 ，自定义一个 NoScrollViewPager         






### **3.  ViewPager在跨页切换时，会出现多页闪烁的问题**   
方案一 ，使用   ViewPager.setCurrentItem(0, false)   代替之前的  ViewPager.currentItem = 0     即添加一个参数 false        
```kotlin
 // 给底部导航栏的菜单项添加点击事件
bottomNav.setOnNavigationItemSelectedListener {
    when (it.itemId) {
        // smoothScroll=false这个参数能解决切换时的多页闪烁问题
        R.id.nav_home -> contentViewPager.setCurrentItem(0, false)
        R.id.nav_video -> contentViewPager.setCurrentItem(1, false)
        R.id.nav_user -> contentViewPager.setCurrentItem(2, false)
    }
    false
}
```

方案二，自定义一个 NewsViewPager ，这个方案用在了 TabLayout与新闻列表ViewPager的联动上 
```kotlin 
// 自定义一个viewPager : 给setCurrentItem方法设置一个默认参数false, 解决切换时的多页闪烁问题
class NewsViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun setCurrentItem(item: Int) {
        // smoothScroll=false 这个参数能解决切换时的多页闪烁问题
        super.setCurrentItem(item, false)
    }
}
```



### **4.  代码中还有哪些没来得及修改的缺陷?**     
这些地方代码要改一下：

 - 在HomeFragment中，要将  `viewPager.adapter = activity?.supportFragmentManager?.let { Adapter(it) } `  改成 ` viewPager.adapter = Adapter(childFragmentManager) ` 具体原因见 https://blog.csdn.net/allan_bst/article/details/64920076      
 - 在Fragment中，不能通过构造函数传递参数，原因请自行搜索。具体修改方法 https://www.jianshu.com/p/3600b5c2f668  ，kotlin版本的做法见 https://github.com/hjzf/news-mvvm/blob/master/app/src/main/java/com/hjzf/ui/news/NewsFragment.kt   

如果代码中还有特别严重的Bug或者设计不合理的地方，欢迎提供建议




###  **5.  如何去掉点击时的水波纹动画效果？**  
基本思路是将背景色设置为透明色。  
 - 去掉TabLayout的水波纹效果（其中transparent是定义的透明色）:        
 ```xml
app:tabRippleColor="@color/transparent"  
 ```
 - 去掉BottomNavigationView的水波纹效果  :                
```xml
app:itemRippleColor="@color/transparent" 
```
 - 去掉标题栏的menu的水波纹效果 :
```xml
<!--设置菜单项的水波纹颜色为透明 v21以上有效，见src/main/res/values-v21/themes.xml-->
<item name="android:selectableItemBackgroundBorderless">@color/transparent</item>
```






###  **6.  如何在RecyclerView中展示多种菜单项，比如有三张图片的新闻列表项？以及上拉加载更多的footer_view?**     
**步骤一:**  在适配器中重写 getItemViewType(position: Int) 函数，判断第position条新闻应该用哪一种列表项展示，返回viewType常量，              

**步骤二:**  在 onCreateViewHolder 中 根据不同的viewType加载不同的列表项布局并创建对应类型的ViewHolder         

**步骤三:**  最后在onBindViewHolder 中根据不同的ViewHolder类型进行数据绑定 。       

见 https://github.com/hjzf/news/blob/master/app/src/main/java/com/app/ui/adapter/NewsAdapter.kt




### **7.  TabLayout的菜单项太多显得很拥挤怎么办？**  
添加属性  app:tabMode="scrollable"  让标签栏能横向滑动      






### **8.  如何实现BiliBili安卓客户端的上滑自动隐藏标题栏效果？**  
使用 CoordinatorLayout + AppBarLayout + app:layout_scrollFlags +  app:layout_behavior 实现标题栏与新闻列表的联动效果    






### **9.  如何播放视频？**  
 android 原生的 VideoView 控件,我在第二个测试页面写了一个VideoView的demo，或者开源库 https://github.com/google/ExoPlayer.git      






###  **10.  如何使用数据库缓存数据?**  
 android可以使用SQLite数据库储存一些关系型的数据。  
这里尝试使用了郭神的开源库 LitePal 简化数据库的操作 ，见   https://github.com/guolindev/LitePal.git 详细的中文文档     https://blog.csdn.net/guolin_blog/category_9262963.html    
使用非常方便，个人感觉比Room的开发体验还要好，强烈推荐，（注意：部分API发生了变化例如DataSupport  改成了  LitePalSupport   
同时尝试了一下郭神的Glance项目 https://github.com/guolindev/Glance.git     






### **11.  这个新闻App还有什么可以实现的?**     
由于缺乏后端支持，很多功能都实现不了，只能写一下界面。但是可以利用SQLite数据库实现一些本地功能例如浏览历史记录，其它更复杂的功能可以尝试写一下后端代码。  
这个App用到的技术非常基础，可以尝试用一下新知识，例如用retrofit替换Okhttp，用其它方式实现底部导航栏页面切换功能，使用LiveData,...或者整个改成MVVM架构，见https://github.com/hjzf/news-mvvm.git           
下次重构打算尝试一下 Jetpack Compose ，继续努力吧       




## 典型BUG记录:

|BUG描述|原因|解决方案|
|   -   | - |   -   |
|Permission denied (missing INTERNET permission?)      | 没有网络权限，加载不了来自网络的图片 |    `<uses-permission android:name="android.permission.INTERNET"></uses-permission>`    |
|lateinit property context has not been initialized    | MyApplication没有注册              |   `android:name=".MyApplication"`   |
|Unable to start activity ComponentInfo{com.app/com.app.MainActivity}: android.os.NetworkOnMainThreadException   |  网络请求不能放在主线程中 | thread { ... }    |
| Only the original thread that created a view hierarchy can touch its views.       | 不能在子线程中刷新UI  | runOnUiThread { ... } |
| have you declared this activity in your AndroidManifest.xml?     | 新活动没有注册  |  `<activity android:name=".DetailActivity"></activity>`   |



## 已完成的内容  

- [x] 新闻类别标签栏
- [x] 底部导航栏 
- [x] home页面的标题栏布局
- [x] home页面中，点击搜索框打开搜索页面(**注意：在代码中实现了搜索页面的内容，但是忘记录制视频**) 
- [x] 用数据库做本地数据缓存,用到了 LitePal 和 Glance
- [x] 使用MVVM架构，MVVM版本的新闻App见  https://github.com/hjzf/news-mvvm.git 



## 未来还会做什么  
- [ ] 使用Jetpack Compose 重构





