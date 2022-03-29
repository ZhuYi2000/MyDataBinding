package com.example.mydatabinding.model;

import android.content.Context;

import com.example.mydatabinding.enity.Trainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CameraModel implements IDBModel{

    private static final String url = "jdbc:mysql://rm-bp1bnehb104c22283fo.mysql.rds.aliyuncs.com/pokemon?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai";
    private static final String user = "p_database";
    private static final String password = "Yige1234";
    private static final String driver = "com.mysql.jdbc.Driver";

    static {
        try {
            Class.forName(driver);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    private CameraCallback callback;


    public CameraModel(CameraCallback callback) {
        this.callback = callback;
    }

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(url,user,password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    //捕捉成功后，新增user_pokemon记录
    @Override
    public void insertPokemonByTrainer(long trainer_id, int pokemon_id) {
        try {
            Connection conn = getConnection();
            String sql = "insert into user_pokemon(user_id,pokemon_id) values (?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setLong(1,trainer_id);
            preparedStatement.setInt(2,pokemon_id);
            int result = preparedStatement.executeUpdate();
            if(result==1) callback.successInsert(trainer_id,pokemon_id);
            else callback.failureInsert();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //没有用的方法，useless方法
    @Override
    public void selectAllTrainerName() {

    }
    @Override
    public void selectTrainerByName(String trainer_name) {

    }
    @Override
    public void insertTrainer(String t_name, String password) {

    }
    @Override
    public void selectPokemonByTrainer(long trainer_id) {

    }
    @Override
    public void isLogin(Context context) {

    }
    @Override
    public void saveLogin(Trainer trainer, Context context) {

    }
    @Override
    public void logout(Trainer the_trainer, Context context) {

    }

    public interface CameraCallback{
        void successInsert(long trainer_id, int pokemon_id);
        void failureInsert();
    }
}
