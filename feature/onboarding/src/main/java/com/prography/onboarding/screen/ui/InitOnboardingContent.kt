package com.prography.onboarding.screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.onboarding.screen.contract.OnboardingAction
import com.prography.onboarding.screen.contract.OnboardingState
import com.prography.onboarding.screen.viewmodel.OnboardingViewModel
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.PrimaryPress
import com.prography.ui.theme.PrographyTheme
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body01Regular
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline01Bold


data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String,
    val buttonText: String
)

@Composable
fun InitOnboardingContent(
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    OnboardingPager(
        state = state,
        onAction = { action -> viewModel.sendAction(action) }
    )
}

@Composable
fun OnboardingPager(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            imageRes = com.prography.ui.R.drawable.img_onboarding_save,
            title = "스크린샷만 쏙\n골라서 저장해요",
            description = "",
            buttonText = "다음"
        ),
        OnboardingPage(
            imageRes = com.prography.ui.R.drawable.img_onboarding_organize,
            title = "여러 장을 한 번에\n태그로 정리해요",
            description = "",
            buttonText = "다음"
        ),
        OnboardingPage(
            imageRes = com.prography.ui.R.drawable.img_onboarding_start,
            title = "태그로 쉽게 찾고\n바로 활용해요",
            description = "",
            buttonText = "시작하기"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    LaunchedEffect(pagerState.currentPage) {
        onAction(OnboardingAction.PageChanged(pagerState.currentPage))
    }

    LaunchedEffect(state.currentPage) {
        if (state.currentPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(30.dp)) // Top Margin

            // HorizontalPager 위쪽 영역에 weight(1f)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dot
            DotIndicator(
                totalPages = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 14.dp)
            )

            // Button
            UiPrimaryButton(
                text = pages[pagerState.currentPage].buttonText,
                onClick = {
                    when (pagerState.currentPage) {
                        pages.size - 1 -> onAction(OnboardingAction.StartClicked)
                        else -> onAction(OnboardingAction.NextClicked)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 26.dp)
            )
        }

        // Skip (top right)
        Text(
            text = "건너뛰기",
            style = body02Regular,
            color = Text03,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 33.dp, end = 16.dp)
                .clickable { onAction(OnboardingAction.SkipClicked) }
        )
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        )

        // Title
        Text(
            text = page.title,
            style = headline01Bold,
            color = Text01,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

    }
}

@Composable
fun DotIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) PrimaryPress else Gray04
                    )
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun InitOnboardingContentPreview() {
    PrographyTheme {
        OnboardingPager(
            state = OnboardingState(currentPage = 0),
            onAction = {}
        )
    }
}