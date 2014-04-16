package cz.muni.fi.motracksmoother;

import cz.muni.fi.motracksmoother.misc.JointType;
import cz.muni.fi.motracksmoother.gui.MoTrackSmoother;
import cz.muni.fi.motracksmoother.misc.InvalidFileSyntaxException;
import cz.muni.fi.motracksmoother.misc.Misc;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of SkeletonManager interface.
 * 
 * @author Tomáš
 * @version 1.0
 * @since 1.0
 */
public class SkeletonManagerImpl implements SkeletonManager {

    private Misc misc = new Misc();

    public void getPositionsFromFile(Skeleton skeleton, File file) throws InvalidFileSyntaxException {

        String filename = file.getName();
        String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        extension = extension.toLowerCase();

        if (extension.equals("json")) {
            getPositionsFromJSON(skeleton, file);
        } else if (extension.equals("xml")) {
            getPositionsFromXML(skeleton, file);
        } else if (extension.equals("csv")) {
            getPositionsFromCSV(skeleton, file);
        }

    }

    public void getPositionsFromJSON(Skeleton skeleton, File file) throws InvalidFileSyntaxException {

        TreeMap<JointType, ArrayList<Float>> xPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();
        TreeMap<JointType, ArrayList<Float>> yPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();
        TreeMap<JointType, ArrayList<Float>> zPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();

        Integer framesTemp = new Integer(0);

        skeleton.setName(file.getName());

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(file));

            JSONObject jsonObject = (JSONObject) obj;

            if (!jsonObject.containsKey("all")) {
                throw new InvalidFileSyntaxException("Key \"all\" is missing.");
            }

