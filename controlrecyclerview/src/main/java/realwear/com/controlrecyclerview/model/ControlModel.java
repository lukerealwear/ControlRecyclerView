/*
 * RealWear Development Software, Source Code and Object Code.
 * Copyright (C) RealWear, Inc. - All rights reserved
 *
 * Contact info@realwear.com for further information about the use of this code.
 *
 */

package realwear.com.controlrecyclerview.model;


/**
 * Control Model class
 */
public abstract class ControlModel {

    private int mIconID;
    private String mTitle;
    public String mState;
    private iModelObserver mObserver;

    public ControlModel(){

    }

    public ControlModel(int icon, String title, String state){
        mIconID = icon;
        mTitle = title;
        mState = state;
    }

    public int getIcon(){
        return mIconID;
    }

    public void setIconID(int iconID){
        mIconID = iconID;
        updateModelObserver();
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title){
        mTitle = title;
        updateModelObserver();
    }

    public String getState(){
        return mState;
    }

    public void setState(String state){
        mState = state;
        updateModelObserver();
    }

    protected void updateModelObserver(){
        if(mObserver != null){
            mObserver.onDataChanged(this);
        }
    }

    public interface iModelObserver{
        void onDataChanged(ControlModel model);
    }


}
