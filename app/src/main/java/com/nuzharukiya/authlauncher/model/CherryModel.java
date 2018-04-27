package com.nuzharukiya.authlauncher.model;

import com.nuzharukiya.authlauncher.R;

import static com.nuzharukiya.authlauncher.utils.Constants.HZ_A;
import static com.nuzharukiya.authlauncher.utils.Constants.HZ_B;

/**
 * Created by Nuzha Rukiya on 18/01/23.
 */

public class CherryModel {

    private String cherryTitle;
    private String cherryBackground;
    private String cherryId;

    public String getCherryTitle() {
        return cherryTitle;
    }

    public void setCherryTitle(String cherryTitle) {
        this.cherryTitle = cherryTitle;
    }

    public int getCherryBackground() {
        switch (cherryId) {
            case HZ_A: {
                return R.color.material_accent;
            }
            case HZ_B: {
                return R.color.material_accent_dark;
            }
        }
        return -1;
    }

//    public int getCherryBackground() {
//        switch (cherryId) {
//            case HZ_A: {
//                return R.mipmap.ic_launcher;
//            }
//            case HZ_B: {
//                return R.mipmap.ic_launcher;
//            }
//        }
//        return -1;
//    }

    public void setCherryId(String cherryId) {
        this.cherryId = cherryId;
    }
}
