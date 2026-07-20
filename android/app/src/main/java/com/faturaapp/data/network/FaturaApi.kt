package com.faturaapp.data.network

import com.faturaapp.data.model.ComparacaoMensal
import com.faturaapp.data.model.Fatura
import com.faturaapp.data.model.ResumoMensal
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FaturaApi {

    @GET("faturas")
    suspend fun listarFaturas(): List<Fatura>

    @GET("faturas/{id}")
    suspend fun obterFatura(@Path("id") id: Int): Fatura

    @Multipart
    @POST("faturas/upload")
    suspend fun uploadFatura(@Part arquivo: MultipartBody.Part): Fatura

    @GET("resumo/{ano}/{mes}")
    suspend fun resumoMensal(@Path("ano") ano: Int, @Path("mes") mes: Int): ResumoMensal

    @GET("comparacao")
    suspend fun compararMeses(@Query("periodos") periodos: String): ComparacaoMensal
}
