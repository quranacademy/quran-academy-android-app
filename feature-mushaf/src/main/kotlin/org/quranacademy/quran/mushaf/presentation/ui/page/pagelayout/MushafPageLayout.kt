package org.quranacademy.quran.mushaf.presentation.ui.page.pagelayout

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.mushaf.presentation.ui.page.highlightingimageview.HighlightingImageView
import org.quranacademy.quran.presentation.extensions.getScreenSize
import org.quranacademy.quran.presentation.extensions.isHorizontalMode
import java.util.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import org.quranacademy.quran.presentation.extensions.dp

class MushafPageLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    enum class BorderMode {
        HIDDEN, LIGHT, DARK, LINE
    }

    private var isNightMode = false
    private var isRightPage: Boolean = true
        set(value) {
            field = value
            updateGradients()
            updatePageBorders()
            invalidate()
        }

    private var isFullWidth: Boolean = false
    private var shouldHideLine: Boolean = false

    private var gradientForNumberOfPages: Int = -1
    private lateinit var leftGradient: PaintDrawable
    private lateinit var rightGradient: PaintDrawable
    private lateinit var leftPageBorder: BitmapDrawable
    private lateinit var rightPageBorder: BitmapDrawable
    private lateinit var leftPageBorderNight: BitmapDrawable
    private lateinit var rightPageBorderNight: BitmapDrawable

    private var lineColor: Int = Color.BLACK
    private var lineDrawable: ShapeDrawable = ShapeDrawable(RectShape())

    private var scrollView: ObservableScrollView? = null
    private lateinit var leftBorder: BorderMode
    private lateinit var rightBorder: BorderMode
    private val viewPaddingSmall: Int = 0
    private val viewPaddingLarge: Int = 0

    private val imageContainer = FrameLayout(context)
    val mushafPageImage = HighlightingImageView(context)
    private val pageNumberLabel = TextView(context)

    var onScrollListener: ((x: Int, y: Int, oldx: Int, oldy: Int) -> Unit)? = null

    init {
        lineDrawable.intrinsicWidth = 1
        lineDrawable.intrinsicHeight = 1

        addPageContent()
        initBorders()

        updateGradients()
        updatePageBorders()
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val view = getContentView()
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        if (!isFullWidth) {
            val leftLineWidth = if (leftBorder === BorderMode.LINE) 1 else leftPageBorder.intrinsicWidth
            val rightLineWidth = if (rightBorder === BorderMode.HIDDEN) 0 else rightPageBorder.intrinsicWidth
            val headerFooterHeight = 0
            width -= (leftLineWidth + rightLineWidth + viewPaddingSmall + viewPaddingLarge)
            height -= 2 * headerFooterHeight
        }
        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val view = getContentView()
        val width = measuredWidth
        val height = measuredHeight
        val leftLineWidth = if (leftBorder === BorderMode.LINE) 1 else leftPageBorder.intrinsicWidth
        val rightLineWidth = if (rightBorder === BorderMode.HIDDEN) 0 else rightPageBorder.intrinsicWidth
        val headerFooterHeight = 0
        view.layout(leftLineWidth, headerFooterHeight, width - rightLineWidth, height - headerFooterHeight)
        super.onLayout(changed, l, t, r, b)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        if (width > 0) {
            val height = height
            if (leftBorder !== BorderMode.LINE || !shouldHideLine) {
                val left = when {
                    leftBorder === BorderMode.LINE -> lineDrawable
                    leftBorder === BorderMode.LIGHT -> leftPageBorder
                    else -> leftPageBorderNight
                }
                left.setBounds(0, 0, left.intrinsicWidth, height)
                left.draw(canvas)
            }

            if (rightBorder !== BorderMode.HIDDEN) {
                val right = if (rightBorder === BorderMode.LIGHT) rightPageBorder else rightPageBorderNight
                right.setBounds(width - right.intrinsicWidth, 0, width, height)
                right.draw(canvas)
            }
        }
    }

    fun enableNightMode(nightModeTextBrightness: Float) {
        this.isNightMode = true
        val matrix = floatArrayOf(
                -1f, 0f, 0f, 0f, nightModeTextBrightness,
                0f, -1f, 0f, 0f, nightModeTextBrightness,
                0f, 0f, -1f, 0f, nightModeTextBrightness,
                0f, 0f, 0f, 1f, 0f
        )
        mushafPageImage.colorFilter = ColorMatrixColorFilter(matrix)
    }

    fun setPageInfo(pageInfo: QuranPage) {
        isRightPage = (pageInfo.pageNumber % 2 == 1)
        mushafPageImage.pageBounds = pageInfo.pageBounds
        pageNumberLabel.text = pageInfo.pageNumber.toString()

        val pageImageBitmap = BitmapFactory.decodeFile(pageInfo.imagePath, BitmapFactory.Options())
        mushafPageImage.setImageDrawable(BitmapDrawable(resources, pageImageBitmap))

        //pageInfo.pageBounds.surahHeaderBounds.forEach { headerBounds ->
        //    val header = View(context)
        //    header.setBackgroundColor(Color.RED)
        //    val headerLp = LayoutParams(headerBounds.width, headerBounds.height)
        //    headerLp.leftMargin = headerBounds.x
        //    headerLp.topMargin = headerBounds.y
        //    imageContainer.addView(header, headerLp)
        //}
    }

    fun updateAyahHighlighting(ayahHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>) {
        mushafPageImage.updateAyahHighlighting(ayahHighlights)
    }

    private fun addPageContent() {
        val isLandscape = context.isHorizontalMode()
        val shouldWrapWithScrollView = true
        val contentView = generatePageContent()

        if (isLandscape && shouldWrapWithScrollView) {
            scrollView = ObservableScrollView(context)
            scrollView?.let { scrollView ->
                scrollView.isFillViewport = true
                addView(scrollView, MATCH_PARENT, MATCH_PARENT)
                scrollView.addView(contentView, MATCH_PARENT, WRAP_CONTENT)
                scrollView.scrollListener = { _: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int ->
                    onScrollListener?.invoke(x, y, oldx, oldy)
                }
            }
        } else {
            addView(contentView, MATCH_PARENT, MATCH_PARENT)
        }
    }

    private fun initBorders() {
        // these bitmaps are 11x1, so fairly small to keep both day and night versions around
        leftPageBorder = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.border_left))
        leftPageBorderNight = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.night_left_border))
        rightPageBorder = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.border_right))
        rightPageBorderNight = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.night_right_border))
    }

    private fun generatePageContent(): View {
        val contentContainer = LinearLayout(context)
        contentContainer.orientation = LinearLayout.VERTICAL

        mushafPageImage.adjustViewBounds = true
        mushafPageImage.setPadding(0, 25.dp, 0, 0)
        val imageContainerLp = LinearLayout.LayoutParams(MATCH_PARENT, 0)
        imageContainerLp.weight = 1f
        contentContainer.addView(imageContainer, imageContainerLp)

        val mushafImageLp = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        imageContainer.addView(mushafPageImage, mushafImageLp)

        pageNumberLabel.gravity = Gravity.CENTER
        contentContainer.addView(pageNumberLabel, MATCH_PARENT, 25.dp)
        return contentContainer
    }

    private fun getContentView(): View = getChildAt(0)

    private fun updatePageBorders() {
        if (isNightMode) {
            lineColor = Color.WHITE
        }

        if (isRightPage) {
            rightBorder = if (isNightMode) BorderMode.DARK else BorderMode.LIGHT
            lineDrawable.paint.color = lineColor
            leftBorder = BorderMode.LINE
        } else {
            leftBorder = if (isNightMode) BorderMode.DARK else BorderMode.LIGHT
            rightBorder = BorderMode.HIDDEN
        }

    }

    private fun updateGradients() {
        val pagesVisible = 1
        if (!::rightGradient.isInitialized || gradientForNumberOfPages != pagesVisible) {
            var width = context.getScreenSize().x
            width /= pagesVisible
            leftGradient = getPaintDrawable(width, 0)
            rightGradient = getPaintDrawable(0, width)
            gradientForNumberOfPages = pagesVisible
        }
    }

    private fun getPaintDrawable(startX: Int, endX: Int): PaintDrawable {
        val drawable = PaintDrawable()
        drawable.shape = RectShape()
        drawable.shaderFactory = getShaderFactory(startX, endX)
        return drawable
    }

    private fun getShaderFactory(startX: Int, endX: Int): ShapeDrawable.ShaderFactory {
        return object : ShapeDrawable.ShaderFactory() {

            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(startX.toFloat(), 0f, endX.toFloat(), 0f,
                        intArrayOf(-0x23252b, -0x2020c, -0x1, -0x20411),
                        floatArrayOf(0f, 0.18f, 0.48f, 1f),
                        Shader.TileMode.REPEAT)
            }
        }
    }

}