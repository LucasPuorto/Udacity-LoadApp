package com.udacity.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.udacity.R
import kotlin.properties.Delegates

sealed class ButtonState(val buttonText: Int) {
    object Clicked : ButtonState(R.string.button_name)
    object Loading : ButtonState(R.string.button_loading)
    object Completed : ButtonState(R.string.button_completed)
    object Error : ButtonState(R.string.button_error)
}

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_ANIMATION_DURATION = 1000L
        private const val ERROR_ANIMATION_REPEAT_COUNTS = 1
    }

    private val attributesTypedArray: TypedArray

    private lateinit var paint: Paint

    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator.ofFloat(0F, 1f)

    private var text = ""
    private var buttonTextColor: Int = 0
    private var buttonBackground: Int = 0

    private var loadLevel = 0f

    private val pointPosition: PointF = PointF(0.0f, 0.0f)
    private val textRect = Rect()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Clicked) { _, _, new ->

        when (new) {
            ButtonState.Clicked -> {
                valueAnimator.cancel()
            }
            ButtonState.Loading -> {
                valueAnimator.repeatMode = ValueAnimator.RESTART
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.duration = DEFAULT_ANIMATION_DURATION
                valueAnimator.disableViewDuringAnimation()
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
            }
            ButtonState.Error -> {
                valueAnimator.repeatMode = ValueAnimator.RESTART
                valueAnimator.repeatCount = ERROR_ANIMATION_REPEAT_COUNTS
                valueAnimator.duration = DEFAULT_ANIMATION_DURATION
                valueAnimator.disableViewDuringAnimation()
                valueAnimator.start()
            }
        }
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    init {
        isClickable = true
        attributesTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0).apply {
            buttonTextColor = getInt(R.styleable.LoadingButton_android_textColor, 0)
            buttonBackground = getInt(R.styleable.LoadingButton_android_backgroundTint, 0)

            recycle()
        }
        valueAnimator.addUpdateListener {
            loadLevel = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        text = context.getString(buttonState.buttonText)

        drawTextBackground(canvas)

        when (buttonState) {
            is ButtonState.Clicked -> {
                drawTextDownload(canvas)
            }
            is ButtonState.Loading -> {
                drawLoading(canvas)
                drawTextDownload(canvas)
            }
            is ButtonState.Completed -> {
                drawTextDownload(canvas)
            }
            is ButtonState.Error -> {
                drawRectError(canvas)
                drawTextDownload(canvas)
            }
        }
    }

    private fun drawLoading(canvas: Canvas?) {
        drawRectLoading(canvas)
        drawArcLoading(canvas)
    }

    private fun drawRectLoading(canvas: Canvas?) {
        paint.color = getColor(context, R.color.colorPrimaryDark)
        canvas?.drawRect(
            0f,
            0f,
            loadLevel * widthSize.toFloat(),
            heightSize.toFloat(),
            paint
        )
    }

    private fun drawArcLoading(canvas: Canvas?) {
        paint.color = getColor(context, R.color.colorAccent)

        val rectF = RectF()
        val circleDiameter = 60.0f
        val circleSize = heightSize - paddingBottom - circleDiameter
        rectF.set(circleDiameter, circleDiameter, circleSize, circleSize)

        canvas?.drawArc(
            rectF,
            0F,
            loadLevel * 360F,
            true,
            paint
        )
    }

    private fun drawRectError(canvas: Canvas?) {
        paint.color = getColor(context, R.color.error)
        canvas?.drawRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h

        setMeasuredDimension(w, h)
    }

    private fun drawTextDownload(canvas: Canvas?) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = context.resources.getDimension(R.dimen.default_text_size)
            color = buttonTextColor
        }
        paint.getTextBounds(text, 0, text.length, textRect)
        pointPosition.computeXYForText(textRect)
        canvas?.drawText(text, pointPosition.x, pointPosition.y, paint)
    }

    private fun drawTextBackground(canvas: Canvas?) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = buttonBackground
        }
        canvas?.drawColor(paint.color)
    }

    private fun PointF.computeXYForText(textRect: Rect) {
        x = widthSize.toFloat() / 2
        y = heightSize.toFloat() / 2 - textRect.centerY()
    }

    private fun ValueAnimator.disableViewDuringAnimation() {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                isEnabled = true
            }
        })
    }
}