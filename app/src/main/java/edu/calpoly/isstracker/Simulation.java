package edu.calpoly.isstracker;

import android.os.Bundle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.calpoly.isstracker.IssData.ISSMath;
// source file from the other project, to be replaced with Test.java code
public class Simulation extends AndroidApplication implements ApplicationListener {

    private PerspectiveCamera cam;
    private Model earthModel;
    private Model skyboxModel;
    private ModelInstance earthInstance;
    private ModelInstance skyboxInstance;
    private ModelCache cache;
    private ModelBatch batch;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 10000.0f;
    private static final long REQUEST_INTERVAL = 2000;

    private Vector3 issPosition;

    private ScheduledExecutorService ses;
    private Runnable issApiRequest;

    private static final String API_URL = "https://api.wheretheiss.at/v1/satellites/25544";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useImmersiveMode = true;
        config.disableAudio = true;
        initialize(this, config);

        issApiRequest = new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(API_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    String result = sb.toString();

                    JSONObject json = new JSONObject(result);

                    float latitude = 0;
                    float longitude = 0;
                    float altitude = 0;
                    try {
                        latitude = Float.valueOf(json.getString("latitude"));
                        longitude = Float.valueOf(json.getString("longitude"));
                        altitude = Float.valueOf(json.getString("altitude"));

                        issPosition.x = latitude;
                        issPosition.y = longitude;
                        issPosition.z = altitude;

                        ISSMath.convertToXyz(issPosition);
                        //cam.position.set(issPosition);
                        cam.position.set(issPosition.nor().scl(ISSMath.EARTH_R * 1.7f));
                        System.out.println(issPosition.toString());
                        cam.lookAt(0f, 0f, 0f);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }
        };
    }

    @Override
    public void create() {
        issPosition = new Vector3();

        cam = new PerspectiveCamera(75f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = Z_NEAR;
        cam.far = Z_FAR;
        cam.position.set(0f, 0f, ISSMath.EARTH_R * 2);
        cam.lookAt(0f, 0f, 0f);

        AssetManager assetManager = new AssetManager();
        assetManager.load("earth.jpg", Texture.class);
        assetManager.load("stars.jpg", Texture.class);
        assetManager.finishLoading();

        Texture earthTexture = assetManager.get("earth.jpg", Texture.class);
        Material earthMaterial = new Material(TextureAttribute.createDiffuse(earthTexture),
                ColorAttribute.createSpecular(Color.WHITE), ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createAmbient(Color.WHITE));
        Texture skyboxTexture = assetManager.get("stars.jpg", Texture.class);
        Material skyboxMaterial = new Material(TextureAttribute.createDiffuse(skyboxTexture),
                ColorAttribute.createSpecular(Color.WHITE), ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createAmbient(Color.WHITE));

        ModelBuilder modelBuilder = new ModelBuilder();
        earthModel = modelBuilder.createSphere(ISSMath.EARTH_R * 2, ISSMath.EARTH_R * 2 * (1 - ISSMath.EARTH_F),
                ISSMath.EARTH_R * 2, 128, 128,
                new Material(earthMaterial),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        earthInstance = new ModelInstance(earthModel);

        modelBuilder = new ModelBuilder();
        skyboxModel = modelBuilder.createSphere(ISSMath.EARTH_R * 4, ISSMath.EARTH_R * 4,
                ISSMath.EARTH_R * 4, 32, 32,
                new Material(skyboxMaterial),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        skyboxInstance = new ModelInstance(skyboxModel);
        skyboxInstance.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));

        Array<ModelInstance> instances = new Array<ModelInstance>();
        instances.add(earthInstance);
        instances.add(skyboxInstance);

        cache = new ModelCache();
        cache.begin();
        cache.add(instances);
        cache.end();

        batch = new ModelBatch();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();
        batch.begin(cam);
        batch.render(cache);
        batch.end();
    }

    @Override
    public void dispose() {
        cache.dispose();
        batch.dispose();
        earthModel.dispose();
        skyboxModel.dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ses.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(issApiRequest, 0, REQUEST_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
