package edu.calpoly.isstracker.IssData;

import java.util.ArrayList;
import java.util.List;

import edu.calpoly.isstracker.IssData.Pojos.Astronaut;
import edu.calpoly.isstracker.IssData.Pojos.IssPosition;

public class IssData {

    private static String[] data_left_text = {"Launch", "Mass", "Length", "Width", "Height"};
    private static String[] data_right_text = {"20 November 1998", "≈ 419,455 kg (924,740 lb)",
            "72.8 m (239 ft)", "108.5 m (356 ft)", "≈ 20 m (66 ft)"};

    private IssPosition position;
    private List<Astronaut> astronauts;

    public void refreshPosition(AsyncTaskCallback callback){
        new IssDataAsyncRequest(this, callback).execute(IssDataAsyncRequest.POSITION);
    }

    public void retrieveAstronauts(AsyncTaskCallback callback){
        new IssDataAsyncRequest(this, callback).execute(IssDataAsyncRequest.ASTRONAUTS);
    }

    public List<ListItem> getDataListItems(){
        ArrayList<ListItem> dataList = new ArrayList<>();

        dataList.add(new ListItem("Facts: ", "", true, false));
        for (int i = 0; i < data_left_text.length; i++){
            dataList.add(new ListItem(data_left_text[i], data_right_text[i], false, false));
        }

        if(astronauts != null){
            dataList.add(new ListItem("Astronauts in Space: ", "", true, false));
            for (int i = 0; i < astronauts.size(); i++){
                dataList.add(new ListItem(astronauts.get(i).getName() + ", " + astronauts.get(i).getCraft(), "", false, true));
            }
        }

        return dataList;
    }

    public void setPosition(IssPosition position){
        this.position = position;
    }

    public IssPosition getPosition(){
        return position;
    }

    public void setAstronauts(List<Astronaut> astronauts){
        this.astronauts = astronauts;
    }
}
