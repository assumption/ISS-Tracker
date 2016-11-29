package edu.calpoly.isstracker.IssData;

import edu.calpoly.isstracker.IssData.Pojos.IssPosition;
import retrofit2.Call;
import retrofit2.http.GET;

interface IssPositionService {
    @GET("satellites/25544")
    Call<IssPosition> getIssPosition();
}
