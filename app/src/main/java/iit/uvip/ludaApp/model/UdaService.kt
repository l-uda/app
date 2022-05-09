package iit.uvip.ludaApp.model


import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface UdaService {

    // "https://www.sagosoft.it/_API_/cpim/luda/www/luda_20210111_1500/api/app/"
    // {"api":"API","version":"1.0","type":"UDA","function":"GET","i":"2","k":0,"status":"1","error":null,"description":"Questa funzione funziona!","notes":["notes","Lorem","Ipse","Dicitur"]}
    @GET("get/?")
    fun getStatus(@Query("i") i: Int, @Query("k") k: Int = -1): Observable<StatusResult>

    @PUT("put/?")
    fun putStatus(@Query("i") i: Int, @Query("k") k: Int = -1, @Query("status") status: Int, @Query("data") data: String? = ""): Observable<StatusResult>

    companion object {

        fun create(BASE_URL:String): UdaService {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()
            return retrofit.create(UdaService::class.java)
        }
    }
}

data class StatusResult(var type: String = "",
                        var i:Int = -1,
                        var k:Int = -1,
                        var status:Int? = null,
                        var data: String? = null,
                        var uda_id: List<Int>? = null,
                        var indizi: List<Int>? = null,
                        var error: String? = null,
                        var description: String? = null)
