/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.marsphotos.network

import com.example.marsphotos.model.MarsPhoto
import retrofit2.Retrofit
import retrofit2.http.GET

/**
 * A public interface that exposes the [getPhotos] method
 */
interface MarsApiService {
    /**
     * Devuelve una lista de [MarsPhoto] desde el endpoint "photos".
     */
    @GET("photos")
    suspend fun getPhotos(): List<MarsPhoto>

    companion object {
        private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

        fun create(): MarsApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MarsApiService::class.java)
        }
    }
}