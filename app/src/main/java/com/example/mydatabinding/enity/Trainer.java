package com.example.mydatabinding.enity;

//用户类（训练师类）
public class Trainer {
    public long t_id;
    public String password;
    public String t_name;

    public Trainer() {
    }

    public Trainer(long t_id, String password, String t_name) {
        this.t_id = t_id;
        this.password = password;
        this.t_name = t_name;
    }

    public long getT_id() {
        return t_id;
    }

    public String getPassword() {
        return password;
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_id(long t_id) {
        this.t_id = t_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }
}
