package com.example.assignment.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseService {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://fwfkoqcimgvtszucaxvp.supabase.co",
        supabaseKey = "sb_secret_LX-D5P2bt5inL3I4FTL-yA_mJBxybxR"
    ) {
        install(Postgrest)
        install(Auth)
    }
}