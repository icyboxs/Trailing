package com.mygame;


import Minie.MinieBulletAppState;
import control.TestsCenarioState;
import control.TestRole;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.FlyByCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.shadow.ShadowUtil;
import control.MinieMovementControl;




/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author icyboxs
 */
public class Main extends SimpleApplication {

  
  public Main(){
        super(  
              new MinieBulletAppState(),
              new StatsAppState(), 
              new AudioListenerState(),
              new DebugKeysAppState(),
              new TestsCenarioState(),
             new TestRole(),
             new MinieMovementControl()


              );
    }
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
        

    @Override
    public void simpleInitApp() {

        
       // flyCam.setEnabled(true);
        createArrow(new Vector3f(5, 0, 0), ColorRGBA.Green);
        createArrow(new Vector3f(0, 5, 0), ColorRGBA.Red);
        createArrow(new Vector3f(0, 0, 5), ColorRGBA.Blue);
        
        
//        Node probeNode = (Node) assetManager.loadModel("Models/Syana/defaultProbe.j3o");
//        LightProbe probe = (LightProbe) probeNode.getLocalLightList().iterator().next();
//        rootNode.addLight(probe);
      
         // 环境光
        AmbientLight ambient = new AmbientLight();
        //ambient.setColor(new ColorRGBA(1f, 1f, 1f, 1f));
        ambient.setColor(new ColorRGBA(4,4,4,1));
        rootNode.addLight(ambient);
      
        
        

        // 阳光
        DirectionalLight sun = new DirectionalLight();
        sun.setName("sun");
        sun.setDirection(new Vector3f(-15, -5, 5).normalizeLocal());
       // sun.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());
        //sun.setColor(ColorRGBA.White);
        sun.setColor(new ColorRGBA(4,4,4,1));
        rootNode.addLight(sun);
    //    System.out.println(rootNode.getLocalLightList().get(1).getName()+"灯光");
        // 定向光影子
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 4);
        dlsr.setLight(sun);// 设置定向光源
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        viewPort.addProcessor(dlsr);
        

        
//        DirectionalLightShadowRenderer dlsr= new DirectionalLightShadowRenderer(assetManager, 1024,3);
//        dlsr.setLight(sun);
//        dlsr.setLambda(0.55f);
//        dlsr.setShadowIntensity(0.8f);
//        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
//        viewPort.addProcessor(dlsr);
//        Camera camera = cam.clone();
//        camera.setViewPort(0f, 1, .5f, 1f);
//        camera.setLocation(new Vector3f(-15f, 5f, -10));
//        camera.lookAt(new Vector3f(1, 1, 0), Vector3f.UNIT_Y);
//
//        ViewPort vp = renderManager.createMainView("TopCam", camera);
//        vp.setClearFlags(true, true, true);
//        vp.attachScene(rootNode);
      /**反转问题 需要查看追逐相机源码**/
      // rootNode.getChild("model").addControl(new RoleControl());

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
           

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    

       private void createArrow(Vector3f vec3, ColorRGBA color) {
        // 创建材质，设定箭头的颜色
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);

        // 创建几何物体，应用箭头网格。
        Geometry geom = new Geometry("arrow", new Arrow(vec3));
        geom.setMaterial(mat);

        // 添加到场景中
         rootNode.attachChild(geom);
    }

}
