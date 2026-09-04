package com.example.assignment.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json


object SupabaseService {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://fwfkoqcimgvtszucaxvp.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ3ZmtvcWNpbWd2dHN6dWNheHZwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODc5MjA3MDksImV4cCI6MjEwMzQ5NjcwOX0.dUeEwLZJ6cEr9CPRBDXX-QlfKwtwvONx_75EpcjxE3M"
    ) {
        install(Postgrest) {
            // Ensure the JSON parser ignores columns not defined in your data class
            serializer = KotlinXSerializer(
                Json { ignoreUnknownKeys = true }
            )
        }
        install(Auth)
        install(Storage)
    }
}