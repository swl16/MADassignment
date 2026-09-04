package com.example.assignment.nearby

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class Facility(
    val id: String,
    val label: String,
    val name: String,
    val type: String,
    val distance: String,
    val openingHours: String,
    val lat: Double,
    val lon: Double
)

data class OverpassResponse(
    @SerializedName("elements") val elements: List<OsmElement>?
)

data class OsmElement(
    val id: Long,
    val lat: Double?,
    val lon: Double?,
    val tags: OsmTags?
)

data class OsmTags(
    val name: String?,
    val amenity: String?,          // "hospital", "clinic", "pharmacy", "doctors"
    @SerializedName("opening_hours") val openingHours: String?,
    @SerializedName("addr:street") val street: String?
)

// --- Retrofit Interface ---
interface OverpassApiService {
    @Headers(
        "Accept: application/json",
        "User-Agent: MADAssignmentHealthCareApp/1.0 (Android; StudentProject)"
    )
    @GET("api/interpreter")
    suspend fun getNearbyFacilities(
        @Query("data") query: String
    ): OverpassResponse
}

// --- Retrofit Singleton ---
object RetrofitClient {
    private const val BASE_URL = "https://overpass-api.de/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "HealthcareApp/1.0 (contact: student@tarumt.edu.my)")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: OverpassApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OverpassApiService::class.java)
    }
}