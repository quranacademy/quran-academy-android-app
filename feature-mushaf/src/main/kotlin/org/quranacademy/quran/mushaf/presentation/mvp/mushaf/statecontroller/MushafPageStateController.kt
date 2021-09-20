package org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.mushaf.presentation.mvp.mushaf.StateMachine
import kotlin.math.abs

class MushafPageStateController constructor(
        ayahsRangeFinder: suspend (first: AyahId, AyahId: AyahId) -> List<AyahId>,
        viewController: MushafControllerView
) {

    private val enviroment = ControllerEnvironment(ayahsRangeFinder, viewController)
    private val stateMachine by lazy {
        StateMachine.create<MushafState, MushafEvent, MushafSideEffect> {
            initialState(MushafState.AyahsUnselected)

            state<MushafState.AyahsUnselected> {
                on<MushafEvent.OnAyahClick> { event ->
                    if (!event.isLongClick) {
                        transitionTo(MushafState.AyahsUnselected, MushafSideEffect.HandleClick())
                    } else {
                        transitionTo(MushafState.AyahsSelected, MushafSideEffect.HighlightAyah())
                    }
                }
            }

            state<MushafState.AyahsSelected> {
                on<MushafEvent.OnAyahClick> { event ->
                    if (event.isLongClick && event.pageNumber == enviroment.ayahToolbarPageNumber) {
                        transitionTo(MushafState.AyahsSelected, MushafSideEffect.HighlightAyahsGroup())
                    } else {
                        transitionTo(MushafState.AyahsSelected, MushafSideEffect.HighlightOtherAyah())
                    }
                }

                on<MushafEvent.OnDoubleClick> {
                    transitionTo(MushafState.AyahsUnselected, MushafSideEffect.ClearAyahsSelection())
                }

                on<MushafEvent.OnBackPressed> {
                    transitionTo(MushafState.AyahsUnselected, MushafSideEffect.ClearAyahsSelection())
                }

                on<MushafEvent.OnStopSelectionMode> {
                    transitionTo(MushafState.AyahsUnselected, MushafSideEffect.ClearAyahsSelection())
                }

                on<MushafEvent.OnShowTranslationClicked> {
                    transitionTo(MushafState.AyahsSelected, MushafSideEffect.OnShowTranslationClicked())
                }

                on<MushafEvent.OnPageChanged> {
                    if (abs(enviroment.ayahToolbarPageNumber - it.pageNumber) > 1) {
                        transitionTo(MushafState.AyahsUnselected, MushafSideEffect.ClearAyahsSelection())
                    }
                    transitionTo(MushafState.AyahsSelected)
                }

                on<MushafEvent.OnTranslationPanelClosed> {
                    transitionTo(MushafState.AyahsUnselected, MushafSideEffect.ClearAyahsSelection())
                }
            }

            onTransition { ts ->
                val transition = ts as? StateMachine.Transition.Valid ?: return@onTransition
                (transition.sideEffect as MushafSideEffect.MushafAction<MushafEvent>?)?.doAction(enviroment, transition.event)
            }
        }
    }

    suspend fun onAyahClick(ayahId: AyahId, pageNumber: Int, isLong: Boolean) =
            stateMachine.transition(MushafEvent.OnAyahClick(ayahId, pageNumber, isLong))

    suspend fun onDoubleClick() =
            stateMachine.transition(MushafEvent.OnDoubleClick())

    suspend fun onBackPressed(): Boolean {
        val isOnBackPressedHandled = stateMachine.state !is MushafState.AyahsUnselected
        stateMachine.transition(MushafEvent.OnBackPressed())
        return isOnBackPressedHandled
    }

    suspend fun stopSelectionMode() =
            stateMachine.transition(MushafEvent.OnStopSelectionMode())

    suspend fun onShowTranslationClicked() =
            stateMachine.transition(MushafEvent.OnShowTranslationClicked())

    suspend fun onTranslationPanelClosed() =
            stateMachine.transition(MushafEvent.OnTranslationPanelClosed())

    suspend fun onPageChanged(pageNumber: Int) =
            stateMachine.transition(MushafEvent.OnPageChanged(pageNumber))

    fun getSelectedAyahs() = enviroment.selectedAyahs.toList()

    fun isTranslationsPanelOpened() = enviroment.isTranslationsShowing

}