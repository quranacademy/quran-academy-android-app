package org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller

sealed class MushafSideEffect {

    abstract class MushafAction<in Event : MushafEvent> : MushafSideEffect() {
        abstract suspend fun doAction(environment: ControllerEnvironment, event: Event)
    }

    class HandleClick : MushafAction<MushafEvent.OnAyahClick>() {
        override suspend fun doAction(
                environment: ControllerEnvironment,
                event: MushafEvent.OnAyahClick
        ) {
            with(environment) {
                viewController.onPageClick()
            }
        }
    }

    open class HighlightAyah : MushafAction<MushafEvent.OnAyahClick>() {
        override suspend fun doAction(
                environment: ControllerEnvironment,
                event: MushafEvent.OnAyahClick
        ) {
            with(environment) {
                selectedAyahs.add(event.ayahId)
                viewController.clearAyahsHighlight(ayahToolbarPageNumber)
                viewController.highlightAyahs(selectedAyahs())
                ayahToolbarPageNumber = event.pageNumber
            }
        }
    }

    class HighlightOtherAyah : MushafAction<MushafEvent.OnAyahClick>() {
        override suspend fun doAction(
                environment: ControllerEnvironment,
                event: MushafEvent.OnAyahClick
        ) = with(environment) {
            selectedAyahs.clear()
            selectedAyahs.add(event.ayahId)
            viewController.clearAyahsHighlight(ayahToolbarPageNumber)
            viewController.highlightAyahs(selectedAyahs())
            ayahToolbarPageNumber = event.pageNumber

            if (isTranslationsShowing) {
                viewController.showAyahTranslation(selectedAyahs())
            }
        }
    }

    class HighlightAyahsGroup : MushafAction<MushafEvent.OnAyahClick>() {
        override suspend fun doAction(
                environment: ControllerEnvironment,
                event: MushafEvent.OnAyahClick
        ) = with(environment) {
            val firstAyah = selectedAyahs.first()
            val startEndAyahs = when (firstAyah.compareTo(event.ayahId)) {
                -1 -> Pair(firstAyah, event.ayahId)
                1 -> Pair(event.ayahId, firstAyah)
                else -> Pair(event.ayahId, event.ayahId)
            }
            getAyahs(startEndAyahs.first, startEndAyahs.second) {
                selectedAyahs.clear()
                selectedAyahs.addAll(it)
                viewController.highlightAyahs(selectedAyahs())
            }

            if (isTranslationsShowing) {
                viewController.showAyahTranslation(selectedAyahs())
            }
        }
    }

    class ClearAyahsSelection : MushafAction<MushafEvent>() {
        override suspend fun doAction(
                environment: ControllerEnvironment,
                event: MushafEvent
        ) = with(environment) {
            selectedAyahs.clear()
            viewController.clearAyahsHighlight(ayahToolbarPageNumber)

            if (isTranslationsShowing) {
                viewController.clearAyahsHighlight(ayahToolbarPageNumber)
                viewController.closeTranslationPanel()
                isTranslationsShowing = false
            }
        }
    }

    class OnShowTranslationClicked : MushafAction<MushafEvent.OnShowTranslationClicked>() {
        override suspend fun doAction(
                environment: ControllerEnvironment,
                event: MushafEvent.OnShowTranslationClicked
        ) {
            with(environment) {
                viewController.showAyahTranslation(selectedAyahs())
                isTranslationsShowing = true
            }
        }
    }

}