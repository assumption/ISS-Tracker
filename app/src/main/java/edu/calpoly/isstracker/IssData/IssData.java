package edu.calpoly.isstracker.IssData;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.calpoly.isstracker.IssData.Pojos.ApiResponse_Astronaut;
import edu.calpoly.isstracker.IssData.Pojos.Astronaut;
import edu.calpoly.isstracker.IssData.Pojos.IssPosition;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IssData {

    private static final String POSITION_API_URL = "https://api.wheretheiss.at/v1/";
    private static final String ASTRONAUTS_API_URL = "http://api.open-notify.org/";

    private static String[] data_left_text = {"Launch", "Mass", "Length", "Width", "Height"};
    private static String[] data_right_text = {"20 November 1998", "≈ 419,455 kg (924,740 lb)",
            "72.8 m (239 ft)", "108.5 m (356 ft)", "≈ 20 m (66 ft)"};

    private IssPosition position;
    private List<Astronaut> astronauts;

    private Timer t;
    private AsyncTaskCallback positionListener;

    public void retrieveAstronauts(final AsyncTaskCallback callback){
        class AsyncRequest extends AsyncTask<Integer, Void, IssData> {
            @Override
            protected IssData doInBackground(Integer... integers) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ASTRONAUTS_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                IssDataService service = retrofit.create(IssDataService.class);

                try {
                    ApiResponse_Astronaut response = service.listAstronauts().execute().body();
                    if (response != null){
                        IssData.this.setAstronauts(response.getAstronaut());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return IssData.this;
            }

            @Override
            protected void onPostExecute(IssData issData) {
                super.onPostExecute(issData);
                if(callback != null){
                    callback.done(issData);
                }
            }
        }
        new AsyncRequest().execute();
    }

    public void listenToPositionRefreshing(AsyncTaskCallback positionListener){
        this.positionListener = positionListener;
    }

    public void startRefreshingPosition(){
        class AsyncPositionRequest extends AsyncTask<Void, Void, IssData>{
            @Override
            protected IssData doInBackground(Void... voids) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(POSITION_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                IssPositionService service = retrofit.create(IssPositionService.class);

                try {
                    IssPosition response = service.getIssPosition().execute().body();
                    if(response != null){
                        IssData.this.setPosition(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return IssData.this;
            }

            @Override
            protected void onPostExecute(IssData issData) {
                super.onPostExecute(issData);
                if(positionListener != null
                        && issData.getPosition() != null){
                    positionListener.done(IssData.this);
                }
            }
        }

        if (t != null) {
            t.cancel();
            t.purge();
            t = null;
        }
        t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        new AsyncPositionRequest().execute();
                    }
                }, 0, 2 * 1000);
    }

    public void stopRefreshingPosition(){
        if (t != null) {
            t.cancel();
            t.purge();
            t = null;
        }
    }

    public List<ListItem> getDataListItems(){
        ArrayList<ListItem> dataList = new ArrayList<>();

        dataList.add(new ListItem("Facts: ", "", true, false));
        for (int i = 0; i < data_left_text.length; i++){
            dataList.add(new ListItem(data_left_text[i], data_right_text[i], false, false));
        }

        if(position != null){
            dataList.add(new ListItem("Velocity", String.valueOf(position.getVelocity() + " km/h"), false, false));
            dataList.add(new ListItem("Altitude", String.valueOf(position.getAltitude() + " km"), false, false));
        }

        if(astronauts != null){
            dataList.add(new ListItem("Astronauts in Space: ", "", true, false));
            for (int i = 0; i < astronauts.size(); i++){
                dataList.add(new ListItem(astronauts.get(i).getName() + ", " + astronauts.get(i).getCraft(), "", false, true));
            }
        }

        return dataList;
    }

    private void setPosition(IssPosition position){
        this.position = position;
    }

    public IssPosition getPosition(){
        return position;
    }

    private void setAstronauts(List<Astronaut> astronauts){
        this.astronauts = astronauts;
    }
}
