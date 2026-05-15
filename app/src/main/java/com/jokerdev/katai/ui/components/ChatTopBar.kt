package com.jokerdev.katai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    onBackClick: () -> Unit = {},
    onDeleteChat: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),

        title = {
            Text(
                text = "Katai",
                style = MaterialTheme.typography.titleLarge
            )
        },

        navigationIcon = {

            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },

        actions = {

            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Delete Chat")
                    },
                    onClick = {
                        expanded = false
                        onDeleteChat()
                    }
                )

                HorizontalDivider()

                DropdownMenuItem(
                    text = {
                        Text("Settings")
                    },
                    onClick = {
                        expanded = false
                        onSettingsClick()
                    }
                )
            }
        }
    )
}