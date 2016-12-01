package edu.calpoly.isstracker;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.CardBoardAndroidApplication;
import com.badlogic.gdx.backends.android.CardBoardApplicationListener;
import com.badlogic.gdx.backends.android.CardboardCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

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

import edu.calpoly.isstracker.IssData.AsyncTaskCallback;
import edu.calpoly.isstracker.IssData.ISSMath;
import edu.calpoly.isstracker.IssData.IssData;
import edu.calpoly.isstracker.IssData.Pojos.IssPosition;

public class SimulationVR extends CardBoardAndroidApplication implements CardBoardApplicationListener {

    private CardboardCamera cam;
    private Model earthModel;
    private Model skyboxModel;
    private ModelInstance earthInstance;
    private ModelInstance skyboxInstance;
    private ModelCache cache;
    private ModelBatch batch;
    private Environment environment;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 10000.0f;
    private static final long REQUEST_INTERVAL = 2000;

    private Vector3 issPosition;
    private DirectionalLight light;

    /*private ScheduledExecutorService ses;
    private Runnable issApiRequest;*/

    /*private static final String API_URL = "https://api.wheretheiss.at/v1/satellites/25544";*/

    private IssData issData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.disableAudio = true;
        initialize(this, config);

        issData = new IssData();
        listenToIssPosition();
        issData.startRefreshingPosition();

        /*issApiRequest = new Runnable() {
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
                        cam.position.set(issPosition);
                        cam.lookAt(0f, issPosition.y, 0f);
                        light.set(0.2f, 0.2f, 0.2f, issPosition.x * -1, issPosition.y * -1, issPosition.z * -1);

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
        };*/
    }

    private void listenToIssPosition(){
        issData.listenToPositionRefreshing(new AsyncTaskCallback() {
            @Override
            public void done(IssData issData) {
                IssPosition position = issData.getPosition();
                issPosition.x = position.getLatitude();
                issPosition.y = position.getLongitude();
                issPosition.z = position.getAltitude();

                ISSMath.convertToXyz(issPosition);
                cam.position.set(issPosition);
                cam.lookAt(0f, issPosition.y, 0f);
                light.set(0.2f, 0.2f, 0.2f, issPosition.x * -1, issPosition.y * -1, issPosition.z * -1);
            }
        });
    }

    @Override
    public void create() {
        issPosition = new Vector3();
        cam = new CardboardCamera();
        light = new DirectionalLight();

        cam.near = Z_NEAR;
        cam.far = Z_FAR;

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(light);

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
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        earthInstance = new ModelInstance(earthModel);

        modelBuilder = new ModelBuilder();
        skyboxModel = modelBuilder.createSphere(ISSMath.EARTH_R * 4, ISSMath.EARTH_R * 4,
                ISSMath.EARTH_R * 4, 32, 32,
                new Material(skyboxMaterial),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
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
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        cache.dispose();
        batch.dispose();
        earthModel.dispose();
        skyboxModel.dispose();
    }

    @Override
    public void onNewFrame(HeadTransform paramHeadTransform) {

    }

    @Override
    public void onDrawEye(Eye eye) {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the camera.
        cam.setEyeViewAdjustMatrix(new Matrix4(eye.getEyeView()));

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        cam.setEyeProjection(new Matrix4(perspective));
        cam.update();

        batch.begin(cam);
        batch.render(cache, environment);
        batch.end();
    }

    @Override
    public void onFinishFrame(Viewport paramViewport) {

    }

    @Override
    public void onRendererShutdown() {

    }

    @Override
    public void onCardboardTrigger() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        issData.stopRefreshingPosition();
    }

    /*@Override
    protected void onPause() {
        super.onPause();

        ses.shutdown();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        issData.startRefreshingPosition();

        /*ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(issApiRequest, 0, REQUEST_INTERVAL, TimeUnit.MILLISECONDS);*/
    }
}