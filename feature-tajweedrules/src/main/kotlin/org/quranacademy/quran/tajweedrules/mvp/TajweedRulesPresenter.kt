package org.quranacademy.quran.tajweedrules.mvp

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.player.data.simpleplayer.SimpleAudioPlayer
import org.quranacademy.quran.player.data.simpleplayer.SimpleAudioPlayerState
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.tajweedrules.R
import javax.inject.Inject

@InjectViewState
class TajweedRulesPresenter @Inject constructor(
        private val simpleAudioPlayer: SimpleAudioPlayer
) : BasePresenter<TajweedRulesView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            simpleAudioPlayer.stateChangeUpdates().collect { state ->
                if (state == SimpleAudioPlayerState.FINISHED || state == SimpleAudioPlayerState.IDLE) {
                    viewState.setCurrentPlayingAudio(null)
                }
            }
        }

        viewState.showStoppingSigns(getStopSigns())
        viewState.showTajweedRules(getTajweedRules())
    }

    fun onPlayTajweedRuleSampleClicked(tajweedRule: TajweedRule) {
        simpleAudioPlayer.stop()
        simpleAudioPlayer.playFromRaw(tajweedRule.audioFileResId)
        viewState.setCurrentPlayingAudio(tajweedRule)
    }

    fun onStopAudioSamplePlayingClicked() {
        simpleAudioPlayer.stop()
    }

    private fun getStopSigns(): List<StoppingSign> {
        return listOf(
                StoppingSign("6", getString(R.string.stopping_sign_must_stop)),
                StoppingSign("2", getString(R.string.stopping_sign_better_to_stop)),
                StoppingSign("7 7", getString(R.string.stopping_sign_pause_at_one)),
                StoppingSign("3", getString(R.string.stopping_sign_a_slight_pause)), //06E3
                StoppingSign("4", getString(R.string.stopping_sign_stop_or_continue)),
                StoppingSign("1", getString(R.string.stopping_sign_better_to_continue)),
                StoppingSign("5", getString(R.string.stopping_sign_dont_stop))
        )
    }

    private fun getTajweedRules(): List<TajweedRule> {
        return listOf(
                TajweedRule(
                        name = getString(R.string.taj_rule_ghunna),
                        description = getString(R.string.taj_rule_desc_ghunna),
                        arabicTextSample = getString(R.string.ghunna_sample),
                        color = "#FF7E1E",
                        audioFileResId = R.raw.ghunna
                ),
                TajweedRule(
                        name = getString(R.string.taj_rule_ikhfa),
                        description = getString(R.string.taj_rule_desc_ikhfa),
                        arabicTextSample = getString(R.string.ikhfa_sample),
                        color = "#D500B7",
                        audioFileResId = R.raw.ikhfa
                ),
                TajweedRule(
                        name = getString(R.string.taj_rule_idgham),
                        description = getString(R.string.taj_rule_desc_idgham),
                        arabicTextSample = getString(R.string.idgham_sample),
                        color = "#169200",
                        audioFileResId = R.raw.idgham
                ),
                TajweedRule(
                        name = getString(R.string.taj_rule_idgham_without_ghunna),
                        description = getString(R.string.taj_rule_desc_idgham_without_ghunna),
                        arabicTextSample = getString(R.string.idgham_without_ghunna_sample),
                        color = "#169200",
                        audioFileResId = R.raw.idgham_without_ghunna
                ),
                TajweedRule(
                        name = getString(R.string.taj_rule_iqlab),
                        description = getString(R.string.taj_rule_desc_iqlab),
                        arabicTextSample = getString(R.string.iqlab_sample),
                        color = "#26BFFD",
                        audioFileResId = R.raw.iqlab
                ),
                TajweedRule(
                        name = getString(R.string.taj_rule_qalqala),
                        description = getString(R.string.taj_rule_desc_qalqala),
                        arabicTextSample = getString(R.string.qalqala_sample),
                        color = "#DD0008",
                        audioFileResId = R.raw.qalqala
                )
        )
    }

    private fun getString(stringId: Int) = resourcesManager.getString(stringId)

}