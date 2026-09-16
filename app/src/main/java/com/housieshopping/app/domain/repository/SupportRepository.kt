package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.FAQItem
import com.housieshopping.app.domain.model.SupportTicket

interface SupportRepository {
    suspend fun getFAQs(): Result<List<FAQItem>>
    suspend fun getSupportTickets(): Result<List<SupportTicket>>
    suspend fun createSupportTicket(subject: String, category: String, description: String): Result<SupportTicket>
}
