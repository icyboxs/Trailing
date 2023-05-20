/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;




import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.Armature;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.action.Action;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.animation.AttachmentLink;
import com.jme3.bullet.animation.BoneLink;
import com.jme3.bullet.animation.DynamicAnimControl;
import com.jme3.bullet.animation.IKJoint;
import com.jme3.bullet.animation.LinkConfig;
import com.jme3.bullet.animation.RangeOfMotion;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.SoftBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.ShadowUtil;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainGridListener;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.material.RenderState;
import trailing.TrailingObject;
/**
 *
 * @author icyboxs
 */
    public class TestRole extends BaseAppState{
    
    private Ray ray=new Ray();   
    private InputManager inputManager;
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;   
    private SimpleApplication simpleApp;
    private AnimComposer control;
    public Node model = new Node("model");
    public Node camNode = new Node("cam");
    private Camera cam;
    private TerrainChaseCamera chaseCam;

    public boolean left = false, right = false, up = false, down = false, v = false,isPresseds=false;
    public final static String BUTTON_LEFT = "	BUTTON_LEFT";
    public final static String DEBUG = "DEBUG";
    public final static String DEBUG1 = "DEBUG1";
    public final static String FORWARD = "forward";
    public final static String BACKWARD = "backward";
    public final static String LEFT = "left";
    public final static String RIGHT = "right";
    public final static String V = "v";
    public final static String JUMP = "jump";
    public Vector3f camDir = new Vector3f();
    public Vector3f camLeft = new Vector3f();
    private Quaternion camRotation = new Quaternion();
    
    private String trailingTagName = "trailingNode";
    private Spatial mTrailingSpatial;
    private TrailingObject mTrailingObject;
    private Material trailingMaterial;
    private boolean mIsFirst = true;
    private boolean enableTrailing = true;
    private float length = 5.0f;
    
    
    public TestRole(){
        mTrailingObject = new TrailingObject();
        mTrailingObject.setup();

    }
    
    
    @Override
    protected void initialize(Application aplctn) {
        simpleApp = (SimpleApplication) aplctn;
        assetManager = aplctn.getAssetManager();
        inputManager = aplctn.getInputManager();
        cam=aplctn.getCamera();
        trailingMaterial = assetManager.loadMaterial("Materials/Generated/TrailingTest.j3m");
        Box b = new Box(2, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        geom.move(0, 10, 0);
        
    //   Node modelABC = ((Node) assetManager.loadModel("Models/model/abc.j3o"));
        model.attachChild(geom);
        camNode.setLocalTranslation(0, 10, 0);
         model.attachChild(camNode);
        
        simpleApp.getRootNode().attachChild(model);
        //initKeys();
   
        
    }
    
//在应用程序状态分离后或在应用程序关闭期间调用（如果状态仍处于附加状态）。
    @Override
    protected void cleanup(Application aplctn) {
        
    }
    
//当状态完全启用时调用，即：已附加并且 isEnabled() 为 true 或当状态附加后 setEnabled() 状态发生变化时调用。
    @Override
    protected void onEnable() {
        System.err.println(model.getChildren());
        chaseCam = new TerrainChaseCamera(cam, model.getChild("cam"), inputManager, simpleApp);
        chaseCam.setDebugFrustum(false);
       // chaseCam.createCameraFrustum();// 开启DEBUG 相机
        chaseCam.setCameraCollisionDetection(true, "ScenarioRoot");//开启地形碰撞设置地形名称
        chaseCam.setDragToRotate(false); //把鼠标锁定在窗口里
        chaseCam.setCursorVisible(false);//设置光标不可见
        chaseCam.setInvertVerticalAxis(true);//反转鼠标的垂直轴移动
        chaseCam.setMinDistance(1f);
        chaseCam.setMaxDistance(1000f);
        chaseCam.setDefaultVerticalRotation(0);
        chaseCam.setMinVerticalRotation(-1.57f);
    }
    

    
//当状态先前启用但现在由于调用 setEnabled(false) 或正在清理状态而被禁用时调用。
    @Override
    protected void onDisable() {
        
    }
    
    @Override
    public void update(float tpf) {
        //RoleMove();
          if(trailingMaterial != null && mIsFirst){
            mIsFirst = false;
            mTrailingObject.mTrailingGeometry.setMaterial(trailingMaterial);
            trailingMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            trailingMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            ((Node)simpleApp.getRootNode()).attachChild(mTrailingObject.mTrailingGeometry);
        }
        if(trailingMaterial != null && enableTrailing){
            mTrailingObject.setTagSpatial(model);
        //    System.err.println("localtion:" + model.getLocalTranslation());
            mTrailingObject.setTrailingVec(new Vector3f(0, 10, 0), new Vector3f(0, 0, 0));
         //   System.err.println(model.getWorldTranslation().add(model.getLocalRotation().getRotationColumn(1).mult(length)));
        //mTrailingObject.setTrailingVec(new Vector3f(0, 3, 0), new Vector3f(0, 0, 0));.getLocalTranslation()
            mTrailingObject.setTrailing(true);
            //开始采样
            //从这开始继续看
            mTrailingObject.sampling((long) (tpf * 1000L));
    }
   }

    
//       private void initKeys() {
//        inputManager.addMapping(BUTTON_LEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
//        inputManager.addMapping(DEBUG, new KeyTrigger(KeyInput.KEY_F1));
//        inputManager.addMapping(DEBUG1, new KeyTrigger(KeyInput.KEY_F2));
//        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
//        inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
//        inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
//        inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
//        inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
//        inputManager.addMapping(V, new KeyTrigger(KeyInput.KEY_V));
//        inputManager.addListener(this, BUTTON_LEFT, DEBUG, DEBUG1, LEFT, RIGHT, FORWARD, BACKWARD, JUMP, V);
//    }
       

}
