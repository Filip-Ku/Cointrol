import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NbpApiService {
    @GET("exchangerates/rates/a/{currency}/")
    fun getExchangeRate(@Path("currency") currency: String): Call<NbpExchangeRate>
}
