package com.example.lenovo.orc_android.presenter;

import com.example.lenovo.orc_android.listener.IUserView;

/**
 * Created by lenovo on 2017/1/26.
 */

public class UserPresenter {
    private IUserView iUserView;
    private String s;

    public UserPresenter(IUserView iUserView){
        this.iUserView = iUserView;
    }

    public String saveUser(){
        s = iUserView.getFristName();
        return s;
    }
}
