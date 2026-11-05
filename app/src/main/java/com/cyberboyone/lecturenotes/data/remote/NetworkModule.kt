package com.cyberboyone.lecturenotes.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Wires the Supabase REST client and a plain OkHttp client for R2 downloads.
 *
 * SUPABASE_URL / SUPABASE_ANON_KEY and R2_PUBLIC_BASE_URL should come from
 * BuildConfig fields set per build variant (local.properties / CI secrets),
 * not hardcoded. Placeholders here so the module compiles standalone.
 */
object NetworkModule {

    private const val SUPABASE_URL = "https://vebuqchewawykyavkgfh.supabase.co/"
    private const val SUPABASE_ANON_KEY = "sb_publishable_YdF1p5anBAAr9k9ONjC9cw_lRrB34mJ"
    const val R2_PUBLIC_BASE_URL = "https://nakudin.com/"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .build()
        chain.proceed(request)
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // Plain client for R2 — no Supabase auth headers needed for public reads.
    val r2Client: OkHttpClient = OkHttpClient.Builder().build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val supabaseApi: SupabaseApi = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(SupabaseApi::class.java)
}
