package com.jokerdev.katai.ui.screens

import android.annotation.SuppressLint
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jokerdev.katai.ui.components.AttachedPdfSection
import com.jokerdev.katai.ui.components.MessageBubble
import com.jokerdev.katai.viewmodel.ChatViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current


    val pdfPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {

                var pdfName = "Unknown PDF"

                context.contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )?.use { cursor ->

                    val nameIndex =
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    if (cursor.moveToFirst() && nameIndex != -1) {
                        pdfName =
                            cursor.getString(nameIndex)
                    }
                }
                viewModel.onPdfSelected(
                    pdfName = pdfName,
                    pdfUri = uri,
                    context = context
                )
            }
        }

    val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            AttachedPdfSection(
                pdfs =
                    uiState.selectedPdfName?.let {
                        listOf(it)
                    } ?: emptyList(),
                onPdfClick = {

                },
                onAddPdfClick = {
                    pdfPickerLauncher.launch("application/pdf")
                }
            )

            if (uiState.extractedPdfText.isNotEmpty()) {

                Text(
                    text = uiState.extractedPdfText.take(5000),
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                items(uiState.messages) { message ->
                    MessageBubble(message = message)
                }
            }

            if (uiState.isLoading) {

                Text(
                    text = "Katai is thinking...",
                    modifier = Modifier.padding(8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = uiState.currentMessage,
                    onValueChange = viewModel::onMessageChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(text = "Ask something...")
                    },
                    shape = MaterialTheme.shapes.extraLarge
                )

                FloatingActionButton(
                    onClick = {
                        viewModel.sendMessage()
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Floating Button"
                    )
                }
            }
        }

}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    ChatScreen()
}