/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.CameraInput;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.shadow.ShadowUtil;
import java.util.Objects;
import java.util.logging.Logger;
/**
 *
 * @author Icyboxs
 */
public class TerrainChaseCamera extends ChaseCamera {
private static Logger log =Logger.getLogger(TerrainChaseCamera.class.toString());

private float maxDistances=10f;
private Quaternion quaternion= new Quaternion();
private Vector3f afterVector= new Vector3f();
protected  boolean DebugFrustum= false;
protected boolean CameraCollisionDetection= false;
private SimpleApplication simpleApp;
private CollisionResults results = new CollisionResults();
private String ChildName;
private boolean CursorVisible =true;
  // 射线
private Ray ray=new Ray();

    public TerrainChaseCamera(Camera cam, Spatial target, InputManager inputManager,SimpleApplication simpleApp) {
        super(cam, target, inputManager);
        this.simpleApp=simpleApp;
    }
    
   public TerrainChaseCamera(Camera cam, Spatial target, InputManager inputManager) {
        super(cam, target, inputManager);
    }


    /**
     * 使用射线检测，判断离摄像机最近的点。
     */
    public Ray RayCollision() {
         ray.setOrigin(targetLocation);
         ray.setDirection(cam.getLocation().subtract(targetLocation).normalize());
        // System.err.println(ray.getDirection());
    return ray;
    }
    
    public Camera getcam(){
    
        
        return cam;
    
    }
    
     /**
     * Set whether the mouse cursor should be visible or not.
     *
     * @param visible whether the mouse cursor should be visible or not.
     */
    public boolean getCursorVisible(){
    return this.CursorVisible;
}
public boolean setCursorVisible(boolean CursorVisible){
    return this.CursorVisible=CursorVisible;
}
    
     /**
     * 是否开启DEBUG相机
     *
     * 
     */ 
public boolean getDebugFrustum(){
    return this.DebugFrustum;
}
public boolean setDebugFrustum(boolean DebugFrustum){
    if(DebugFrustum){
    createCameraFrustum();
    }
    
    return this.DebugFrustum=DebugFrustum;
}
    
     /**
     * 相机地形碰撞状态
     *
     * 
     */ 
public boolean getCameraCollisionDetection(){
    return this.CameraCollisionDetection;
}
     /**
     * 开启相机地形碰撞 设置开启和碰撞地形名称
     */ 
public boolean setCameraCollisionDetection(boolean CameraCollisionDetection,String ChildName){
    if(CameraCollisionDetection){
    this.ChildName=ChildName;
    return this.CameraCollisionDetection=CameraCollisionDetection;
    }else{
    this.ChildName=ChildName;
    return this.CameraCollisionDetection=CameraCollisionDetection;
   
    }
    
}


