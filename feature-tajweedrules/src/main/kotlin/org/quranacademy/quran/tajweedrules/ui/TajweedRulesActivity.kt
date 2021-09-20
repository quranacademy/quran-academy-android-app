package org.quranacademy.quran.tajweedrules.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_tajweed_rules.*
import kotlinx.android.synthetic.main.stopping_sign_item.view.*
import kotlinx.android.synthetic.main.tajweed_rule_item.view.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.di.get
import org.quranacademy.quran.presentation.extensions.fromHtml
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.tajweedrules.R
import org.quranacademy.quran.tajweedrules.mvp.StoppingSign
import org.quranacademy.quran.tajweedrules.mvp.TajweedRule
import org.quranacademy.quran.tajweedrules.mvp.TajweedRulesPresenter
import org.quranacademy.quran.tajweedrules.mvp.TajweedRulesView

class TajweedRulesActivity : BaseThemedActivity(), TajweedRulesView {

    override val layoutRes = R.layout.activity_tajweed_rules

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val currentAudioUpdates = BroadcastChannel<TajweedRule?>(1)

    @InjectPresenter
    lateinit var presenter: TajweedRulesPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<TajweedRulesPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.setTitle(R.string.tajweed_rules)
    }

    override fun showStoppingSigns(stoppingSigns: List<StoppingSign>) {
        stoppingSigns.forEach { stoppingSign ->
            val stoppingSignView = stoppingSignsContainer.inflate(R.layout.stopping_sign_item)
            stoppingSignView.stoppingSignSymbol.text = stoppingSign.sign
            stoppingSignView.stoppingSignDescription.text = stoppingSign.description
            stoppingSignsContainer.addView(stoppingSignView)
        }
    }

    override fun showTajweedRules(tajweedRules: List<TajweedRule>) {
        tajweedRules.forEach { tajweedRule ->
            val tajweedRuleView = tajweedRulesContainer.inflate(R.layout.tajweed_rule_item)
            tajweedRuleView.tajweedRuleHeader.setBackgroundColor(Color.parseColor(tajweedRule.color))
            tajweedRuleView.tajweedRuleName.text = tajweedRule.name
            tajweedRuleView.tajweedRuleDescription.text = tajweedRule.description.fromHtml()
            tajweedRuleView.tajweedSampleLabel.spanned = tajweedRule.arabicTextSample.fromHtml()
            tajweedRulesContainer.addView(tajweedRuleView)

            var isPlaying = false
            tajweedRuleView.playPauseTajweedSampleButton.setOnClickListener {
                if (!isPlaying) {
                    presenter.onPlayTajweedRuleSampleClicked(tajweedRule)
                } else {
                    presenter.onStopAudioSamplePlayingClicked()
                }
            }

            launch {
                currentAudioUpdates.asFlow().collect { currentPlayingAudio ->
                    isPlaying = tajweedRule.name == currentPlayingAudio?.name
                    tajweedRuleView.playPauseTajweedSampleButton.change(!isPlaying)
                }
            }
        }
    }

    override fun setCurrentPlayingAudio(tajweedRule: TajweedRule?) {
        launch { currentAudioUpdates.send(tajweedRule) }
    }

}