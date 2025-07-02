package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.R
import com.prography.ui.theme.*

@Composable
fun UiSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "태그 이름으로 검색해 보세요",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                color = Gray02,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "검색",
            tint = Gray05,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Text01,
                    fontFamily = FontFamily(Font(R.font.prography_pretendard_medium))
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = body02Regular,
                    color = Gray05
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UiSearchBarPreview() {
    var searchText by remember { mutableStateOf("") }

    PrographyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UiSearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = "태그 이름으로 검색해 보세요"
            )

            UiSearchBar(
                value = "검색어 입력됨",
                onValueChange = {},
                placeholder = "태그 이름으로 검색해 보세요"
            )
        }
    }
}