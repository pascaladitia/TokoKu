package com.pascal.tokoku

import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.network.model.ResponseLogin
import com.pascal.tokoku.data.repository.NetworkRepository
import com.pascal.tokoku.presentation.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mockNetworkRepository: NetworkRepository

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockNetworkRepository = mockk()
        loginViewModel = LoginViewModel(mockNetworkRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testGetLoginSuccess() = runBlocking {
        val builder: MultipartBody.Builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        builder
            .addFormDataPart("username", "pitjarus")
            .addFormDataPart("password", "admin")
            .build()

        val login = ResponseLogin()
        val expectedResource = Resource.Success(login)
        coEvery { mockNetworkRepository.login(any()) } returns flowOf(expectedResource)

        loginViewModel.login(builder.build())

        val actualResource: Resource<ResponseLogin> = loginViewModel.loginResult.value
        assertEquals(expectedResource::class, actualResource::class)
        assertEquals(expectedResource.data, actualResource.data)
    }
}