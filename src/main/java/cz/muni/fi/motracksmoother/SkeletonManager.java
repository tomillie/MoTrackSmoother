package cz.muni.fi.motracksmoother;

import cz.muni.fi.motracksmoother.misc.InvalidFileSyntaxException;
import java.io.File;

/**
 * Provides reading from input, storing data into entity and writing output.
 * 
 * @author Tomas Smetanka
 * @version 1.0
 * @since 1.0
 */
public interface SkeletonManager {

    /**
     * Processes given file, checks wether it is JSON, XML or CSV file 
     * and calls proper method for additional processing.
     * 
     * @param skeleton is the entity to be filled up with processed data acquired from file
     * @param file is the file to read from
     * @throws InvalidFileSyntaxException if file structure does not suit requirements
     * @version 1.0
     * @since 1.0
     */
    void getPositionsFromFile(Skeleton skeleton, File file) throws InvalidFileSyntaxException;

    /**
     * Processes given JSON file and acquires parsed data.
     * 
     * @param skeleton is the entity to be filled up with parsed data
     * @param file is the file to read from
     * @throws InvalidFileSyntaxException if file structure does not suit requirements
     * @version 1.0
     * @since 1.0
     */
    void getPositionsFromJSON(Skeleton skeleton, File file) throws InvalidFileSyntaxException;

    /**
     * Processes given XML file and acquires parsed data.
     * 
     * @param skeleton is the entity to be filled up with parsed data
     * @param file is the file to read from
     * @throws InvalidFileSyntaxException if file structure does not suit requirements
     * @version 1.0
     * @since 1.0
     */
    void getPositionsFromXML(Skeleton skeleton, File file) throws InvalidFileSyntaxException;

    /**
     * Processes given CSV file and acquires parsed data.
     * 
     * @param skeleton is the entity to be filled up with parsed data
     * @param file is the file to read from
     * @throws InvalidFileSyntaxException if file structure does not suit requirements
     * @version 1.1
     * @since 1.0
     */
    void getPositionsFromCSV(Skeleton skeleton, File file) throws InvalidFileSyntaxException;

    /**
     * Counts number of frames in skeleton entity.
     * 
     * @return number of frames
     * @version 1.0
     * @since 1.0
     */
    Integer getNumberOfFrames(Skeleton skeleton);

    /**
     * Checks what file type is defined and chooses proper method for additional processing.
     * 
     * @param skeleton is the entity to get data from
     * @param file is the file to write to
     * @param filteredMotion is true if filtered motion is going to be exported
     * @param start is first frame to use
     * @param end is last frame to use
     * @version 1.0
     * @since 1.0
     */
    void createOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end);

    /**
     * Creates output and writes the content to the selected JSON file. <br>
     * Structure of output:
     * <pre>
     * {@code
     *  {    
     *  "all":[
     *      {       
     *             "jointType":"Head",
     *             "position":[
     *                {
     *                   "frame":"1",
     *                   "x":"0,8966634",
     *                   "y":"1,025259",
     *                   "z":"2,306435"
     *                }
     *                . . . 
     *  }
     * </pre>
     * 
     * @param skeleton is the entity to get data from
     * @param file is the file to write to
     * @param filteredMotion is true if filtered motion is going to be exported
     * @param start is first frame to use
     * @param end is last frame to use
     * @version 1.0
     * @since 1.0
     */
    void createJSONOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end);

    /**
     * Creates output and writes the content to the selected XML file. <br>
     * Structure of output:
     * <pre>
     * {@code
     *  <joints>
     *      <joint>
     *          <type>Head</type>
     *          <positions>
     *              <position>
     *                  <frame>1</frame>
     *                  <x>0,7269982</x>
     *                  <y>1,04084</y>
     *                  <z>2,300994</z>
     *              </position>   
     *              <position>
     *                  <frame>2</frame>
     *                  <x>0,727215</x>
     *                  <y>1,040738</y>
     *                  <z>2,301057</z>
     *              </position>
     *          </positions>
     *          . . .
     *      </joint>
     *      . . .
     *  </joints>
     * }
     * </pre>
     * 
     * @param skeleton is the entity to get data from
     * @param file is the file to write to
     * @param filteredMotion is true if filtered motion is going to be exported
     * @param start is first frame to use
     * @param end is last frame to use
     * @version 1.0
     * @since 1.0
     */
    void createXMLOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end);

    /**
     * Creates output and writes the content to the selected CSV file. <br>
     * Structure of output:
     * <pre>
     * {@code
     *  Head,Head,Head,ShoulderRight,ShoulderRight,ShoulderRight . . .
     *  "0,7779385","0,9686465","2,232532","0,777445","0,9685029","2,232491" . . .
     *  . . .
     * }
     * </pre>
     * 
     * @param skeleton is the entity to get data from
     * @param file is the file to write to
     * @param filteredMotion is true if filtered motion is going to be exported
     * @param start is first frame to use
     * @param end is last frame to use
     * @version 1.1
     * @since 1.0
     */
    void createCSVOutput(Skeleton skeleton, File file, boolean filteredMotion, int start, int end);
}