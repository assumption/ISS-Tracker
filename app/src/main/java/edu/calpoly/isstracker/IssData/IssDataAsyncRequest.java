package edu.calpoly.isstracker.IssData;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;

import edu.calpoly.isstracker.IssData.Pojos.ApiResponse_Astronaut;
import edu.calpoly.isstracker.IssData.Pojos.ApiResponse_Position;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IssDataAsyncRequest extends AsyncTask<Integer, Void, IssData> {

    public static int POSITION = 1;
    public static int ASTRONAUTS = 2;

    private IssData issData;
    private AsyncTaskCallback callback;
    private boolean timeout = false;

    public IssDataAsyncRequest(IssData issData, AsyncTaskCallback callback){
        this.issData = issData;
        this.callback = callback;
    }

    @Override
    protected IssData doInBackground(Integer... integers) {
        if(issData == null){
            issData = new IssData();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.open-notify.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IssDataService service = retrofit.create(IssDataService.class);

        try {
            for (int i = 0; i < integers.length; i++){
                if(integers[i] == POSITION){
                    ApiResponse_Position response = service.issPosition().execute().body();
                    if(response != null){
                        Log.d("IssDataAsyncRequest", "ApiResponse_Position: " + response.getMessage());
                        issData.setPosition(response.getIssPosition());
                    }
                } else if (integers[i] == ASTRONAUTS){
                    ApiResponse_Astronaut response = service.listAstronauts().execute().body();
                    if (response != null){
                        Log.d("IssDataAsyncRequest", "ApiResponse_Astronaut: " + response.getMessage());
                        issData.setAstronauts(response.getAstronaut());
                    }
                }
            }
        } catch (SocketTimeoutException e){
            Log.d("IssDataAsyncRequest", "SocketTimeoutException");
            timeout = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return issData;
    }

    @Override
    protected void onPostExecute(IssData issData) {
        super.onPostExecute(issData);

        if(callback != null){
            if(!timeout){
                callback.done(issData);
            } else {
                callback.timeoutError();
            }
        }
    }
}
