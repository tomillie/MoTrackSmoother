package cz.muni.fi.motracksmoother.misc;

import cz.muni.fi.motracksmoother.Skeleton;

/**
 * Miscellaneous methods.
 * 
 * @author Tomas Smetanka
 * @version 1.0
 * @since 1.0
 */
public class Misc {
    
    /**
     * Clear all attributes of given skeleton.
     * 
     * @param skeleton is the entity to be cleared
     * @version 1.0
     * @since 1.0
     */
    public void clearAttributes(Skeleton skeleton) {

        skeleton.setName("");
        skeleton.setFrames(0);
        if (skeleton.getxPositions() != null) {
            skeleton.getxPositions().clear();
        }
        if (skeleton.getyPositions() != null) {
            skeleton.getyPositions().clear();
        }
        if (skeleton.getzPositions() != null) {
            skeleton.getzPositions().clear();
        }
        if (skeleton.getxPositionsCleaned() != null) {
            skeleton.getxPositionsCleaned().clear();
        }
        if (skeleton.getyPositionsCleaned() != null) {
            skeleton.getyPositionsCleaned().clear();
        }
        if (skeleton.getzPositionsCleaned() != null) {
            skeleton.getzPositionsCleaned().clear();
        }

    }
    
    /**
     * Converts JointType enum to CamelCased String.
     * 
     * @param jointType JointType to be converted
     * @return converted JointType
     * @version 1.0
     * @since 1.0
     */
    public String jointTypeToCamelCase(JointType jointType) {
        
         if (jointType == JointType.HEAD) {
            return "Head";
        } else if (jointType == JointType.SHOULDERCENTER) {
            return "ShoulderCenter";
        } else if (jointType == JointType.SHOULDERRIGHT) {
            return "ShoulderRight";
        } else if (jointType == JointType.SHOULDERLEFT) {
            return "ShoulderLeft";
        } else if (jointType == JointType.ELBOWRIGHT) {
            return "ElbowRight";
        } else if (jointType == JointType.ELBOWLEFT) {
            return "ElbowLeft";
        } else if (jointType == JointType.WRISTRIGHT) {
            return "WristRight";
        } else if (jointType == JointType.WRISTLEFT) {
            return "WristLeft";
        } else if (jointType == JointType.HANDRIGHT) {
            return "HandRight";
        } else if (jointType == JointType.HANDLEFT) {
            return "HandLeft";
        } else if (jointType == JointType.SPINE) {
            return "Spine";
        } else if (jointType == JointType.HIPCENTER) {
            return "HipCenter";
        } else if (jointType == JointType.HIPRIGHT) {
            return "HipRight";
        } else if (jointType == JointType.HIPLEFT) {
            return "HipLeft";
        } else if (jointType == JointType.KNEERIGHT) {
            return "KneeRight";
        } else if (jointType == JointType.KNEELEFT) {
            return "KneeLeft";
        } else if (jointType == JointType.ANKLERIGHT) {
            return "AnkleRight";
        } else if (jointType == JointType.ANKLELEFT) {
            return "AnkleLeft";
        } else if (jointType == JointType.FOOTRIGHT) {
            return "FootRight";
        } else if (jointType == JointType.FOOTLEFT) {
            return "FootLeft";
        }
         
         return null;
        
    }
    
}
