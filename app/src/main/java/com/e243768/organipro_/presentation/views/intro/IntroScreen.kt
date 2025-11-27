package com.e243768.organipro_.presentation.views.intro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.intro.IntroUiEvent
import com.e243768.organipro_.presentation.viewmodels.intro.IntroViewModel
import com.e243768.organipro_.presentation.views.intro.components.IntroButton
import com.e243768.organipro_.presentation.views.intro.components.IntroSlide
import com.e243768.organipro_.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun IntroScreen(
    navController: NavController,
    viewModel: IntroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.totalPages })
    val coroutineScope = rememberCoroutineScope()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is IntroViewModel.NavigationEvent.NavigateToLogin -> {
                navController.navigate(Routes.Login) {
                    popUpTo(Routes.Intro) { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            null -> { /* No hacer nada */ }
        }
    }

    // Sincronizar pager con el state
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            viewModel.onEvent(
                if (pagerState.currentPage > uiState.currentPage) {
                    IntroUiEvent.NextPage
                } else {
                    IntroUiEvent.PreviousPage
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientWithStarsBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pager con slides
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    when (page) {
                        0 -> IntroSlide(
                            title = "Organiza mejor con\nOrganiPro",
                            description = "Gestiona tus tareas diarias de manera eficiente y alcanza tus objetivos.",
                            illustration = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF8A5CFF),
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                        )
                        1 -> IntroSlide(
                            title = "Sistema de\nRecompensas",
                            description = "Gana puntos y sube de nivel completando tus tareas. ¡Compite con tus amigos!",
                            illustration = {
                                Icon(
                                    imageVector = Icons.Outlined.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC700),
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                        )
                        2 -> IntroSlide(
                            title = "Mantén tu Racha",
                            description = "Completa tareas todos los días para mantener tu racha activa y obtener bonificaciones.",
                            illustration = {
                                Icon(
                                    imageVector = Icons.Outlined.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                        )
                    }
                }

                // Indicador de páginas (dots)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(uiState.totalPages) { index ->
                        val color = if (pagerState.currentPage == index) {
                            Color(0xFF8A5CFF)
                        } else {
                            Color(0xFF8A5CFF).copy(alpha = 0.3f)
                        }

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp)
                                .then(
                                    if (pagerState.currentPage == index) {
                                        Modifier.width(24.dp)
                                    } else {
                                        Modifier
                                    }
                                )
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = color)
                            }
                        }
                    }
                }

                // Botones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 48.dp)
                ) {
                    IntroButton(
                        text = if (uiState.isLastPage) "Comenzar" else "Siguiente",
                        onClick = {
                            if (uiState.isLastPage) {
                                viewModel.onEvent(IntroUiEvent.FinishIntro)
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}