package realwear.com.controlrecyclerview.headtracker;
/*------------------- COPYRIGHT AND TRADEMARK INFORMATION -------------------+
  |
  |    WearNext Development Software, Source Code and Object Code
  |    (c) 2015, 2016 WearNext, Inc. All rights reserved.
  |
  |    Contact info@wearnext.com for further information about the use of
  |    this code.
  |
  +--------------------------------------------------------------------------*/


/*----------------------- SOURCE MODULE INFORMATION -------------------------+
 |
 | Source Name:  Headtracker Listener
 |
 | Handles the headtracking events
 |
 | Version: v1.0
 | Date: January 2016
 | Author: Chris Parkinson
 |
  +--------------------------------------------------------------------------*/


public interface HFHeadtrackerListener {


    /////////////////////////////////////////////////////////////////////////////
    //
    // Head Movement Event
    //
    /////////////////////////////////////////////////////////////////////////////
    void onHeadMoved(float deltaX, float deltaY, float deltaZ);


    void onOrientationChanged(float pitch, float roll, float v);
}
