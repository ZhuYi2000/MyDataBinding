package com.example.mydatabinding.enity;

import android.util.Log;

public class Pokemon {
    //id是宝可梦唯一标识，可以根据ID获取大图、4小图
    public Pokemon(int id) {
        this.id = id;
    }

    // 一个宝可梦有9个属性
    public int id;
    public String name;
    public int exp;//经验值
    public double height;
    public double weight;

    public int hp;//生命值
    public int attack;//攻击值
    public int defense;//防御值
    public int speed;//速度值


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getExp() {
        return exp;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void printAttr(){
        String message = "id:"+id+"\n"+
                "name:"+name+"\n"+
                "exp:"+exp+"\n"+
                "height:"+height+"\n"+
                "weight:"+weight+"\n"+
                "hp:"+hp+"\n"+
                "attack:"+attack+"\n"+
                "defense:"+defense+"\n"+
                "speed:"+speed+"\n";
        Log.e("zhu",message);
    }
}
