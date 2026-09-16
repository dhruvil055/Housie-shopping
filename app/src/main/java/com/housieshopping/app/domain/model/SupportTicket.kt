package com.housieshopping.app.domain.model

enum class TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

data class SupportTicket(
    val id: String,
    val ticketNumber: String,
    val subject: String,
    val category: String,
    val description: String,
    val status: TicketStatus,
    val createdAt: String,
    val updatedAt: String
)

data class FAQItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: String
)
