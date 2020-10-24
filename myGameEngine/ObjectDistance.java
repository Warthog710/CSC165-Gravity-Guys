package myGameEngine;

import ray.rml.*;

public class ObjectDistance {
    public static float distanceBetweenVectors(Vector3f one, Vector3f two) {
        return (float) Math.sqrt(Math.pow((double) two.x() - (double) one.x(), 2)
                + Math.pow((double) two.y() - (double) one.y(), 2) + Math.pow((double) two.z() - (double) one.z(), 2));
    }
}
