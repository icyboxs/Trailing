/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author Icyboxs
 */
public class MinieMovementControl extends BaseAppState implements ActionListener{
    private SimpleApplication simpleApp;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera cam;
    public static BulletAppState bulletAppState;
    //方向控制器
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private Vector3f viewDirection = new Vector3f();
    private final Vector3f viewDirUP = new Vector3f(0,0,1f);
    private final Vector3f viewDirLeft = new Vector3f (1,0, 1);
    
    private Vector3f dir; // 向量
    private Quaternion targetQua = new Quaternion();
    private Quaternion Quat = new Quaternion();
    private Quaternion rotationi= new Quaternion();
    private Quaternion rotationii= new Quaternion();
    private Vector3f TPS = new Vector3f();
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    public boolean left = false, right = false, up = false, down = false, v = false, isPresseds=false;
    public final static String BUTTON_LEFT = "	BUTTON_LEFT";
    public final static String DEBUG = "DEBUG";
    public final static String DEBUG1 = "DEBUG1";
    public final static String FORWARD = "forward";
    public final static String BACKWARD = "backward";
    public final static String LEFT = "left";
    public final static String RIGHT = "right";
    public final static String V = "v";
    public final static String JUMP = "jump";
     Node character = new Node("character");
    @Override
    protected void initialize(Application aplctn) {
        
        simpleApp = (SimpleApplication) aplctn;
        assetManager = aplctn.getAssetManager();
        inputManager = aplctn.getInputManager();
        cam=aplctn.getCamera();
        bulletAppState = simpleApp.getStateManager().getState( BulletAppState.class);
        bulletAppState.setDebugEnabled(true);
        float radius = 1.5f;// 胶囊半径0.3米
        float height = 5.5f;// 胶囊身高1.8米
        float stepHeight = 0.5f;// 角色步高0.5米
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(radius, height, 1);
        player = new CharacterControl(capsuleShape,stepHeight);
        player.setJumpSpeed(10f);// 起跳速度
        player.setFallSpeed(20f);// 坠落速度
        player.setGravity(9.81f * 3);// 重力加速度
        player.setPhysicsLocation(new Vector3f(0, 1f, 0));
        //player.getCharacter().setMaxSlope(0.5f);
        (simpleApp.getRootNode().getChild("model")).move(0,  -(height / 2 + radius), 0);
        character.attachChild((simpleApp.getRootNode().getChild("model")));
           // (simpleApp.getRootNode().getChild("model")).scale(1.8f);
        initKeys();
        walkDirection.x=0.01f;
        viewDirection.set(Vector3f.UNIT_Z);
    }

    @Override
    protected void cleanup(Application aplctn) {
            
    }

    @Override
    protected void onEnable() {
            bulletAppState.getPhysicsSpace().add(player);
            character.addControl(player);
            simpleApp.getRootNode().attachChild(character);
            
    }

    @Override
    protected void onDisable() {
           
    }
    
