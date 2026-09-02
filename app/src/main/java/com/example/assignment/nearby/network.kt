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
    val location: LatLng
)
//data class NearbySearchRequest(
//    val includedTypes: List<String> = listOf("hospital", "clinic", "pharmacy"),
//    val maxResultCount: Int = 10,
//    val locationRestriction: LocationRestriction
//)
//
//data class LocationRestriction(
//    val circle: CircleRestriction
//)
//
//data class CircleRestriction(
//    val center: CenterPoint,
//    val radius: Double = 5000.0 // Radius in meters (5 km)
//)
//
//data class CenterPoint(
//    val latitude: Double,
//    val longitude: Double
//)
//
//// --- Response Models ---
//data class PlacesResponse(
//    @SerializedName("places") val places: List<PlaceDto>?
//)
//
//data class PlaceDto(
//    val id: String,
//    val displayName: LocalizedText?,
//    val formattedAddress: String?,
//    val location: LatLngDto?,
//    val primaryTypeDisplayName: LocalizedText?,
//    val currentOpeningHours: OpeningHoursDto?
//)
//
//data class LocalizedText(val text: String)
//data class LatLngDto(val latitude: Double, val longitude: Double)
//data class OpeningHoursDto(val openNow: Boolean?)
//
//// --- Retrofit API Service ---
//interface PlacesApiService {
//    @POST("v1/places:searchNearby")
//    suspend fun searchNearby(
//        @Header("X-Goog-Api-Key") apiKey: String,
//        @Header("X-Goog-FieldMask") fieldMask: String = "places.id,places.displayName,places.formattedAddress,places.location,places.primaryTypeDisplayName,places.currentOpeningHours",
//        @Body request: NearbySearchRequest
//    ): PlacesResponse
//}

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