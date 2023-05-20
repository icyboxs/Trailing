/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trailing;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.util.BufferUtils;
import static com.jme3.util.BufferUtils.createFloatBuffer;
import java.nio.FloatBuffer;

/**
 *
 * @author JhonKkk
 */
public class BufferTool {
    public static FloatBuffer createFloatBuffer(Vector2f[]data, int length) {
        if (data == null) {
            return null;
        }
        FloatBuffer buff = BufferUtils.createFloatBuffer(2 * length);
        for(int i = 0;i < length;i++){
            buff.put(data[i].x).put(data[i].y);
        }
        buff.position(0);
        return buff;
    }
    public static FloatBuffer createFloatBuffer(Vector3f[]data, int length) {
        if (data == null) {
            return null;
        }
        FloatBuffer buff = BufferUtils.createFloatBuffer(3 * length);
        for(int i = 0;i < length;i++){
            buff.put(data[i].x).put(data[i].y).put(data[i].z);
        }
        buff.position(0);
        return buff;
    }
}
