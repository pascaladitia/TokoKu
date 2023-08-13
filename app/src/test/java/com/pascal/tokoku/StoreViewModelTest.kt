package com.pascal.tokoku

import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.local.entity.StoreEntity
import com.pascal.tokoku.data.repository.LocalRepository
import com.pascal.tokoku.presentation.main.viewModel.StoreViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class StoreViewModelTest {

    private lateinit var storeViewModel: StoreViewModel

    @Mock
    private lateinit var mockLocalRepository: LocalRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        storeViewModel = StoreViewModel(mockLocalRepository)

        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetStoreSuccess() = runBlockingTest {
        val storeEntity = StoreEntity(
            "1",
            "Id1234",
            "testChannel",
            "Test area",
            "Jl Sukabumi",
            "dcName",
            "123",
            "1",
            "1",
            "1",
            "1",
            "1",
            "test",
            "123",
            "",
            "",
            "1020",
            "123",
            "400",
            true,
        )
        val expectedResource = Resource.Success(listOf(storeEntity))
        Mockito.`when`(mockLocalRepository.getStore()).thenReturn(flowOf(expectedResource))

        storeViewModel.getStore()

        assertEquals(expectedResource::class, storeViewModel.getStoreResult.value::class)
        assertEquals(expectedResource.data, storeViewModel.getStoreResult.value.data)
    }
}