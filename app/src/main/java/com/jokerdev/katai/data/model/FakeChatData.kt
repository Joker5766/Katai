package com.jokerdev.katai.data.model


val fakeMessages = listOf(
    ChatMessage(
        id = 1,
        text = "What is normalization?",
        isUser = true
    ),
    ChatMessage(
        id = 2,
        text = "Normalization is the process of organizing data to reduce redundancy.",
        isUser = false
    ),
    ChatMessage(
        id = 3,
        text = "Explain 1NF.",
        isUser = true
    ),
    ChatMessage(
        id = 4,
        text = "First Normal Form ensures atomic values in table columns.",
        isUser = false
    )
)