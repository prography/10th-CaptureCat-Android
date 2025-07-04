package com.prography.onboarding.screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.onboarding.screen.contract.OnboardingAction
import com.prography.onboarding.screen.contract.OnboardingState
import com.prography.onboarding.screen.viewmodel.OnboardingViewModel
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.*



@Composable
fun OnboardingContent(
    onAction: (OnboardingAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 33.dp, start = 16.dp, end = 16.dp, bottom = 26.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(8.dp)
                    .clickable {
                        onAction(OnboardingAction.SkipClicked)
                    },
                painter = painterResource(id = com.prography.ui.R.drawable.ic_close_black),
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = com.prography.ui.R.string.onboarding_title),
                style = headline01Bold,
                color = Text01
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = com.prography.ui.R.string.onboarding_info),
                style = body01Regular.copy(textAlign = TextAlign.Center),
                color = Text02
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center) // 수직·수평 중앙 정렬
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_onboarding_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UiPrimaryButton(
                text = stringResource(id = com.prography.ui.R.string.onboarding_login),
                onClick = { onAction(OnboardingAction.LoginClicked) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = com.prography.ui.R.string.onboarding_next),
                color = Gray08,
                style = caption02Regular,
                modifier = Modifier
                    .padding(10.dp, 8.dp)
                    .clickable {
                        onAction(OnboardingAction.SkipClicked)
                    }
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun OnboardingContentPreview() {
    PrographyTheme {
        OnboardingContent(
            onAction = {}
        )
    }
}
