package com.example.econome

import com.example.econome.data.datasource.NoSqlDataSource
import com.example.econome.data.local.TransactionDao
import com.example.econome.data.local.toRoom
import com.example.econome.data.repository.DualRepositoryImpl
import com.example.econome.domain.model.Transaction
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class DualRepositoryTest {

    private lateinit var repository: DualRepositoryImpl
    private val dao: TransactionDao = mock()
    private val noSqlDataSource: NoSqlDataSource = mock()

    @Before
    fun setup() {
        repository = DualRepositoryImpl(dao, noSqlDataSource)
    }

    @Test
    fun `saveTransaction calls Room DAO when isSqlActive is true`() = runTest {
        val transaction = Transaction(concept = "Test SQL", amount = 10.0)

        repository.toggleDatabaseEngine(true)
        repository.saveTransaction(transaction)

        verify(dao).insertTransaction(any())
        verifyNoInteractions(noSqlDataSource)
    }

    @Test
    fun `getTransactions returns list from Room when isSqlActive is true`() = runTest {
        whenever(dao.getTransactionsList()).thenReturn(emptyList())

        repository.toggleDatabaseEngine(true)
        val result = repository.getTransactions()

        verify(dao).getTransactionsList()
        assert(result.isEmpty())
    }

    @Test
    fun `toRoom extension maps Transaction fields correctly`() {
        val domain = Transaction(concept = "Mercado", amount = 50.5, date = 12345L)
        
        val room = domain.toRoom()

        assert(room.concept == "Mercado")
        assert(room.amount == 50.5)
        assert(room.date == 12345L)
    }
}
