package com.example.mydatabinding.view;

import com.example.mydatabinding.enity.Trainer;

//待完善这个接口
//一开始是登录按钮、注册按钮
//点击登录按钮，输入信息，调用presenter的login方法，成功后，显示界面
public interface IDBView {
    void showErrorMessage();
    void showSuccessRegister();//注册成功
    void showExistMessage(String t_name);//注册失败
    void showNotExitMessage();//登陆时用户不存在
    void showPassError();//登陆时密码错误

    void showAfterLogin(Trainer trainer);
}
