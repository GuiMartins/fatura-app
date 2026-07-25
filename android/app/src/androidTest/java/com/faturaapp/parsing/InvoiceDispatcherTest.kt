package com.faturaapp.parsing

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Validates the Kotlin parsing engine (PdfBox-Android) against the same real
 * invoices and the same numbers already validated by the original Python
 * parser.
 */
@RunWith(AndroidJUnit4::class)
class InvoiceDispatcherTest {

    private fun readAsset(name: String): ByteArray {
        val context = InstrumentationRegistry.getInstrumentation().context
        return context.assets.open(name).use { it.readBytes() }
    }

    @Test
    fun nubank_matchesThePythonParserBaseline() {
        val invoice = InvoiceDispatcher.processInvoice(readAsset("nubank.pdf"))
        assertEquals("nubank", invoice.bank)
        assertEquals(7, invoice.referenceMonth)
        assertEquals(2026, invoice.referenceYear)
        assertEquals(134, invoice.transactions.size)
        assertEquals(12619.10, invoice.transactions.sumOf { it.amount }, 0.01)
    }

    @Test
    fun itauMastercard_matchesThePythonParserBaseline() {
        val invoice = InvoiceDispatcher.processInvoice(readAsset("itau_mastercard.pdf"))
        assertEquals("itau", invoice.bank)
        assertEquals("5563", invoice.card)
        assertEquals(16, invoice.transactions.size)
        assertEquals(2019.28, invoice.transactions.sumOf { it.amount }, 0.01)
    }

    @Test
    fun itauVisa_matchesThePythonParserBaseline() {
        val invoice = InvoiceDispatcher.processInvoice(readAsset("itau_visa.pdf"))
        assertEquals("itau", invoice.bank)
        assertEquals("7434", invoice.card)
        assertEquals(1, invoice.transactions.size)
        assertEquals(490.73, invoice.transactions.sumOf { it.amount }, 0.01)
    }

    @Test
    fun mercadoPago_matchesThePythonParserBaseline() {
        val invoice = InvoiceDispatcher.processInvoice(readAsset("mercadopago.pdf"))
        assertEquals("mercadopago", invoice.bank)
        assertEquals("2177", invoice.card)
        assertEquals(41, invoice.transactions.size)
        assertEquals(3100.22, invoice.transactions.sumOf { it.amount }, 0.01)
    }

    @Test
    fun wrongPassword_throwsIncorrectPasswordException() {
        val bytes = readAsset("itau_mastercard_protegido.pdf")
        assertThrows(IncorrectPasswordException::class.java) {
            InvoiceDispatcher.processInvoice(bytes, listOf("0000-errada"))
        }
    }

    @Test
    fun correctPasswordAmongSeveralCandidates_opensAndExtractsCorrectly() {
        val bytes = readAsset("itau_mastercard_protegido.pdf")
        // Simulates the "default passwords" flow: tries a wrong one before the right one.
        val invoice = InvoiceDispatcher.processInvoice(bytes, listOf("0000-errada", "14501", "1450"))
        assertEquals("itau", invoice.bank)
        assertEquals("5563", invoice.card)
        assertEquals(16, invoice.transactions.size)
        assertEquals(2019.28, invoice.transactions.sumOf { it.amount }, 0.01)
    }
}
