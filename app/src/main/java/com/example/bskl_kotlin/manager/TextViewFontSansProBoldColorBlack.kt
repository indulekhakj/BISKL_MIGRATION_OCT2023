package com.example.bskl_kotlin.manager

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.example.bskl_kotlin.R

class TextViewFontSansProBoldColorBlack :androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : super(context) {
        val type = Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Bold.otf")
        this.typeface = type
        this.setTextColor(context.resources.getColor(R.color.black))
    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val type = Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Bold.otf")
        this.typeface = type
        this.setTextColor(context.resources.getColor(R.color.black))
    }
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        val type = Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Bold.otf")
        this.typeface = type
        this.setTextColor(context.resources.getColor(R.color.black))
    }
}