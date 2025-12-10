package com.e243768.organipro_.presentation.viewmodels.profile

import android.net.Uri

sealed class ProfileUiEvent {
    object SettingsClicked : ProfileUiEvent()
    object AvatarClicked : ProfileUiEvent()
    object RefreshProfile : ProfileUiEvent()
    data class PhotoSelected(val uri: Uri) : ProfileUiEvent()
}