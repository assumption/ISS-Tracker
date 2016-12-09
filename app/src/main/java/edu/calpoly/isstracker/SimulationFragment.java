package edu.calpoly.isstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

public class SimulationFragment extends AndroidFragmentApplication {

    public static SimulationFragment getInstance(Simulation simulation){
        SimulationFragment fragment = new SimulationFragment();
        fragment.setSimulation(simulation);
        return fragment;
    }

    public Simulation simulation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(simulation == null){
            simulation = new Simulation();
        }

        //Workaround for SurfaceView drawing over NavDrawer
        RelativeLayout rl = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        View v = new View(getContext());
        v.setLayoutParams(params);
        View sim = initializeForView(simulation);
        sim.setLayoutParams(params);
        rl.addView(sim);
        rl.addView(v);
        return rl;
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
