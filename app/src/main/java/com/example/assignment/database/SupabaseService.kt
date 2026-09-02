package com.example.assignment.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage


object SupabaseService {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://fwfkoqcimgvtszucaxvp.supabase.co",
        supabaseKey = "sb_publishable_fMIDfmIRVenY0dwfyOH2vA_Fp7OUgiB"
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}