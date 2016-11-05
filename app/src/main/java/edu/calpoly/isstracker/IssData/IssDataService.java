package edu.calpoly.isstracker.IssData;

import edu.calpoly.isstracker.IssData.Pojos.ApiResponse_Astronaut;
import edu.calpoly.isstracker.IssData.Pojos.ApiResponse_Position;
import retrofit2.Call;
import retrofit2.http.GET;

interface IssDataService {
    @GET("astros.json")
    Call<ApiResponse_Astronaut> listAstronauts();

    @GET("iss-now.json")
    Call<ApiResponse_Position> issPosition();
}

