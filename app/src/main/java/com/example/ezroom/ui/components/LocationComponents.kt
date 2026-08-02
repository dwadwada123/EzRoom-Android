package com.example.ezroom.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.ezroom.data.remote.LocationSuggestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> LocationDropdown(
    label: String,
    items: List<T>,
    selectedItemName: String,
    onItemSelected: (T) -> Unit,
    getItemName: (T) -> String,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        CustomTextField(
            value = if (isLoading) "Đang tải..." else selectedItemName,
            onValueChange = {},
            readOnly = true,
            label = label,
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled && !isLoading
        )
        // Overlay a transparent clickable box over the entire field to capture clicks reliably
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = enabled && !isLoading) { expanded = !expanded }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            if (items.isEmpty() && !isLoading) {
                DropdownMenuItem(
                    text = { Text("Không có dữ liệu", style = MaterialTheme.typography.bodyMedium) },
                    onClick = { expanded = false },
                    enabled = false
                )
            }
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(getItemName(item), style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddressSuggestionField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suggestions: List<LocationSuggestion>,
    onSuggestionSelected: (LocationSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(suggestions) {
        showDropdown = suggestions.isNotEmpty()
    }

    Box(modifier = modifier.fillMaxWidth()) {
        CustomTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                showDropdown = true
            },
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            enabled = enabled
        )

        DropdownMenu(
            expanded = showDropdown && isFocused && suggestions.isNotEmpty(),
            onDismissRequest = { showDropdown = false },
            properties = androidx.compose.ui.window.PopupProperties(focusable = false),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = suggestion.displayName, 
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        ) 
                    },
                    onClick = {
                        onSuggestionSelected(suggestion)
                        showDropdown = false
                    }
                )
            }
        }
    }
}