    @Override
    public void update(float tpf) {
        camDir.set(cam.getDirection().mult(1f));
        camLeft.set(cam.getLeft().mult(1f));
        walkDirection.set(0, 0, 0);
        
        camDir.setY(0).normalizeLocal();
        camLeft.setY(0).normalizeLocal();
        //System.err.println(camDir.y+","+camLeft.y);
        //viewDirection.set(camDir);
        // 计算运动方向

        boolean changed = false;
        if(up){

        
        
        Quaternion rotation = simpleApp.getRootNode().getChild("model").getLocalRotation().clone();
        Vector3f direction = camDir;//.subtractLocal(simpleApp.getRootNode().getChild("model").getWorldTranslation())
        targetQua.lookAt(direction, Vector3f.UNIT_Y);
        rotation.nlerp(targetQua, 0.1f);
        //viewDirection.set(rotation.mult(Vector3f.UNIT_Z));
        simpleApp.getRootNode().getChild("model").setLocalRotation(rotation);

        walkDirection.addLocal(camDir);
        changed = true;
        } 
        if(down){

        Quaternion rotation = simpleApp.getRootNode().getChild("model").getLocalRotation().clone();
        Vector3f direction = camDir.negate();//.subtractLocal(simpleApp.getRootNode().getChild("model").getWorldTranslation())
        targetQua.lookAt(direction, Vector3f.UNIT_Y);
        rotation.nlerp(targetQua, 0.1f);
        //viewDirection.set(rotation.mult(Vector3f.UNIT_Z));
        simpleApp.getRootNode().getChild("model").setLocalRotation(rotation);
        walkDirection.addLocal(camDir.negate());
        changed = true;
        }
        if(left){
        
        Quaternion rotation = simpleApp.getRootNode().getChild("model").getLocalRotation().clone();
        Vector3f direction = camLeft;//.subtractLocal(simpleApp.getRootNode().getChild("model").getWorldTranslation())
        targetQua.lookAt(direction, Vector3f.UNIT_Y);
        rotation.nlerp(targetQua, 0.1f);
        //viewDirection.set(rotation.mult(Vector3f.UNIT_Z));
        simpleApp.getRootNode().getChild("model").setLocalRotation(rotation);
        walkDirection.addLocal(camLeft);
          changed = true;
        }
        if(right){
                    
        Quaternion rotation = simpleApp.getRootNode().getChild("model").getLocalRotation().clone();
        Vector3f direction = camLeft.negate();//.subtractLocal(simpleApp.getRootNode().getChild("model").getWorldTranslation())
        targetQua.lookAt(direction, Vector3f.UNIT_Y);
        rotation.nlerp(targetQua, 0.1f);
        //viewDirection.set(rotation.mult(Vector3f.UNIT_Z));
        simpleApp.getRootNode().getChild("model").setLocalRotation(rotation);
        walkDirection.addLocal(camLeft.negate());
             changed = true;
        }else if(changed){
            //viewDirection.y = 0.0f;// 将行走速度的方向限制在水平面上。
            //viewDirection.normalizeLocal();// 向量标准化
//            viewDirection.mult(500f);// 改变速率
            //walkDirection.y = 0;// 将行走速度的方向限制在水平面上。
//            walkDirection.normalizeLocal();// 向量标准化
//            walkDirection.multLocal(0.53f);// 改变速率
        }

     player.setWalkDirection(walkDirection);
     
    }


    @Override
    public void onAction(String name, boolean bln, float tpf) {
   
         if(FORWARD == name){
                if(bln){
                    System.err.println("按下W");
                    up=true;
                }else if(!bln){
                    System.err.println("弹起W");
                    up=false;
                } 
         }
         if(BACKWARD == name){
                if(bln){
                    System.err.println("按下S");
                    down=true;
                }else if(!bln){
                    System.err.println("弹起S");
                    down=false;
                }      
          }  
         if(LEFT == name){
                if(bln){
                    System.err.println("按下A");
                    left = true;
                }else if(!bln){
                    System.err.println("弹起A");
                    left = false;
                }     
         } 
         if(RIGHT == name){
                if(bln){
                    System.err.println("按下D");
                    right = true;
                }else if(!bln){
                    System.err.println("弹起D");
                    right = false;
                }     
         }
         if(V == name){
                if(bln){
                    System.err.println("按下V");
                    
                }else if(!bln){
                    System.err.println("弹起V");
                    
                }    
            }
         if(DEBUG == name){
                if(bln){
                    System.err.println("按下F1");
                    boolean debugEnabled = bulletAppState.isDebugEnabled();
                    bulletAppState.setDebugEnabled(!debugEnabled);
                    
                }else if(!bln){
                    System.err.println("弹起F1");
                    
                } 
         }
          
        }
    
          
       private void initKeys() {
        inputManager.addMapping(BUTTON_LEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(DEBUG, new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping(DEBUG1, new KeyTrigger(KeyInput.KEY_F2));
        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(V, new KeyTrigger(KeyInput.KEY_V));
        inputManager.addListener(this, BUTTON_LEFT, DEBUG, DEBUG1, LEFT, RIGHT, FORWARD, BACKWARD, JUMP, V);
    }
    
    }
    
  

