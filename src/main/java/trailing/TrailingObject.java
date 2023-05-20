/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trailing;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author JhonKkk
 */
public class TrailingObject {

    /**
     * 返回拖尾目标
     * @return the mTagSpatial
     */
    public Spatial getTagSpatial() {
        return mTagSpatial;
    }

    /**
     * 设置拖尾目标
     * @param mTagSpatial the mTagSpatial to set
     */
    public void setTagSpatial(Spatial mTagSpatial) {
        this.mTagSpatial = mTagSpatial;
    }
    //采样决策时间毫秒(默认采样时间是10毫秒,因为这里没有进行采样轨迹插值计算,而直接减低采样时间来确保轨迹面的光滑)
    public static final long S_SAMPLING_TIME = 10;
    //这里为了减少除法运算直接使用倒数乘法
    public static final float S_DIVIDER_TIME = 1.0f / (S_SAMPLING_TIME * 1000.0f);
    //是否拖影
    private boolean mTrailing = false;
    //最大拖影顶点缓存数量
    protected int mMaxBufferTrailingPartCount = 100;
    //拖影产生最小速度
    protected float mVmin = 0.00001f;
    //拖影回收速度
    private float mVminRec = 0.1f;
    //纹理宽度
    protected float mLen = 80;
    //纹理宽度倒数,用于加快计算
    protected float mDividerLen = 1.0f / mLen;
    //旋转角度变量
    private int mAngle;
    //采样轨迹时间(毫秒)
    protected int mSamplingDecisionTime = 0;
    //轨迹列表
    protected List<TrailingVecPair> mTrajectory = new ArrayList<>();
    //武器上下顶点（拖影跟踪点对）
    public Vector4f mVecTop = new Vector4f(), mVecBtm = new Vector4f();
    //拖尾跟踪对象
    private Spatial mTagSpatial;
    //跟踪变换矩阵
    public Matrix4f mTrailingMatrix4f = new Matrix4f();
    //拖尾轨迹面顶点数量
    protected int mTrailingCount;
    public float sp = 0;
    //顶点属性
    public Vector3f[] mBufferPositions;
    //纹理属性
    public Vector2f[] mBufferUvs;
    //拖影网格
    public Mesh mTrailingMesh;
    //拖影geometry
    public Geometry mTrailingGeometry;
    /**
     * 拖影顶点对
     */
    public static class TrailingVecPair{
        //表示该拖影点对是否首次生成(在拖影产生阶段,而不是跟随阶段,只需要对每个拖影点对计算一次mL)
        public boolean mCreate = true;
        //0表示top顶点,1表示btm顶点
        public Vector3f[] mVecPair;
        //0标识top顶点纹理;1表示btm顶点纹理
        public Vector2f[] mUvs;
        //表示到拖影原点的距离(以top顶点计算距离)
        public float mL;
        //表示到武器原点的距离(以top顶点计算距离)
        public float mLr;

        public TrailingVecPair(){
            mVecPair = new Vector3f[]{new Vector3f(), new Vector3f()};
            mUvs = new Vector2f[]{new Vector2f(), new Vector2f()};
        }

        @Override
        public boolean equals(Object obj) {
            TrailingVecPair t = (TrailingVecPair)obj;
            return t.mVecPair[0].equals(mVecPair[0]);
        }

        /**
         * 计算拖影段距离
         */
        public final void scaleLAndLr(){

        }

        /**
         * 计算与指定对的距离(以top计算)
         * @param trailingVecPair
         * @return
         */
        public final float scaleAtLength(TrailingVecPair trailingVecPair){
//            float tx = mVecPair[0] - trailingVecPair.mVecPair[0];
//            float ty = mVecPair[1] - trailingVecPair.mVecPair[1];
//            float tz = mVecPair[2] - trailingVecPair.mVecPair[2];
//            return (float) Math.sqrt(tx * tx + ty * ty + tz * tz);
            return mVecPair[0].distance(trailingVecPair.mVecPair[0]);
        }
    }
    public void setup(){
        mTrajectory = new ArrayList<>();
        mBufferPositions = new Vector3f[mMaxBufferTrailingPartCount];
        mBufferUvs = new Vector2f[mMaxBufferTrailingPartCount];
        mTrailingMesh = new Mesh();
        mTrailingMesh.setDynamic();
        mTrailingMesh.setMode(Mesh.Mode.TriangleStrip);
        mTrailingGeometry = new Geometry("TrailingGeo", mTrailingMesh);
    }
    /**
     * 设置是否拖影
     * @param trailing
     */
    public void setTrailing(boolean trailing) {
        mTrailing = trailing;
    }

