/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;



import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Icyboxs
 */
public class TestsCenarioState extends BaseAppState {
 private AssetManager assetManager;
 private SimpleApplication simpleApp;
  private BulletAppState bulletAppState;
  private Node ScenarioRoot = new Node("ScenarioRoot");
    @Override
    protected void initialize(Application app) {
        simpleApp = (SimpleApplication) app;
       assetManager = app.getAssetManager();
        bulletAppState = app.getStateManager().getState(BulletAppState.class);

        
       //Node Scenario =  (Node)assetManager.loadModel("Models/dixing/zokiew-w-18-wieku-zhovkva-in-18th-century.j3o");
       
              
        Box b = new Box(500, 1, 500);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        geom.move(0, -5, 0);
       
        Node Scenario= new Node();
        Scenario.attachChild(geom);
        ScenarioRoot.attachChild(Scenario);
        Scenario.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);// 承载阴影
        //Scenario.setMaterial(assetManager.loadMaterial("Models/Test/Test.j3m"));

        RigidBodyControl rigidBody = new RigidBodyControl(0);
//MeshCollisionShape rigidBody1 = new MeshCollisionShape(Scenario.getChild(0).);
        ScenarioRoot.addControl(rigidBody);
        bulletAppState.getPhysicsSpace().add(rigidBody); 
        bulletAppState.startPhysics();
        
    }

    @Override
    protected void cleanup(Application aplctn) {

    }

    @Override
    protected void onEnable() {
        simpleApp.getRootNode().attachChild(ScenarioRoot);
        bulletAppState.setDebugEnabled(true);
       
       // bulletAppState.setDebugEnabled(true);
//            // 点光源影子
//    DirectionalLightShadowRenderer DLSR = new DirectionalLightShadowRenderer(assetManager, 1024,4);
//    DLSR.setLight((DirectionalLight) simpleApp.getRootNode().getLocalLightList().get(1));// 设置点光源
//    DLSR.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
//    simpleApp.getViewPort().addProcessor(DLSR);
    
        /* 产生阴影 */
    final int SHADOWMAP_SIZE=1024;

    DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
    dlsf.setLight((DirectionalLight) simpleApp.getRootNode().getLocalLightList().get(1));
    dlsf.setEnabled(true);
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(dlsf);
    simpleApp.getViewPort().addProcessor(fpp);
    
    }

    @Override
    protected void onDisable() {
           ScenarioRoot.removeFromParent();
    }


    
}
