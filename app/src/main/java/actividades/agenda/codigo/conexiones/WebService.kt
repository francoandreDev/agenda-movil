package actividades.agenda.codigo.conexiones

import actividades.agenda.codigo.modelos.tarea.Tarea
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object  AppConstantes {
    const val BASE_URL = "http://192.168.229.1:3000"
}

interface WebService {
    @GET("/tasks")
    suspend fun obtenerTareas(): Response<TareaResponse>
}

interface WebServicepost {
    @POST("/tasks")
    suspend fun addTask(@Body tarea: Tarea): Response<Any>

    @PUT("/tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body tarea: Tarea): Response<Any>

    @DELETE("/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Any>
}

object RetrofitCliente {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }
    val webServicepost: WebServicepost by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebServicepost::class.java)
    }

}