    /**
     * 是否拖影
     * @return
     */
    public boolean isTrailing() {
        return mTrailing;
    }
    /**
     * 设置拖影点对
     * @param vecTop
     * @param vecBtm
     */
    public final void setTrailingVec(Vector3f vecTop, Vector3f vecBtm){
        mVecTop.set(vecTop.x, vecTop.y, vecTop.z, 1.0f);
        mVecBtm.set(vecBtm.x, vecBtm.y, vecBtm.z, 1.0f);
    }
    /**
     * 采样轨迹
     */
    public final void sampling(long exTime){
        TrailingVecPair vecPair = new TrailingVecPair();
        mTrailingMatrix4f = mTagSpatial.getLocalToWorldMatrix(mTrailingMatrix4f);
        //这里执行了模型变换
        Vector4f v1 = mTrailingMatrix4f.mult(mVecTop);
        Vector4f v2 = mTrailingMatrix4f.mult(mVecBtm);
        vecPair.mVecPair[0].set(v1.x, v1.y, v1.z);
        vecPair.mVecPair[1].set(v2.x, v2.y, v2.z);
        if(mTrajectory.isEmpty()){
            mTrajectory.add(vecPair);
        }
        else{
            //v表示当前计算出当前轨迹点对的real time运动速度
            //vMin表示最小产生拖尾的速度,只有超过这个速度,才能产生拖尾
            //当L大于Len时,拖影跟随,从武器原点产生拖影
            //当L小于Len时,生成拖影,从拖影原点产生拖影
            //比较当前轨迹点对与前一轨迹点对的速度变化,如果超过一定速度则添加进去
            //否则,如果速度小于某个值,则删除最前面一个轨迹点对,直到空为止
            //或者,当超过指定长度时,则删除最前面的轨迹点对,直到在范围内为止

            //前一个拖影点对
            TrailingVecPair proPair = mTrajectory.get(mTrajectory.size() - 1);
            //real time阶段,实时计算每个拖影点对的L和Lr
            vecPair.mL = vecPair.scaleAtLength(proPair);
            //计算速度v
            float v = vecPair.mL * S_DIVIDER_TIME;
            vecPair.mL += proPair.mL;
            if(v > mVmin){//
                mTrajectory.add(vecPair);
            }
            else{
                v = 0;
            }
            //存在拖影
            if(mTrajectory.size() >= 2){
                mTrailing = true;
                Iterator<TrailingVecPair> it = null;
                TrailingVecPair tvp = null;
                float s = 0;
                int dataOffset = 0;
//                GLLog.loge("vecPair.mL:" + vecPair.mL + ";mLen:" + mLen);
                if(vecPair.mL > mLen){
//                    GLLog.loge("上面" + mTrajectory.toString());
                    //拖影跟随
                    //从武器原点计算拖影纹理
                    for(int i = mTrajectory.size() - 1,k = 0;i >= 0;i--,k++){
                        tvp = mTrajectory.get(i);
                        //计算当前点到武器原点的距离lr(反过来取)
                        //如果当前仍在运动,则实时计算每个tvp到武器原点的距离
                        if(v > 0){
//                            if(i == 0){
//                                tvp.mUvs[0] = -1;
//                                continue;
//                            }
                            if(k == 0){
                                tvp.mLr = 0;
                            }
                            else{
                                proPair = mTrajectory.get(i + 1);
                                tvp.mLr = tvp.scaleAtLength(proPair) + proPair.mLr;
                            }
//                            tvp.mLr = vecPair.scaleAtLength(tvp);
//                            if(k > 0){
//                                tvp.mLr += mTrajectory.get(i + 1).mLr;
//                            }
                            s = 1.0f - tvp.mLr * mDividerLen;
                            tvp.mUvs[0].x = s;
                            tvp.mUvs[0].y = 0;
                            tvp.mUvs[1].x = s;
                            tvp.mUvs[1].y = 1;
//                            GLLog.loge("仍在运动,第" + k + "个,tvp.s:" + tvp.mUvs[0] + ",mLr:" + tvp.mLr);
                        }
                        if(tvp.mUvs[0].x > 1 || tvp.mUvs[0].x < 0){
                            continue;
                        }
                        if(dataOffset >= mMaxBufferTrailingPartCount - 1)continue;
                        //否则添加到渲染集合中
                        mBufferPositions[dataOffset] = tvp.mVecPair[0];
                        mBufferPositions[dataOffset + 1] = tvp.mVecPair[1];
                        mBufferUvs[dataOffset] = tvp.mUvs[0];
                        mBufferUvs[dataOffset + 1] = tvp.mUvs[1];
                        dataOffset += 2;
//                        mBufferFloat[dataOffset] = tvp.mVecPair[0];
//                        mBufferFloat[dataOffset + 1] = tvp.mVecPair[1];
//                        mBufferFloat[dataOffset + 2] = tvp.mVecPair[2];
//                        mBufferFloat[dataOffset + 3] = tvp.mUvs[0];
//                        mBufferFloat[dataOffset + 4] = tvp.mUvs[1];
//                        dataOffset += 5;
//                        mBufferFloat[dataOffset] = tvp.mVecPair[4];
//                        mBufferFloat[dataOffset + 1] = tvp.mVecPair[5];
//                        mBufferFloat[dataOffset + 2] = tvp.mVecPair[6];
//                        mBufferFloat[dataOffset + 3] = tvp.mUvs[2];
//                        mBufferFloat[dataOffset + 4] = tvp.mUvs[3];
//                        dataOffset += 5;
                    }
                }
                else{
//                    GLLog.loge("下面");
                    //产生拖影
                    //从拖影原点计算拖影纹理
                    int l = mTrajectory.size();
                    for(int i = 0;i < l;i++){
                        tvp = mTrajectory.get(i);
                        if(tvp.mCreate){
                            tvp.mCreate = false;
                            s = tvp.mL * mDividerLen;
                            tvp.mUvs[0].x = s;
                            tvp.mUvs[0].y = 0;
                            tvp.mUvs[1].x = s;
                            tvp.mUvs[1].y = 1;
                        }
                        if(tvp.mUvs[0].x < 0 || tvp.mUvs[0].x > 1)continue;
                        //添加到渲染集合中
                        //否则添加到渲染集合中
                        if(dataOffset >= mMaxBufferTrailingPartCount - 1)continue;
                        mBufferPositions[dataOffset] = tvp.mVecPair[0];
                        mBufferPositions[dataOffset + 1] = tvp.mVecPair[1];
                        mBufferUvs[dataOffset] = tvp.mUvs[0];
                        mBufferUvs[dataOffset + 1] = tvp.mUvs[1];
                        dataOffset += 2;
//                        mBufferFloat[dataOffset] = tvp.mVecPair[0];
//                        mBufferFloat[dataOffset + 1] = tvp.mVecPair[1];
//                        mBufferFloat[dataOffset + 2] = tvp.mVecPair[2];
//                        mBufferFloat[dataOffset + 3] = tvp.mUvs[0];
//                        mBufferFloat[dataOffset + 4] = tvp.mUvs[1];
//                        dataOffset += 5;
//                        mBufferFloat[dataOffset] = tvp.mVecPair[4];
//                        mBufferFloat[dataOffset + 1] = tvp.mVecPair[5];
//                        mBufferFloat[dataOffset + 2] = tvp.mVecPair[6];
//                        mBufferFloat[dataOffset + 3] = tvp.mUvs[2];
//                        mBufferFloat[dataOffset + 4] = tvp.mUvs[3];
//                        dataOffset += 5;
                    }
                }
                //顶点数量和fb
                mTrailingCount = dataOffset;
                mTrailingMesh.setBuffer(VertexBuffer.Type.Position, 3, BufferTool.createFloatBuffer(mBufferPositions, mTrailingCount));
                mTrailingMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferTool.createFloatBuffer(mBufferUvs, mTrailingCount));
                mTrailingMesh.updateBound();
                mTrailingGeometry.updateModelBound();
//                GLLog.loge("mTrajectory大小:" + mTrajectory.size() + ":mTrailingCount : " + mTrailingCount);
//                GLLog.loge("顶点数据:" + mTrajectory.toString());
//                mTrailingFB.clear();
//                mTrailingFB.put(mBufferFloat).position(0);
                //提交数据到gpu渲染
//                GLES30.glUseProgram(mTrailingShader.mShaderProgram);
//                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTrailingVBO);
//                GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, dataOffset * SizeOf.S_FLOAT_SIZE, mTrailingFB);
//                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
//                GLES30.glUseProgram(0);
                //然后
                //自动回收拖影(将所有拖影点对的top顶点s坐标-lBack,如果小于0或>1,则删除)
                if(v > 0){
                    it = mTrajectory.iterator();
                    while(it.hasNext()){
                        tvp = it.next();
//                    tvp.mUvs[0] -= sp;
//                    s = tvp.mUvs[0] - sp;
                        s = tvp.mUvs[0].x;
                        if(s < 0 || s > 1){
                            it.remove();
                        }
                    }
                }
                else{
                    float lBack = (exTime * mVminRec) * mDividerLen;
                    sp += lBack;
                    it = mTrajectory.iterator();
                    while(it.hasNext()){
                        tvp = it.next();
                        tvp.mUvs[0].x -= lBack;
//                    tvp.mUvs[0] -= sp;
//                    s = tvp.mUvs[0] - sp;
                        s = tvp.mUvs[0].x;
                        if(s < 0 || s > 1){
                            it.remove();
                        }
                    }
                }
            }
        }
    }
}
