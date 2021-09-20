package org.quranacademy.quran.ui.widgets.translationview

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by ${Deven} on 6/1/18.
 */
class ReadMoreOption private constructor(builder: Builder) {

    // required
    private val context: Context

    // optional
    private val textLength: Int
    private val textLengthType: Int
    private val moreLabel: String
    private val lessLabel: String
    private val moreLabelColor: Int
    private val lessLabelColor: Int
    private val labelUnderLine: Boolean
    private val expandAnimation: Boolean
    private val showLessLabel: Boolean

    init {
        this.context = builder.context
        this.textLength = builder.textLength
        this.textLengthType = builder.textLengthType
        this.moreLabel = builder.moreLabel
        this.lessLabel = builder.lessLabel
        this.moreLabelColor = builder.moreLabelColor
        this.lessLabelColor = builder.lessLabelColor
        this.labelUnderLine = builder.labelUnderLine
        this.expandAnimation = builder.expandAnimation
        this.showLessLabel = builder.showLessLabel
    }

    fun addReadMoreTo(textView: TextView, text: CharSequence) {
        if (textLengthType == TYPE_CHARACTER) {
            if (text.length <= textLength) {
                textView.text = text
                return
            }
        } else {
            // If TYPE_LINE
            textView.setLines(textLength)
            textView.text = text
        }

        textView.post(Runnable {
            var textLengthNew = textLength
            if (textLengthType == TYPE_LINE) {
                textView.layout?.let { layout ->
                    val linesCount = layout.lineCount
                    if (linesCount <= textLength) {
                        textView.text = text
                        textView.setLines(linesCount)
                        return@Runnable
                    }

                    val lp = textView.layoutParams as ViewGroup.MarginLayoutParams

                    val subString = text.toString().substring(textView.layout.getLineStart(0),
                            textView.layout.getLineEnd(textLength - 1))
                    textLengthNew = subString.length - (moreLabel.length + 4 + lp.rightMargin / 6)
                }
            }

            val spannableStringBuilder = SpannableStringBuilder(text.subSequence(0, textLengthNew))
                    .append("... ")
                    .append(moreLabel)

            val ss = SpannableString.valueOf(spannableStringBuilder)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    if (showLessLabel) {
                        addReadLess(textView, text)
                    } else {
                        textView.maxLines = Integer.MAX_VALUE
                        textView.text = text
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = labelUnderLine
                    ds.color = moreLabelColor
                }
            }
            ss.setSpan(clickableSpan, ss.length - moreLabel.length, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && expandAnimation) {
                val layoutTransition = LayoutTransition()
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                (textView.parent as ViewGroup).layoutTransition = layoutTransition
            }

            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
        })
    }

    private fun addReadLess(textView: TextView, text: CharSequence) {
        textView.maxLines = Integer.MAX_VALUE
        val spannableStringBuilder = SpannableStringBuilder(text)
                .append(" ")
                .append(lessLabel)

        val ss = SpannableString.valueOf(spannableStringBuilder)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Handler().post { addReadMoreTo(textView, text) }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = labelUnderLine
                ds.color = lessLabelColor
            }
        }
        ss.setSpan(clickableSpan, ss.length - lessLabel.length, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = ss
    }

    class Builder(val context: Context) {
        // optional
        var textLength = 100
        var textLengthType = TYPE_CHARACTER
        var moreLabel = "read more"
        var lessLabel = "read less"
        var moreLabelColor = Color.parseColor("#ff00ff")
        var lessLabelColor = Color.parseColor("#ff00ff")
        var labelUnderLine = false
        var expandAnimation = false
        var showLessLabel = false

        /**
         * @param length         can be no. of line OR no. of characters - default is 100 character
         * @param textLengthType ReadMoreOption.TYPE_LINE for no. of line OR
         * ReadMoreOption.TYPE_CHARACTER for no. of character
         * - default is ReadMoreOption.TYPE_CHARACTER
         * @return Builder obj
         */
        fun textLength(length: Int, textLengthType: Int): Builder {
            this.textLength = length
            this.textLengthType = textLengthType
            return this
        }

        fun moreLabel(moreLabel: String): Builder {
            this.moreLabel = moreLabel
            return this
        }

        fun lessLabel(lessLabel: String): Builder {
            this.lessLabel = lessLabel
            return this
        }

        fun moreLabelColor(moreLabelColor: Int): Builder {
            this.moreLabelColor = moreLabelColor
            return this
        }

        fun lessLabelColor(lessLabelColor: Int): Builder {
            this.lessLabelColor = lessLabelColor
            return this
        }

        fun labelUnderLine(labelUnderLine: Boolean): Builder {
            this.labelUnderLine = labelUnderLine
            return this
        }

        fun showLessLabel(showLessLabel: Boolean): Builder {
            this.showLessLabel = showLessLabel
            return this
        }

        /**
         * @param expandAnimation either true to enable animation on expand or false to disable animation
         * - default is false
         * @return Builder obj
         */
        fun expandAnimation(expandAnimation: Boolean): Builder {
            this.expandAnimation = expandAnimation
            return this
        }

        fun build(): ReadMoreOption {
            return ReadMoreOption(this)
        }

    }

    companion object {

        private val TAG = ReadMoreOption::class.java.simpleName
        val TYPE_LINE = 1
        val TYPE_CHARACTER = 2
    }

}