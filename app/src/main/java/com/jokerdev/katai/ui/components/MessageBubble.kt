package com.jokerdev.katai.ui.components

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jokerdev.katai.data.model.ChatMessage
import kotlinx.coroutines.launch

@Composable
fun MessageBubble(
    message: ChatMessage
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val scale = remember { Animatable(0.92f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(message.id) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 280,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 220
                )
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value,
                alpha = alpha.value
            ),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = screenWidth * 0.85f)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (message.isUser) 20.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 20.dp
                ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = if (message.isUser) 0.dp else 1.dp,
                border = if (message.isUser) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                }
            ) {
                val userModifier = if (message.isUser) {
                    Modifier.background(MaterialTheme.colorScheme.primary)
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                }

                Box(
                    modifier = userModifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = com.jokerdev.katai.utils.MarkdownParser.parse(message.text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (message.isUser) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = FontWeight.Normal,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.05f
                    )
                }
            }

            if (!message.isUser && message.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable {
                            clipboardManager.setText(AnnotatedString(message.text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy Response",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Copy response",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}