package com.example.econome

import com.example.econome.domain.model.Transaction
import com.example.econome.domain.repository.TransactionRepository
import com.example.econome.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private lateinit var viewModel: TransactionViewModel
    private val repository: TransactionRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        whenever(repository.getTransactions()).doReturn(emptyList())
        viewModel = TransactionViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `save calls repository saveTransaction and reloads list`() = runTest {
        // Arrange
        viewModel.concept = "Gasto Test"
        viewModel.amount = "100.0"

        // Act
        viewModel.save()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(repository).saveTransaction(
            argThat { this.concept == "Gasto Test" && this.amount == 100.0 }
        )
        verify(repository, times(2)).getTransactions() // init + after save
    }

    @Test
    fun `onToggleEngine updates state and reloads list`() = runTest {
        // Act
        viewModel.onToggleEngine(false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assert(!viewModel.isSqlActive)
        verify(repository).toggleDatabaseEngine(false)
        verify(repository, times(2)).getTransactions() // init + onToggle
    }
}
