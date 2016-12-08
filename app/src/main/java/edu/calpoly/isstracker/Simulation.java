package edu.calpoly.isstracker;

import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.JsonValue;

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

class Simulation extends ApplicationAdapter implements GestureDetector.GestureListener, InputProcessor {

    private static final String TAG = "SIMULATION";
    private PerspectiveCamera cam;
    private Model earthModel;
    private Model skyboxModel;
    private Model issModel;
    private ModelInstance earthInstance;
    private ModelInstance skyboxInstance;
    private ModelInstance issInstance;
    private ModelCache cache;
    private ModelBatch batch;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = ISSMath.EARTH_R * 10;

    private Vector3 origin = new Vector3(0f, 0f, 0f);
    private Vector3 yAxis = new Vector3(0f, 1f, 0f);
    private Vector3 camAxis = new Vector3(1f, 0f, 0f);
    private int lastX = 0;
    private int lastY = 0;

    private Vector3 issPosition;

    //private ScheduledExecutorService ses;
    //private Runnable issApiRequest;

    //private static final long REQUEST_INTERVAL = 2000;
    //private static final String API_URL = "https://api.wheretheiss.at/v1/satellites/25544";
    private IssData issData;

    @Override
    public void create() {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setInputProcessor(new GestureDetector(this));

        issPosition = new Vector3();

        cam = new PerspectiveCamera(75f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = Z_NEAR;
        cam.far = Z_FAR;
        cam.position.set(0f, 0f, ISSMath.EARTH_R * 3.5f);
        cam.lookAt(0f, 0f, 0f);

        AssetManager assetManager = new AssetManager();
        assetManager.load("earth.jpg", Texture.class);
        assetManager.load("stars.jpg", Texture.class);
        /*assetManager.load("issmodel.g3db", Model.class);*/
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
        skyboxModel = modelBuilder.createSphere(ISSMath.EARTH_R * 10, ISSMath.EARTH_R * 10,
                ISSMath.EARTH_R * 10, 32, 32,
                new Material(skyboxMaterial),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        skyboxInstance = new ModelInstance(skyboxModel);
        skyboxInstance.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));

        modelBuilder = new ModelBuilder();
        issModel = modelBuilder.createSphere(ISSMath.EARTH_R / 20, ISSMath.EARTH_R / 20,
                ISSMath.EARTH_R / 30, 32, 32,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        issInstance = new ModelInstance(issModel);

        /*issModel = assetManager.get("issmodel.g3db", Model.class);*/
        /*ModelLoader loader = new ObjLoader();
        issModel = loader.loadModel(Gdx.files.internal("assets/iss.obj"));
        issInstance = new ModelInstance(issModel);*/

        Array<ModelInstance> instances = new Array<>();
        instances.add(earthInstance);
        instances.add(skyboxInstance);

        cache = new ModelCache();
        cache.begin();
        cache.add(instances);
        cache.end();

        batch = new ModelBatch();

        issData = new IssData();
        listenToIssPosition();
        issData.startRefreshingPosition();

        //initIssApiRequest();
    }

    /*public void initIssApiRequest() {
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
                        updateIssPosition();

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

        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(issApiRequest, 0, REQUEST_INTERVAL, TimeUnit.MILLISECONDS);
    }*/

    private void listenToIssPosition(){
        issData.listenToPositionRefreshing(new AsyncTaskCallback() {
            @Override
            public void done(IssData issData) {
                IssPosition position = issData.getPosition();
                issPosition.x = position.getLatitude();
                issPosition.y = position.getLongitude();
                issPosition.z = position.getAltitude();

                ISSMath.convertToXyz(issPosition);
                updateIssPosition();
            }
        });
    }

    private void updateIssPosition() {
        issInstance.transform.setToTranslation(issPosition.x, issPosition.y, issPosition.z);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();
        batch.begin(cam);
        batch.render(cache);
        batch.render(issInstance);
        batch.end();
    }

    @Override
    public void dispose() {
        cache.dispose();
        batch.dispose();
        earthModel.dispose();
        skyboxModel.dispose();
        issModel.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        /*ses.shutdown();*/
    }

    public void onStop(){
        issData.stopRefreshingPosition();
    }

    @Override
    public void resume() {
        /*ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(issApiRequest, 0, REQUEST_INTERVAL, TimeUnit.MILLISECONDS);*/
        issData.startRefreshingPosition();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastX = screenX;
        lastY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        int deltaX = screenX - lastX;
        int deltaY = screenY - lastY;

        camAxis.rotate(yAxis, -deltaX / 8);
        cam.rotateAround(origin, yAxis, -deltaX / 8);
        cam.rotateAround(origin, camAxis, -deltaY / 8);
        lastX = screenX;
        lastY = screenY;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void onSlide(float slideOffset){
        //scale and move simulation upwards
    }

    //I tried myself on gestures :)
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        float dampening = 8 * (ISSMath.EARTH_R * 3.5f / cam.position.len());

        camAxis.rotate(yAxis, -deltaX / dampening);
        cam.rotateAround(origin, yAxis, -deltaX / dampening);
        cam.rotateAround(origin, camAxis, -deltaY / dampening);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        float delta =  (initialDistance - distance) / 32;
        float camLen = cam.position.len() + delta;

        if (camLen < ISSMath.EARTH_R * 4.9 && camLen > ISSMath.EARTH_R * 1.1) {
            cam.position.setLength(camLen);
        }

        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
