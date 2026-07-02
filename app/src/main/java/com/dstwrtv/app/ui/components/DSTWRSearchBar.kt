package com.dstwrtv.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DSTWRSearchBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    
    TextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("ابحث عن القنوات، المسلسلات، أو الباقات...", color = DSTWRTheme.TextMuted, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(54.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = 1.dp,
                brush = if (isFocused) Brush.linearGradient(listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber)) 
                        else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DSTWRTheme.SurfaceDark.copy(alpha = 0.5f),
            unfocusedContainerColor = DSTWRTheme.SurfaceDark.copy(alpha = 0.25f),
            disabledContainerColor = DSTWRTheme.SurfaceDark.copy(alpha = 0.1f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = DSTWRTheme.TextMain,
            unfocusedTextColor = DSTWRTheme.TextMain,
            cursorColor = DSTWRTheme.PrimaryRed
        ),
        shape = RoundedCornerShape(20.dp),
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = "بحث", tint = if (isFocused) DSTWRTheme.PrimaryRed else DSTWRTheme.TextMuted, modifier = Modifier.size(20.dp))
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { 
                    onSearchChange("") 
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Rounded.Close, contentDescription = "مسح", tint = DSTWRTheme.TextMuted, modifier = Modifier.size(18.dp))
                }
            }
        }
    )
}
