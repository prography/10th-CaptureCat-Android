package com.prography.home.ui.home.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.theme.*
import androidx.core.net.toUri

@Composable
fun ErrorReportBanner(
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infoText = stringResource(com.prography.ui.R.string.error_report_info)
    val emailAddress = "capturecat77@gmail.com"

    val containsEmail = infoText.contains(emailAddress)

    // Tag for the clickable email part
    val emailTag = "EMAIL_TAG"

    val annotatedString = buildAnnotatedString {
        if (containsEmail) {
            val beforeEmail = infoText.substringBefore(emailAddress)
            append(beforeEmail)

            // Add a tag to the email part for click detection
            pushStringAnnotation(tag = emailTag, annotation = emailAddress)
            withStyle(
                style = SpanStyle(
                    color = Primary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(emailAddress)
            }
            // End the clickable section
            pop()

            val afterEmail = infoText.substringAfter(emailAddress, "")
            append(afterEmail)
        } else {
            append(infoText)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = Color(0x24FF6600)
            )
            .clickable { onReportClick() }
            .padding(start = 24.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = stringResource(com.prography.ui.R.string.error_report_title),
                        style = subhead01Bold,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    if (containsEmail) {
                        ClickableText(
                            text = annotatedString,
                            style = caption02Regular.copy(color = Text02),
                            onClick = { offset ->
                                annotatedString.getStringAnnotations(emailTag, offset, offset)
                                    .firstOrNull()?.let { annotation ->
                                        openEmailApp(context, annotation.item)
                                    }
                            }
                        )
                    } else {
                        Text(
                            text = infoText,
                            style = caption02Regular,
                            color = Text02
                        )
                    }
                }
            }

            Image(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_report),
                contentDescription = stringResource(com.prography.ui.R.string.common_image),
                modifier = Modifier.size(76.dp, 70.dp)
            )
        }
    }
}

private fun openEmailApp(context: Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, "[CaptureCat] Error Report")
        }
        context.startActivity(Intent.createChooser(intent, null))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context,
            context.getString(com.prography.ui.R.string.email_app_not_found),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorReportBannerPreview() {
    PrographyTheme {
        ErrorReportBanner(
            onReportClick = {},
        )
    }
}