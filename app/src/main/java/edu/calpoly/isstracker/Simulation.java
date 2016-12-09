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

class Simulation extends ApplicationAdapter implements GestureDetector.GestureListener {

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
    private Vector3 camX = new Vector3(1f, 0f, 0f);
    private Vector3 camY = new Vector3(0f, 1f, 0f);
    private float lastInitialDist = -1;
    private float lastDist = -1;

    private Vector3 issPosition;

    private IssData issData;

    @Override
    public void create() {
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
        assetManager.load("issmodel.g3db", Model.class);
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

        issModel = assetManager.get("issmodel.g3db", Model.class);
        issInstance = new ModelInstance(issModel);

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
    }

    private void listenToIssPosition() {
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
    public void render() {
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
    }

    public void onStop() {
        issData.stopRefreshingPosition();
    }

    @Override
    public void resume() {
        issData.startRefreshingPosition();
    }

    public void onSlide(float slideOffset) {
    }

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
        float dampening = 8 * (float) Math.pow((ISSMath.EARTH_R * 3.5f / cam.position.len()), 2);

        camX.rotate(camY, -deltaX / dampening);
        camY.rotate(camX, -deltaY / dampening);
        cam.rotateAround(origin, camY, -deltaX / dampening);
        cam.rotateAround(origin, camX, -deltaY / dampening);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        float delta;

        if (lastInitialDist != initialDistance) {
            delta = initialDistance - distance;
            lastInitialDist = initialDistance;
        } else {
            delta = lastDist - distance;
        }

        lastDist = distance;

        float camLen = cam.position.len() + delta / 2;

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
