package org.quranacademy.quran.presentation.textsettingspanel

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_translation_settings.*
import kotlinx.android.synthetic.main.fragment_translation_settings.view.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.TranslationFont
import org.quranacademy.quran.extensions.normalizeHarfsInText
import org.quranacademy.quran.presentation.extensions.fromHtml
import org.quranacademy.quran.presentation.textsettingspanel.mvp.TranslationTextSettingsPresenter
import org.quranacademy.quran.presentation.textsettingspanel.mvp.TranslationTextSettingsView
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import org.quranacademy.quran.presentation.ui.global.getTypeface
import org.quranacademy.quran.presentation.ui.textsettingspanel.R

class TextSettingsFragment : BaseFragment(), TranslationTextSettingsView {

    companion object {
        fun newInstance(): TextSettingsFragment {
            return TextSettingsFragment()
        }
    }

    override val layoutRes = R.layout.fragment_translation_settings

    @InjectPresenter
    lateinit var presenter: TranslationTextSettingsPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<TranslationTextSettingsPresenter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arabicTextSizeSeekBar.onValueChangedListener = { textSize ->
            presenter.setArabicTextSize(textSize)
            arabicTextSample.textSize = textSize.toFloat()
        }

        translationTextSizeSeekBar.onValueChangedListener = { textSize ->
            presenter.setTranslationTextSize(textSize)
            translationTextSample.textSize = textSize.toFloat()
        }

        wbwTranslationTextSizeSeekBar.onValueChangedListener = { textSize ->
            presenter.setWbwTranslationTextSize(textSize)
            wbwTranslationTextSample.textSize = textSize.toFloat()
        }

        arabicFontValue.setOnClickListener {
            presenter.onSelectArabicFontClicked()
        }

        translationFontValue.setOnClickListener {
            presenter.onSelectTranslationFontClicked()
        }

        centerQuranTextSwitch.setOnCheckedChangeListener { _, isChecked ->
            presenter.setQuranTextCenteringEnabled(isChecked)
            updateQuranTextGravity(isChecked)
        }

        centerTranslationTextSwitch.setOnCheckedChangeListener { _, isChecked ->
            presenter.setTranslationTextCenteringEnabled(isChecked)
            updateTranslationTextGravity(isChecked)
        }

        enableTajweedHighlightingSwitch.setOnCheckedChangeListener { _, isChecked ->
            presenter.setTajweedHighlightingEnabled(isChecked)
            textTajweedHighlighting(isChecked)
        }
    }

    override fun initScreen(
            arabicTextSize: Int,
            translationTextSize: Int,
            wbwTranslationTextSize: Int,
            quranTextCenteringEnabled: Boolean,
            translationTextCenteringEnabled: Boolean,
            isTajweedEnabled: Boolean,
            arabicFont: ArabicFont,
            translationFont: TranslationFont
    ) {
        arabicTextSizeSeekBar.setValue(arabicTextSize)
        arabicTextSample.textSize = arabicTextSize.toFloat()

        translationTextSizeSeekBar.setValue(translationTextSize)
        translationTextSample.textSize = translationTextSize.toFloat()

        wbwTranslationTextSizeSeekBar.setValue(wbwTranslationTextSize)
        wbwTranslationTextSample.textSize = wbwTranslationTextSize.toFloat()

        arabicFontValue.text = arabicFont.title
        translationFontValue.text = translationFont.title

        arabicTextSample.typeface = arabicFont.getTypeface()
        translationTextSample.typeface = translationFont.getTypeface(context!!)
        wbwTranslationTextSample.typeface = translationFont.getTypeface(context!!)

        centerQuranTextSwitch.isChecked = quranTextCenteringEnabled
        updateQuranTextGravity(quranTextCenteringEnabled)
        centerTranslationTextSwitch.isChecked = translationTextCenteringEnabled
        updateTranslationTextGravity(translationTextCenteringEnabled)

        enableTajweedHighlightingSwitch.isChecked = isTajweedEnabled
    }

    override fun showArabicFontSelectionDialog(fonts: List<ArabicFont>) {
        val fonTitles = fonts.map { it.title }
        MaterialDialog(requireContext()).show {
            listItems(items = fonTitles) { _, index, _ ->
                val font = fonts[index]
                presenter.onArabicFontSelected(font)
                view!!.arabicTextSample.typeface = font.getTypeface()
                view!!.arabicFontValue.text = font.title
            }
        }
    }

    override fun showTranslationFontSelectionDialog(fonts: List<TranslationFont>) {
        val fonTitles = fonts.map { it.title }
        MaterialDialog(requireContext()).show {
            listItems(items = fonTitles) { _, index, _ ->
                val font = fonts[index]
                presenter.onTranslationFontSelected(font)
                view!!.translationTextSample.typeface = font.getTypeface(context)
                view!!.translationFontValue.text = font.title
            }
        }
    }

    private fun updateQuranTextGravity(isEnabled: Boolean) {
        arabicTextSample.gravity = if (isEnabled) Gravity.CENTER else Gravity.START
    }

    private fun updateTranslationTextGravity(isEnabled: Boolean) {
        translationTextSample.gravity = if (isEnabled) Gravity.CENTER else Gravity.START
    }

    private fun textTajweedHighlighting(isEnabled: Boolean) {
        if (isEnabled) {
            val basmalaWithTajweed = "بِسْمِ <font color=\"#AAAAAA\">ٱ</font>للَّهِ <font color=\"#AAAAAA\">ٱ</font>" +
                    "<font color=\"#AAAAAA\">ل</font>رَّحْمَ<font color=\"#537FFF\"></font>نِ <font color=\"#AAAAAA\">ٱ</font>" +
                    "<font color=\"#AAAAAA\">ل</font>رَّح<font color=\"#4050FF\">ِي</font>مِ"
            arabicTextSample.text = basmalaWithTajweed.normalizeHarfsInText().fromHtml()
        } else {

            arabicTextSample.text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَنِ ٱلرَّحِيمِ".normalizeHarfsInText()
        }
    }

    private fun ArabicFont.getTypeface(): Typeface {
        val fontPathResId = requireContext().resources.getIdentifier(this.resCodeName, "string", context!!.packageName)
        val fullFontPath = requireContext().getString(fontPathResId)
        return Typeface.createFromAsset(requireContext().assets, fullFontPath)
    }

}