package com.mcebotari.expandingbutton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.mcebotari.expandingbutton.databinding.ButtonMainBinding

private const val TAG = "ExpandingButton: "

open class ExpandingButton @JvmOverloads constructor(context: Context,val attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private lateinit var buttonBackgroundDrawable: Drawable

    private var initialCornerRadius : Float = 0f

    protected var animationDuration = 400L // default
    protected var binding: ButtonMainBinding = ButtonMainBinding.inflate(LayoutInflater.from(context), this, true)
    protected val successIcon : Drawable = ContextCompat.getDrawable(context, R.drawable.ic_success)!!
    protected val failIcon : Drawable = ContextCompat.getDrawable(context, R.drawable.ic_fail)!!
    protected val roundedCornersTarget : Float = context.resources.getDimension(R.dimen.rounded_corners_radius)
    protected var currentButtonState : ButtonState = ButtonState.IDLE

    private val collapseButtonProgress = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = animationDuration * 1.5.toLong()
        addUpdateListener {
            val layoutParams = binding.mainBackground.layoutParams
            val newWidth = maxOf(binding.progressBar.width, (measuredWidth * (1f - it.animatedValue as Float)).toInt())
            layoutParams.width = newWidth
            binding.mainBackground.layoutParams = layoutParams
        }
    }
    private val expandButtonProgress = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = animationDuration * 1.5.toLong()
        addUpdateListener {
            val layoutParams = binding.mainBackground.layoutParams
            val newWidth = maxOf(binding.progressBar.width, (measuredWidth * (it.animatedValue as Float)).toInt())
            layoutParams.width = newWidth
            binding.mainBackground.layoutParams = layoutParams
        }
        this.doOnEnd { currentButtonState = ButtonState.IDLE }
    }

    private fun roundCornersProgress(from : Float, to : Float) = ValueAnimator.ofFloat(from, to).apply {
        this.duration = animationDuration * 1.5.toLong()
        addUpdateListener {
            Log.d(TAG, "initial cornerRadius: $initialCornerRadius")
            Log.d(TAG, "target cornerRadius: $roundedCornersTarget")
            Log.d(TAG, "animated radius: ${this.animatedValue as Float}")
            (binding.mainBackground.background.mutate() as? GradientDrawable)?.cornerRadius = this.animatedValue as Float
        }
    }

    init {
        fetchCustomAttributes()
        setupCustomAttributes()
    }

    private fun fetchCustomAttributes() {
        val customAttributes = context.obtainStyledAttributes(attrs, R.styleable.ExpandingButton)
        buttonBackgroundDrawable = customAttributes.getDrawable(R.styleable.ExpandingButton_background_drawable) ?: ContextCompat.getDrawable(context, R.drawable.default_background)!!
        initialCornerRadius = (buttonBackgroundDrawable as? GradientDrawable)?.cornerRadius ?: 0f
        customAttributes.recycle()
    }

    private fun setupCustomAttributes() {
        binding.mainBackground.background = buttonBackgroundDrawable
    }

    fun startLoadingAnimation() {
        if (currentButtonState == ButtonState.IDLE) {
            currentButtonState = ButtonState.LOADING
            hideText()
            showProgressBar()
            roundCornersProgress(initialCornerRadius, roundedCornersTarget).start()
            collapseButtonProgress.start()
        }
    }

    fun finishLoading(success : Boolean = true) {
        val resultIcon = if (success) successIcon else failIcon
        if (currentButtonState == ButtonState.LOADING) {
            hideProgressBar()
            showResultIcon(resultIcon)
        }
    }

    fun resetButton() {
        if (currentButtonState == ButtonState.FINISHED) {
            currentButtonState = ButtonState.IDLE
            hideResultIcon()
            showButtonText()
            roundCornersProgress(roundedCornersTarget, initialCornerRadius).start()
            expandButtonProgress.start()
        }
    }

    private fun showResultIcon(iconDrawable: Drawable) {
        binding.resultIcon.setImageDrawable(iconDrawable)
        binding.resultIcon
            .animate()
            .alpha(1f)
            .setDuration(animationDuration)
            .withEndAction {
                currentButtonState = ButtonState.FINISHED
            }
            .start()
    }

    private fun hideProgressBar() {
        binding.progressBar
            .animate()
            .alpha(0f)
            .setDuration(animationDuration / 2)
            .start()
    }

    private fun showButtonText() {
        binding.mainButtonText.translationY = binding.root.measuredHeight.toFloat()
        binding.mainButtonText
            .animate()
            .yBy(binding.root.measuredHeight * -1f)
            .alpha(1f)
            .setDuration(animationDuration)
            .start()
    }

    private fun hideResultIcon() {
        binding.resultIcon
            .animate()
            .alpha(0f)
            .setDuration(animationDuration)
            .start()
    }

    private fun showProgressBar() {
        binding.progressBar.translationY = binding.progressBar.height * -1f
        binding.progressBar
            .animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(animationDuration)
            .start()
    }

    private fun hideText() {
        binding.mainButtonText
            .animate()
            .yBy(binding.root.measuredHeight.toFloat())
            .alpha(0f)
            .setDuration(animationDuration)
            .withEndAction { binding.mainButtonText.translationY = binding.root.measuredHeight.toFloat() * -1f }
            .start()
    }
}

enum class ButtonState{
    IDLE,
    LOADING,
    FINISHED
}