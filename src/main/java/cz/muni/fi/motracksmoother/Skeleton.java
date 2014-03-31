package cz.muni.fi.motracksmoother;

import cz.muni.fi.motracksmoother.misc.JointType;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Skeleton entity.
 * 
 * @author Tomas Smetanka
 * @version 1.0
 * @since 1.0
 */
public class Skeleton {

    String name;
    Integer frames;
    TreeMap<JointType, ArrayList<Float>> xPositions;
    TreeMap<JointType, ArrayList<Float>> yPositions;
    TreeMap<JointType, ArrayList<Float>> zPositions;
    TreeMap<JointType, ArrayList<Float>> xPositionsCleaned;
    TreeMap<JointType, ArrayList<Float>> yPositionsCleaned;
    TreeMap<JointType, ArrayList<Float>> zPositionsCleaned;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFrames() {
        return frames;
    }

    public void setFrames(Integer frames) {
        this.frames = frames;
    }

    public TreeMap<JointType, ArrayList<Float>> getxPositions() {
        return xPositions;
    }

    public void setxPositions(TreeMap<JointType, ArrayList<Float>> xPositions) {
        this.xPositions = xPositions;
    }

    public TreeMap<JointType, ArrayList<Float>> getyPositions() {
        return yPositions;
    }

    public void setyPositions(TreeMap<JointType, ArrayList<Float>> yPositions) {
        this.yPositions = yPositions;
    }

    public TreeMap<JointType, ArrayList<Float>> getzPositions() {
        return zPositions;
    }

    public void setzPositions(TreeMap<JointType, ArrayList<Float>> zPositions) {
        this.zPositions = zPositions;
    }

    public TreeMap<JointType, ArrayList<Float>> getxPositionsCleaned() {
        return xPositionsCleaned;
    }

    public void setxPositionsCleaned(TreeMap<JointType, ArrayList<Float>> xPositionsCleaned) {
        this.xPositionsCleaned = xPositionsCleaned;
    }

    public TreeMap<JointType, ArrayList<Float>> getyPositionsCleaned() {
        return yPositionsCleaned;
    }

    public void setyPositionsCleaned(TreeMap<JointType, ArrayList<Float>> yPositionsCleaned) {
        this.yPositionsCleaned = yPositionsCleaned;
    }

    public TreeMap<JointType, ArrayList<Float>> getzPositionsCleaned() {
        return zPositionsCleaned;
    }

    public void setzPositionsCleaned(TreeMap<JointType, ArrayList<Float>> zPositionsCleaned) {
        this.zPositionsCleaned = zPositionsCleaned;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.frames != null ? this.frames.hashCode() : 0);
        hash = 89 * hash + (this.xPositions != null ? this.xPositions.hashCode() : 0);
        hash = 89 * hash + (this.yPositions != null ? this.yPositions.hashCode() : 0);
        hash = 89 * hash + (this.zPositions != null ? this.zPositions.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Skeleton other = (Skeleton) obj;
        if (this.frames != other.frames && (this.frames == null || !this.frames.equals(other.frames))) {
            return false;
        }
        if (this.xPositions != other.xPositions && (this.xPositions == null || !this.xPositions.equals(other.xPositions))) {
            return false;
        }
        if (this.yPositions != other.yPositions && (this.yPositions == null || !this.yPositions.equals(other.yPositions))) {
            return false;
        }
        if (this.zPositions != other.zPositions && (this.zPositions == null || !this.zPositions.equals(other.zPositions))) {
            return false;
        }
        return true;
    }  
    
}
