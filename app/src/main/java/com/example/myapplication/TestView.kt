package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View

class TestView: View{

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var mode = MeasureSpec.getMode(widthMeasureSpec)
        var size = MeasureSpec.getSize(widthMeasureSpec)
        when(mode){
            MeasureSpec.AT_MOST -> Log.i("fsfdfseee","atmost")
            MeasureSpec.EXACTLY -> Log.i("fsfdfseee","EXACTLY")
            MeasureSpec.UNSPECIFIED -> Log.i("fsfdfseee","UNSPECIFIED")
        }

        Log.i("fsfdfseee","size: "+size)

    }
}