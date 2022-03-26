package com.example.mydatabinding.model;

import android.util.Log;

import com.example.mydatabinding.enity.Trainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RightModel implements IDBModel {
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

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(url,user,password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    DataBaseCallback callback;

    public RightModel(DataBaseCallback callback) {
        this.callback = callback;
    }

    @Override
    public void selectAllTrainerName() {
        //数据库操作必须在新的线程中操作
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select user_name from user_base");
            List<String> name_list = new ArrayList<>();
            while (rs.next()){
                name_list.add(rs.getString("user_name"));
            }
            callback.successAllTrainerName(name_list);
            rs.close();
            st.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void selectTrainerByName(String trainer_name) {
        //数据库操作必须在新的线程中操作
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            String sql = "select * from user_base where user_name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,trainer_name);
            ResultSet rs = preparedStatement.executeQuery();
            Trainer trainer = new Trainer();
            while (rs.next()){
                trainer.setT_name(rs.getString("user_name"));
                trainer.setPassword(rs.getString("user_password"));
                trainer.setT_id(rs.getLong("user_id"));
            }
            if(trainer.getT_name()!=null) callback.successTrainerByName(trainer);
            else callback.failureTrainerByName(trainer);//用户名不存在

            rs.close();
            st.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void insertTrainer(String t_name,String password) {
        try {
            Connection conn = getConnection();
            String sql = "insert into user_base(user_name,user_password) values (?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,t_name);
            preparedStatement.setString(2,password);
            int result = preparedStatement.executeUpdate();
            if(result == 1) callback.successInsertTrainer();
            else callback.failureInsertTrainer();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void selectPokemonByTrainer(long trainer_id) {

    }

    @Override
    public void insertPokemonByTrainer(long trainer_id, long pokemon_id) {

    }

    @Override
    public void isLogin() {

    }

    public interface DataBaseCallback{
        void successAllTrainerName(List<String> name_list);
        void successTrainerByName(Trainer trainer);
        void failureTrainerByName(Trainer trainer);

        void successInsertTrainer();
        void failureInsertTrainer();

        void successPokemonByTrainer(long trainer_id,List<Long> pid_list);
        void successInsertPT();
        void failureInsertPT();
    }
}
