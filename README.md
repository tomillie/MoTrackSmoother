MoTrackSmoother
===============

MoTrackSmoother is a tool mainly for smoothing (filtering) recorded motion using Microsoft Kinect. 

At first, user imports a file of skeleton joints recorded in <a href="https://github.com/tomillie/MoTrackRecorder">MoTrackRecorder</a>, then it is possible to play the imported motion, smooth it and suppress estimated errors. For smoothing, the aplication uses <strong>Kalman filter</strong>. Also, it is possible to cut the motion and create a desired submotion. The output is exported JSON, XML or CSV file.