            JSONArray joints = (JSONArray) jsonObject.get("all");
            for (int i = 0; i < joints.size(); i++) {
                JSONObject childJSONObject = (JSONObject) joints.get(i);
                if (!childJSONObject.containsKey("jointType") || !childJSONObject.containsKey("position")) {
                    throw new InvalidFileSyntaxException("At least one of keys (jointType, position) is missing.");
                }

                String name = (String) childJSONObject.get("jointType");
                JointType jointType = JointType.valueOf(name.toUpperCase());

                JSONArray positions = (JSONArray) childJSONObject.get("position");
                framesTemp = (Integer) positions.size();
                ArrayList<Float> xPositionsTemp = new ArrayList<Float>();
                ArrayList<Float> yPositionsTemp = new ArrayList<Float>();
                ArrayList<Float> zPositionsTemp = new ArrayList<Float>();
                for (int j = 0; j < positions.size(); j++) {
                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number;

                    JSONObject positionChildJSONObject = (JSONObject) positions.get(j);
                    if (!positionChildJSONObject.containsKey("frame") || !positionChildJSONObject.containsKey("x") || !positionChildJSONObject.containsKey("y") || !positionChildJSONObject.containsKey("z")) {
                        throw new InvalidFileSyntaxException("At least one of keys (frame, x, y, z) is missing.");
                    }

                    String frameString = (String) positionChildJSONObject.get("frame");
                    Integer frame = Integer.parseInt(frameString);

                    String xString = (String) positionChildJSONObject.get("x");
                    number = format.parse(xString);
                    Float x = number.floatValue();
                    xPositionsTemp.add(x);

                    String yString = (String) positionChildJSONObject.get("y");
                    number = format.parse(yString);
                    Float y = number.floatValue();
                    yPositionsTemp.add(y);

                    String zString = (String) positionChildJSONObject.get("z");
                    number = format.parse(zString);
                    Float z = number.floatValue();
                    zPositionsTemp.add(z);

                }

                xPositionsMapTemp.put(jointType, (ArrayList<Float>) xPositionsTemp.clone());
                yPositionsMapTemp.put(jointType, (ArrayList<Float>) yPositionsTemp.clone());
                zPositionsMapTemp.put(jointType, (ArrayList<Float>) zPositionsTemp.clone());

                xPositionsTemp.clear();
                yPositionsTemp.clear();
                zPositionsTemp.clear();

            }

        } catch (java.text.ParseException ex) {
            Logger.getLogger(MoTrackSmoother.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MoTrackSmoother.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MoTrackSmoother.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(MoTrackSmoother.class.getName()).log(Level.SEVERE, null, ex);
        }

        skeleton.setFrames(framesTemp);
        skeleton.setxPositions((TreeMap<JointType, ArrayList<Float>>) xPositionsMapTemp.clone());
        skeleton.setyPositions((TreeMap<JointType, ArrayList<Float>>) yPositionsMapTemp.clone());
        skeleton.setzPositions((TreeMap<JointType, ArrayList<Float>>) zPositionsMapTemp.clone());

        xPositionsMapTemp.clear();
        yPositionsMapTemp.clear();
        zPositionsMapTemp.clear();

    }

    public void getPositionsFromXML(Skeleton skeleton, File file) throws InvalidFileSyntaxException {

        TreeMap<JointType, ArrayList<Float>> xPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();
        TreeMap<JointType, ArrayList<Float>> yPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();
        TreeMap<JointType, ArrayList<Float>> zPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();

        Integer framesTemp = new Integer(0);

        skeleton.setName(file.getName());

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            // Normalization, stackoverflow.com/questions/13786607
            document.getDocumentElement().normalize();

            // joints
            NodeList jointList = document.getElementsByTagName("joint");
            if (jointList == null) {
                throw new InvalidFileSyntaxException("Key \"joint\" is missing.");
            }

            for (int i = 0; i < jointList.getLength(); i++) {

                JointType jointType = null;
                ArrayList<Float> xPositionsTemp = new ArrayList<Float>();
                ArrayList<Float> yPositionsTemp = new ArrayList<Float>();
                ArrayList<Float> zPositionsTemp = new ArrayList<Float>();

                Node jointNode = jointList.item(i);

                if (jointNode.getNodeType() == Node.ELEMENT_NODE) {

                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number;

                    Element eElement = (Element) jointNode;

                    NodeList type = eElement.getElementsByTagName("type");
                    if (type == null) {
                        throw new InvalidFileSyntaxException("Key \"type\" is missing.");
                    }
                    Element line = (Element) type.item(0);
                    // type of joint
                    jointType = JointType.valueOf(line.getFirstChild().getTextContent().toUpperCase());

                    // x-axis
                    NodeList xs = eElement.getElementsByTagName("x");
                    if (xs == null) {
                        throw new InvalidFileSyntaxException("Key \"x\" is missing.");
                    }
                    for (int j = 0; j < xs.getLength(); j++) {
                        line = (Element) xs.item(j);
                        number = format.parse(line.getFirstChild().getTextContent());
                        Float value = number.floatValue();
                        xPositionsTemp.add(value);
                    }

                    // y-axis
                    NodeList ys = eElement.getElementsByTagName("y");
                    if (ys == null) {
                        throw new InvalidFileSyntaxException("Key \"y\" is missing.");
                    }
                    for (int j = 0; j < ys.getLength(); j++) {
                        line = (Element) ys.item(j);
                        number = format.parse(line.getFirstChild().getTextContent());
                        Float value = number.floatValue();
                        yPositionsTemp.add(value);
                    }
                    // z-axis
                    NodeList zs = eElement.getElementsByTagName("z");
                    if (zs == null) {
                        throw new InvalidFileSyntaxException("Key \"z\" is missing.");
                    }
                    for (int j = 0; j < zs.getLength(); j++) {
                        line = (Element) zs.item(j);
                        number = format.parse(line.getFirstChild().getTextContent());
                        Float value = number.floatValue();
                        zPositionsTemp.add(value);
                    }

                    if (i + 1 == jointList.getLength()) {
                        framesTemp = zs.getLength();
                    }

                }

                xPositionsMapTemp.put(jointType, xPositionsTemp);
                yPositionsMapTemp.put(jointType, yPositionsTemp);
                zPositionsMapTemp.put(jointType, zPositionsTemp);

            }

        } catch (SAXException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidFileSyntaxException(ex.getMessage());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.text.ParseException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        skeleton.setFrames(framesTemp);
        skeleton.setxPositions(xPositionsMapTemp);
        skeleton.setyPositions(yPositionsMapTemp);
        skeleton.setzPositions(zPositionsMapTemp);

    }

    public void getPositionsFromCSV(Skeleton skeleton, File file) throws InvalidFileSyntaxException {

        TreeMap<JointType, ArrayList<Float>> xPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();
        TreeMap<JointType, ArrayList<Float>> yPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();
        TreeMap<JointType, ArrayList<Float>> zPositionsMapTemp = new TreeMap<JointType, ArrayList<Float>>();

        Integer framesTemp = 0;

        skeleton.setName(file.getName());

        BufferedReader br = null;
        String line = "";
        String splitBy = "\",\"";
        int i = 0;

        // initialize temporary maps
        for (JointType jointType : JointType.values()) {
            xPositionsMapTemp.put(jointType, new ArrayList<Float>());
            yPositionsMapTemp.put(jointType, new ArrayList<Float>());
            zPositionsMapTemp.put(jointType, new ArrayList<Float>());
        }

        try {

            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {

                if (!line.startsWith("Head")) {

                    // counts number of frames
                    framesTemp++;

                    // removes first and last double quotes in the line
                    if (!line.isEmpty()) {
                        line = line.substring(1, line.length() - 1);
                    }

                    String[] positions = line.split(splitBy);

                    i = 0;
                    for (JointType jointType : JointType.values()) {

                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        Number number;

                        for (int j = 0; j < 3; j++) {

                            number = format.parse(positions[i]);
                            Float value = number.floatValue();
                            switch (j) {
                                case 0:
                                    xPositionsMapTemp.get(jointType).add(value);
                                    break;
                                case 1:
                                    yPositionsMapTemp.get(jointType).add(value);
                                    break;
                                case 2:
                                    zPositionsMapTemp.get(jointType).add(value);
                                    break;
                            }

                            i++;

                        }

                    }

                }

            }

        } catch (java.text.ParseException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (br != null) {
                try {
                    if (i != JointType.values().length * 3) {
                        throw new InvalidFileSyntaxException("There are some missing joint type(s).");
                    }
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        skeleton.setFrames(framesTemp);
        skeleton.setxPositions(xPositionsMapTemp);
        skeleton.setyPositions(yPositionsMapTemp);
        skeleton.setzPositions(zPositionsMapTemp);

    }

    public Integer getNumberOfFrames(Skeleton skeleton) {

        return skeleton.getxPositions().get(JointType.HEAD).size();

    }

    public void createOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end) {

        String filename = file.getName();
        String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        extension = extension.toLowerCase();

        if (extension.equals("json")) {
            createJSONOutput(skeleton, file, filteredMotion, start, end);
        } else if (extension.equals("xml")) {
            createXMLOutput(skeleton, file, filteredMotion, start, end);
        } else if (extension.equals("csv")) {
            createCSVOutput(skeleton, file, filteredMotion, start, end);
        }

    }

    public void createJSONOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end) {

        String jointTypeWithCamelCase = null;
        int i = 0;

        String jsonContent = "{\"all\":[";

        for (JointType jointType : JointType.values()) {

            jointTypeWithCamelCase = misc.jointTypeToCamelCase(jointType);
            jsonContent += "{\"jointType\":\"" + jointTypeWithCamelCase + "\",\"position\":[";

            int k = 1;
            for (int j = start; j < end; j++) {

                jsonContent += "{\"frame\":\"" + k + "\","
                        + "\"x\":\"" + ((filteredMotion)
                        ? skeleton.getxPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getxPositions().get(jointType).get(j).toString().replace(".", ",")) + "\","
                        + "\"y\":\"" + ((filteredMotion)
                        ? skeleton.getyPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getyPositions().get(jointType).get(j).toString().replace(".", ",")) + "\","
                        + "\"z\":\"" + ((filteredMotion)
                        ? skeleton.getzPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getzPositions().get(jointType).get(j).toString().replace(".", ",")) + "\"}";

                if (j != end - 1) {
                    jsonContent += ",";
                }

                k++;

            }

            jsonContent += "]}";

            if (i != JointType.values().length - 1) {
                jsonContent += ",";
            }
            i++;

        }

        jsonContent += "]}";

        try {
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(jsonContent);
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createXMLOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end) {

        String jointTypeWithCamelCase = null;

        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><joints>";

        for (JointType jointType : JointType.values()) {

            jointTypeWithCamelCase = misc.jointTypeToCamelCase(jointType);
            xmlContent += "<joint><type>" + jointTypeWithCamelCase + "</type><positions>";

            int k = 1;
            for (int j = start; j < end; j++) {
                xmlContent += "<position>"
                        + "<frame>" + k + "</frame>"
                        + "<x>" + ((filteredMotion)
                        ? skeleton.getxPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getxPositions().get(jointType).get(j).toString().replace(".", ",")) + "</x>"
                        + "<y>" + ((filteredMotion)
                        ? skeleton.getyPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getyPositions().get(jointType).get(j).toString().replace(".", ",")) + "</y>"
                        + "<z>" + ((filteredMotion)
                        ? skeleton.getzPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getzPositions().get(jointType).get(j).toString().replace(".", ",")) + "</z>"
                        + "</position>";

                k++;
            }
            xmlContent += "</positions></joint>";
        }
        xmlContent += "</joints>";

        try {
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(xmlContent);
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createCSVOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end) {

        String jointTypeWithCamelCase = null;

        String csvContent = "";

        int k = 0;
        for (JointType jointType : JointType.values()) {

            jointTypeWithCamelCase = misc.jointTypeToCamelCase(jointType);
            for (int i = 0; i < 3; i++) {
                csvContent += jointTypeWithCamelCase;

                if (k != (JointType.values().length - 1) || i != 2) {
                    csvContent += ",";
                } else {
                    csvContent += "\n";
                }
            }

            k++;

        }

        for (int j = start; j < end; j++) {

            k = 0;
            for (JointType jointType : JointType.values()) {

                csvContent += "\"" + ((filteredMotion)
                        ? skeleton.getxPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getxPositions().get(jointType).get(j).toString().replace(".", ",")) + "\","
                        + "\"" + ((filteredMotion)
                        ? skeleton.getyPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getyPositions().get(jointType).get(j).toString().replace(".", ",")) + "\","
                        + "\"" + ((filteredMotion)
                        ? skeleton.getzPositionsCleaned().get(jointType).get(j).toString().replace(".", ",")
                        : skeleton.getzPositions().get(jointType).get(j).toString().replace(".", ",")) + "\"";

                if (k != (JointType.values().length - 1)) {
                    csvContent += ",";
                } else {
                    csvContent += "\n";
                }

                k++;

            }

        }

        try {
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(csvContent);
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(SkeletonManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
