package edu.calpoly.isstracker;

import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;


public class SimulationFragment extends AndroidFragmentApplication {

    public static SimulationFragment getInstance(Simulation simulation){
        SimulationFragment fragment = new SimulationFragment();
        fragment.setSimulation(simulation);
        return fragment;
    }
    //private ScheduledExecutorService ses;

    public Simulation simulation;
    //public Runnable issApiRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(simulation == null){
            simulation = new Simulation();
        }
        View v = initializeForView(simulation);
        //v.setFocusable(false);
        return v;
    }

    public Simulation getSimulation(){
        return simulation;
    }

    public void setSimulation(Simulation simulation){
        this.simulation = simulation;
    }

    @Override
    public void onStop() {
        super.onStop();
        simulation.onStop();
    }

    @Override
    public void exit() {

    }
}
