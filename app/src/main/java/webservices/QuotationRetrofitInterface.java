package webservices;

import model.Quotation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QuotationRetrofitInterface {

    @GET("https://api.forismatic.com/api/1.0/?method=getQuote&format=json")
    Call<Quotation> getCurrentQuotation(@Query("lang") String l);
}
