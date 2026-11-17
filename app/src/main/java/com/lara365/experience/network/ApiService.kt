package com.lara365.experience.network


import com.lara365.experience.models.ApiResponse
import com.lara365.experience.models.Establishment
import com.lara365.experience.models.Event
import com.lara365.experience.models.Route
import com.lara365.experience.models.AuthResponse
import com.lara365.experience.models.EbookCompleteResponse
import com.lara365.experience.models.EbookResponse
import com.lara365.experience.models.GastronomiaResponse
import com.lara365.experience.models.ValidationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("events")
    fun getEvents(): Call<ApiResponse<List<Event>>>

    @GET("establishments")
    fun getEstablishments(
        @Query("modulo") modulo: String? = null,
        @Query("es_punto_interes") esPunto: Boolean? = null
    ): Call<ApiResponse<List<Establishment>>>

    @GET("routes")
    suspend fun getRoutes(): ApiResponse<List<Route>>

    @GET("ebooks")
    suspend fun obtenerEbooks(
        @Header("Authorization") token: String
    ): Response<EbookResponse>

    @POST("ebooks/{id}/complete")
    suspend fun completarModuloEbook(
        @Header("Authorization") token: String,
        @Path("id") moduloId: Int
    ): Response<EbookCompleteResponse>

    @GET("gastronomia")
    suspend fun getGastronomia(): List<GastronomiaResponse>

    @GET("gastronomia/tipo/{tipo}")
    suspend fun getGastronomiaPorTipo(
        @Path("tipo") tipo: String
    ): List<GastronomiaResponse>

    // Endpoint de Login
    @POST("login")
    suspend fun login(
        @Body request: HashMap<String, String>
    ): Response<AuthResponse>

    // Endpoint de Registro
    @POST("register")
    suspend fun register(
        @Body request: HashMap<String, String>
    ): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String // Pasamos el token como "Bearer XYZ"
    ): Response<Map<String, String>>

    @FormUrlEncoded
    @POST("validar-tarjeta")
    suspend fun validarTarjeta(
        @Field("codigo") codigo: String,
        @Field("user_id") userId: Int
    ): Response<ValidationResponse>

    @GET("profile/{user_id}")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int
    ): Response<AuthResponse>
}