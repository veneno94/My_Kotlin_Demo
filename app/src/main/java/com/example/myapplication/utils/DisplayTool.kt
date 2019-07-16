package com.example.myapplication.utils

import android.content.Context

class DisplayTool(var con: Context) {
    private val wDip: Int // 屏幕宽度的dip
    private val hDip: Int // 屏幕长度的dip
    private val wScreen: Int // 获取屏幕的px
    private val hScreen: Int // 获取屏幕的px

    fun getwDip(): Int {
        return wDip
    }

    fun gethDip(): Int {
        return hDip
    }

    fun getwScreen(): Int {
        return wScreen
    }

    fun gethScreen(): Int {
        return hScreen
    }

    init {
        val dm = con.resources.displayMetrics
        wScreen = dm.widthPixels // 屏幕宽度的px
        hScreen = dm.heightPixels // 屏幕宽度的px
        wDip = px2dip(wScreen.toDouble()) // 屏幕宽度的dip
        hDip = px2dip(hScreen.toDouble()) // 屏幕长度的dip
    }

    fun dip2px(dipValue: Double): Int {

        val scale = con.resources.displayMetrics.density

        return (dipValue * scale + 0.5f).toInt()
    }

    fun px2dip(pxValue: Double): Int {

        val scale = con.resources.displayMetrics.density

        return (pxValue / scale + 0.5f).toInt()

    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale = con.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun sp2px(spValue: Float): Int {
        val fontScale = con.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

}