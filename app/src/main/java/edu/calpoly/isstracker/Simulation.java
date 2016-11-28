package edu.calpoly.isstracker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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

import edu.calpoly.isstracker.IssData.ISSMath;

public class Simulation extends ApplicationAdapter {

    private Model earthModel;
    private Model skyboxModel;
    private ModelInstance earthInstance;
    private ModelInstance skyboxInstance;
    private ModelCache cache;
    private ModelBatch batch;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 10000.0f;

    public PerspectiveCamera cam;
    public Vector3 issPosition;

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

        //System.out.println("still running..." + this.toString());
    }

    @Override
    public void dispose() {
        //System.out.println("disposing test");
        cache.dispose();
        batch.dispose();
        earthModel.dispose();
        skyboxModel.dispose();
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