    /**
     * update the camera control, should only be used internally
     *
     * @param tpf time per frame (in seconds)
     */
    @Override
    public void update(float tpf) {
        updateCamera(tpf);
        updateCameraExtension(tpf);
    }
/**
 *追逐相机扩展功能
 *@param tpf time per frame (in seconds)
 **/
     public void updateCameraExtension(float tpf) {
       inputManager.setCursorVisible(CursorVisible);
       if(DebugFrustum){
       simpleApp.getRootNode().getChild("Viewing.Frustum").setLocalTranslation(cam.getLocation());
       simpleApp.getRootNode().getChild("Viewing.Frustum").setLocalRotation(cam.getRotation());
       }
       if(CameraCollisionDetection){
      //射线检测
      if(ChildName == null){
       
       }else{
        simpleApp.getRootNode().getChild(ChildName).collideWith(RayCollision(), results);
        CameraCollision(results);
      }

       }
     }
    
    
public  void createCameraFrustum() {
	//this.DebugFrustum=true;
       Vector3f[]  points = new Vector3f[8];
	for (int i = 0; i < 8; i++) {
		points[i] = new Vector3f();
	}
	
	Camera frustumCam = cam.clone();
        frustumCam.setLocation(new Vector3f(0, 0, 0));
        frustumCam.lookAt(Vector3f.UNIT_Z, Vector3f.ZERO);
	ShadowUtil.updateFrustumPoints2(frustumCam, points);
	Mesh mesh = WireFrustum.makeFrustum(points);
	
	Geometry frustumGeo = new Geometry("Viewing.Frustum", mesh);
	Material mat = new Material( simpleApp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
	mat.setColor("Color", ColorRGBA.Red);
	frustumGeo.setMaterial(mat);
	frustumGeo.setCullHint(Spatial.CullHint.Never);
	frustumGeo.setShadowMode(RenderQueue.ShadowMode.Off);
      
        simpleApp.getRootNode().attachChild(frustumGeo);
}




    /**
     * 控制相机碰撞地形缩放
     * 
     * @param results
     */
    private void CameraCollision(CollisionResults results) {

//        System.err.println("碰撞结果：" + results.size());
//        System.err.println("射线：" + ray);

        /**
         * 判断检测结果
         */
        if (results.size() > 0) {
            // 从近到远，打印出射线途径的所有交点。
//            for (int i = 0; i < results.size(); i++) {
//                CollisionResult result = results.getCollision(i);
//
//                float dist = result.getDistance();
//                Vector3f point = result.getContactPoint();
//                Vector3f normal = result.getContactNormal();
//                Geometry geom = result.getGeometry();
//                System.err.printf("序号：%d, 距离：%.2f, 物体名称：%s, 交点：%s, 交点法线：%s\n", i, dist, geom.getName(), point, normal);
//            }

            // 离射线原点最近的交点
            Vector3f closest = results.getClosestCollision().getContactPoint();
            // 离射线原点最远的交点
            Vector3f farthest = results.getFarthestCollision().getContactPoint();
           
            // 离射线原点最近的距离
            float ClosestDist = results.getClosestCollision().getDistance();
            float FarthestDist = results.getFarthestCollision().getDistance();

                if(ClosestDist<=maxDistances){
                    
                    targetDistance=ClosestDist;
//                float incrimentDistance =  (-distance+dist1); // might need subtracted in opposite order 
//               
//                 zoomCamera(incrimentDistance);

                }else{
                   
               }
          //System.err.printf("最近点：%s, 最远点：%s\n", closest, farthest);
         //    System.err.printf("离射线原点最近的距离：%s\n", dist);
             
           results.clear();
        }else{
           
//setMinDistance(10f);
//setMaxDistance(10f);
           // zoomCamera(10f);
           // zoomCamera(10);getMaxDistance()
           targetDistance=maxDistances;
         //  System.err.printf("getDistanceToTarget：%s\n", getDistanceToTarget());
           
        }
//      System.err.printf("maxDistances：%s\n", maxDistances);
//      System.err.printf("minDistance：%s\n", minDistance);
//       System.err.println(distance);
    }
    
        @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals(CameraInput.CHASECAM_MOVELEFT)) {
            rotateCamera(-value);
        } else if (name.equals(CameraInput.CHASECAM_MOVERIGHT)) {
            rotateCamera(value);
        } else if (name.equals(CameraInput.CHASECAM_UP)) {
            vRotateCamera(value);
        } else if (name.equals(CameraInput.CHASECAM_DOWN)) {
            vRotateCamera(-value);
        } else if (name.equals(CameraInput.CHASECAM_ZOOMIN)) {
            zoomCamera(-value);
            maxDistances += -value * zoomSensitivity;
            if(maxDistances<minDistance){
            maxDistances=minDistance;
            }
            if (zoomin == false) {
                distanceLerpFactor = 0;
            }
            zoomin = true;
        } else if (name.equals(CameraInput.CHASECAM_ZOOMOUT)) {
            maxDistances += value * zoomSensitivity;
            if(maxDistances>maxDistance){
            maxDistances=maxDistance;
            }
            zoomCamera(+value);
            if (zoomin == true) {
                distanceLerpFactor = 0;
            }
            zoomin = false;
        }
    }
    
}
