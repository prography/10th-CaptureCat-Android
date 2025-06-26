package com.prography.onboarding.screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.onboarding.screen.contract.OnboardingAction
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.Gray08
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.body01Regular
import com.prography.ui.theme.headline01Bold

@Composable
fun OnboardingContent(
    onAction: (OnboardingAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(8.dp),
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
            Spacer(modifier = Modifier.height(79.dp))
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                painter = painterResource(id = com.prography.ui.R.drawable.ic_onboarding_logo),
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            UiPrimaryButton(
                text = stringResource(id = com.prography.ui.R.string.onboarding_login),
                onClick = { onAction(OnboardingAction.LoginClicked) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = com.prography.ui.R.string.onboarding_next),
                color = Gray08,
                modifier = Modifier.padding(14.dp, 20.dp).clickable {
                    onAction(OnboardingAction.SkipClicked)
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginContentPreview() {
    OnboardingContent(
        onAction = {} // 빈 람다 전달
    )
}
