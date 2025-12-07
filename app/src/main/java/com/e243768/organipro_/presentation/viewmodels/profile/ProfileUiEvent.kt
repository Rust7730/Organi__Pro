package com.e243768.organipro_.presentation.viewmodels.profile

sealed class ProfileUiEvent {
    object SettingsClicked : ProfileUiEvent()
    object AvatarClicked : ProfileUiEvent()
    object RefreshProfile : ProfileUiEvent()
}