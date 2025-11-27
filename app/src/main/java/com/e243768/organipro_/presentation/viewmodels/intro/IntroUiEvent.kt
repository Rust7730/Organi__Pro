package com.e243768.organipro_.presentation.viewmodels.intro

sealed class IntroUiEvent {
    object NextPage : IntroUiEvent()
    object PreviousPage : IntroUiEvent()
    object SkipIntro : IntroUiEvent()
    object FinishIntro : IntroUiEvent()
}