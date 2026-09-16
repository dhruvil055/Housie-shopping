package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.FAQItem
import com.housieshopping.app.domain.model.SupportTicket
import com.housieshopping.app.domain.model.TicketStatus
import com.housieshopping.app.domain.repository.SupportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepositoryImpl @Inject constructor() : SupportRepository {

    private val tickets = MockData.mockSupportTickets.toMutableList()

    override suspend fun getFAQs(): Result<List<FAQItem>> {
        return Result.success(MockData.mockFaqs)
    }

    override suspend fun getSupportTickets(): Result<List<SupportTicket>> {
        return Result.success(tickets)
    }

    override suspend fun createSupportTicket(
        subject: String,
        category: String,
        description: String
    ): Result<SupportTicket> {
        val newTicket = SupportTicket(
            id = "t_${System.currentTimeMillis()}",
            ticketNumber = "TK-${(1000..9999).random()}",
            subject = subject,
            category = category,
            description = description,
            status = TicketStatus.OPEN,
            createdAt = "Just now",
            updatedAt = "Just now"
        )
        tickets.add(0, newTicket)
        return Result.success(newTicket)
    }
}
