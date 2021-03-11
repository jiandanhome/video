package com.tencent.qcloud.ugckit.custom

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tencent.qcloud.ugckit.R
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicAdapter
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicInfo
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicManager
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicManager.LoadMusicListener
import com.tencent.qcloud.ugckit.utils.BackgroundTasks
import com.tencent.qcloud.ugckit.utils.ToastUtil
import kotlinx.android.synthetic.main.dialog_ugc_select_music.*
import java.io.File
import java.util.*

class SelectMusicDialog: BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_ugc_select_music, container, false)
    }

    private var adapter:TCMusicAdapter?=null

    private val musicList= mutableListOf<TCMusicInfo>()

    var selectMusicCallback:((Intent)->Unit)?=null
    var dismissCallback:(()->Unit)?=null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setBgTransparent()
        setDialogStyle()
        intiAdapter()
        setListeners()
        TCMusicManager.getInstance().loadCustomMusicList()
    }

    private fun intiAdapter(){
        adapter= TCMusicAdapter(requireActivity(), musicList).also {
            rvMusic.adapter=it
        }
    }

    private fun setListeners() {
        ibClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
        adapter?.setOnClickSubItemListener { button, position ->
            val musicInfo: TCMusicInfo = musicList[position]
            if (musicInfo.status == TCMusicInfo.STATE_UNDOWNLOAD) {
                musicInfo.status = TCMusicInfo.STATE_DOWNLOADING
                adapter?.updateItem(position, musicInfo)
                downloadMusic(position)
            } else if (musicInfo.status == TCMusicInfo.STATE_DOWNLOADED) {
                backToEditActivity(position, musicInfo.localPath!!)
            }
        }
        val mLoadMusicListener = object : LoadMusicListener {
            override fun onBgmList(tcBgmInfoList: ArrayList<TCMusicInfo>?) {
                BackgroundTasks.getInstance().runOnUiThread {
                    musicList.clear()
                    if (tcBgmInfoList != null) {
                        musicList.addAll(tcBgmInfoList)
                    }
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onBgmDownloadSuccess(position: Int, filePath: String) {
                try {
                    Thread.sleep(200)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                BackgroundTasks.getInstance().runOnUiThread {
                    val info: TCMusicInfo = musicList.get(position)
                    info.status = TCMusicInfo.STATE_DOWNLOADED
                    info.localPath = filePath
                    adapter?.updateItem(position, info)
                }
            }

            override fun onDownloadFail(position: Int, errorMsg: String) {
                BackgroundTasks.getInstance().runOnUiThread {
                    val info: TCMusicInfo = musicList.get(position)
                    info.status = TCMusicInfo.STATE_UNDOWNLOAD
                    info.progress = 0
                    adapter?.updateItem(position, info)
                    ToastUtil.toastShortMessage(resources.getString(R.string.ugckit_bgm_select_activity_download_failed))
                }
            }

            override fun onDownloadProgress(position: Int, progress: Int) {
                BackgroundTasks.getInstance().runOnUiThread {
                    if (musicList != null && musicList.size > 0) {
                        val info: TCMusicInfo = musicList.get(position)
                        if (info != null) {
                            info.status = TCMusicInfo.STATE_DOWNLOADING
                            info.progress = progress
                            adapter?.updateItem(position, info)
                        }
                    }
                }
            }
        }
        TCMusicManager.getInstance().setOnLoadMusicListener(mLoadMusicListener)
    }

    private fun backToEditActivity(position: Int, path: String) {
        val intent = Intent()
        intent.putExtra(UGCKitConstants.MUSIC_POSITION, position)
        intent.putExtra(UGCKitConstants.MUSIC_PATH, path)
        intent.putExtra(UGCKitConstants.MUSIC_NAME, musicList.get(position).name)
        selectMusicCallback?.invoke(intent)
        dismissAllowingStateLoss()
    }

    private fun downloadMusic(position: Int) {
        val musicInfo: TCMusicInfo = musicList.get(position)
        if (TextUtils.isEmpty(musicInfo.localPath)) {
            downloadMusicInfo(position, musicInfo)
            musicInfo.status = TCMusicInfo.STATE_DOWNLOADING
            musicInfo.progress = 0
            adapter?.updateItem(position, musicInfo)
            return
        }
        val file = File(musicInfo.localPath)
        if (!file.isFile) {
            musicInfo.localPath = ""
            musicInfo.status = TCMusicInfo.STATE_DOWNLOADING
            musicInfo.progress = 0
            adapter?.updateItem(position, musicInfo)
            downloadMusicInfo(position, musicInfo)
            return
        }
    }

    private fun downloadMusicInfo(position: Int, TCMusicInfo: TCMusicInfo) {
        TCMusicManager.getInstance().downloadMusicInfo(TCMusicInfo.name, position, TCMusicInfo.url)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.invoke()
    }

    private fun setBgTransparent(){
        var container=view?.parent as View?
        container?.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setDialogStyle(){
        val lp=dialog?.window?.attributes
        lp?.windowAnimations=R.style.bottomSheet_animation
        lp?.dimAmount=0F
        dialog?.window?.attributes=lp
    }

}