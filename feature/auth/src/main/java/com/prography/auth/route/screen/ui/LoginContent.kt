package com.prography.auth.route.screen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prography.auth.route.screen.contract.LoginAction
import com.prography.auth.route.screen.contract.LoginState

@Composable
fun LoginContent(state: LoginState, onAction: (LoginAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Kakao Login Button
        Button(
            onClick = { onAction(LoginAction.ClickKakao) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text(text = "카카오로 로그인", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        // Google Login Button
        Button(
            onClick = { onAction(LoginAction.ClickGoogle) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text(text = "Google로 로그인", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip Login Button/Link (Navigate to Home)
        Text(
            text = "나중에 하기",
            color = Color.Gray,
            modifier = Modifier.clickable {
                onAction(LoginAction.ClickSkip)
            }
        )
    }
}