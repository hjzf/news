package com.app.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.R
import com.app.util.showToast
import java.lang.StringBuilder

// 一个 VideoView 的 demo
class VideoFragment : Fragment() {

    private lateinit var videoView: VideoView
    private lateinit var loadBtn: Button
    private lateinit var playBtn: Button
    private lateinit var pauseBtn: Button
    private lateinit var replayBtn: Button
    private lateinit var fileTitle: EditText
    private lateinit var videoPathIsPlaying: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        videoView = view.findViewById(R.id.video_view)
        loadBtn = view.findViewById(R.id.video_load)
        playBtn = view.findViewById(R.id.video_play)
        pauseBtn = view.findViewById(R.id.video_pause)
        replayBtn = view.findViewById(R.id.video_replay)
        fileTitle = view.findViewById(R.id.file_title)
        videoPathIsPlaying = view.findViewById(R.id.video_path_is_playing)
        return view
    }

    private fun findVideoPathByTitle(title: String): String? {
        var videoPath: String? = null
        val contentResolver = context?.contentResolver
        contentResolver?.let {
            // 执行SQL:   select _data from Video文件表 where title='xxx'
            val tableName = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val cursor =
                it.query(tableName, arrayOf("_data"), "title=?", arrayOf(title), null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    // 如果找到了该文件
                    videoPath = cursor.getString(0)
                }
                cursor.close()
            }
        }
        return videoPath
    }

    private fun loadResource() {
        // 测试用的的的视频资源路径是 内部存储 /Pictures/movie.mp4   文件标题是movie 格式是mp4
        // val videoPath = Environment.getExternalStorageDirectory().path + 文件名

        // 上面的写法过时了，推荐 MediaStore Api, 改用下面数据库查询的方式找到视频文件
        val videoPath = findVideoPathByTitle(fileTitle.text.toString())
        if (videoPath != null) {
            videoView.setVideoPath(videoPath)
            // 将当前正在播放的视频路径显示在界面上
            val stringBuilder = StringBuilder("当前播放:")
            stringBuilder.append(videoPath)
            videoPathIsPlaying.text = stringBuilder.toString()
        } else {
            "/Pictures /Movies /Documents /Download... 这些公共媒体目录中都找不到标题为${fileTitle.text}的视频文件".showToast()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //  显示视频进度控制栏
        videoView.setMediaController(MediaController(activity))
        //  解决只有声音没有图像的问题 https://www.jianshu.com/p/b8c060ce50b0
        videoView.setZOrderOnTop(true)

        // 加载新视频资源
        loadBtn.setOnClickListener {
            // 如果把资源放在SD卡上，需要判断是否有 WRITE_EXTERNAL_STORAGE权限才能播放视频
            activity?.let {
                val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                val result = ContextCompat.checkSelfPermission(it, permission)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    val arr = arrayOf(permission)
                    ActivityCompat.requestPermissions(it, arr, 1)
                } else {
                    loadResource()
                    videoView.start()
                }
            }
        }
        // 播放
        playBtn.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()
            }
        }
        // 暂停
        pauseBtn.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }
        // 重新播放
        replayBtn.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.resume()
            }
        }
    }

    // 申请完权限之后
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadResource()
                videoView.start()
            } else {
                "拒绝权限将无法使用程序".showToast()
                activity?.finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放资源
        videoView.suspend()
    }

}