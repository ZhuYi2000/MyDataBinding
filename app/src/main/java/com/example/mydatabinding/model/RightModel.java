package com.example.mydatabinding.model;

import android.content.Context;
import android.util.Log;

import com.example.mydatabinding.enity.Trainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
    String file_url;

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
            preparedStatement.close();
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
        try {
            Connection conn = getConnection();
            String sql = "select * from user_pokemon where user_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setLong(1,trainer_id);
            ResultSet rs = preparedStatement.executeQuery();
            List<Integer> pid_list = new ArrayList<>();
            while (rs.next()){
                pid_list.add(rs.getInt("pokemon_id"));
            }
            callback.successPokemonByTrainer(trainer_id,pid_list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void insertPokemonByTrainer(long trainer_id, long pokemon_id) {

    }

    //通过文件的读写判断是否已经登录
    @Override
    public void isLogin(Context context) {
        file_url = context.getFilesDir().getPath()+"/trainerState.txt";
        File file = new File(file_url);
        if(file.exists()){
            //已经登录，读取文件，判断是否过期
            Log.d("zhu","登录文件存在，判断登录是否过期");
            Trainer trainer = new Trainer();
            try {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String str_tid = bufferedReader.readLine();
                long t_id = Long.parseLong(str_tid);

                String password = bufferedReader.readLine();
                String trainer_name = bufferedReader.readLine();

                String str_timestamp = bufferedReader.readLine();
                long timestamp = Long.parseLong(str_timestamp);
                long current_time = (long) System.currentTimeMillis()/1000;
                long daySeconds = 86400;//一天有86400秒
                if(current_time-timestamp<daySeconds){
                    Log.d("zhu","登录文件未过期");
                    trainer.setT_id(t_id);
                    trainer.setPassword(password);
                    trainer.setT_name(trainer_name);
                    callback.alreadyLogin(trainer);
                }else {
                    Log.d("zhu","登录文件已过期");
                    callback.notLogin();
                }
                bufferedReader.close();
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            //没有登录
            Log.d("zhu","登录文件不存在，不进行任何操作");
            callback.notLogin();
        }
    }

    @Override
    public void saveLogin(Trainer trainer, Context context) {
        try {
            long timecurrentTimeMillis = System.currentTimeMillis();//时间戳,用以判断登录是否过期
            long timestamp = (long) timecurrentTimeMillis/1000;//转换为秒数为最小单位的时间戳，方便计算
            String str_time = timestamp+"";
            file_url = context.getFilesDir().getPath()+"/trainerState.txt";
            FileOutputStream outputStream = new FileOutputStream(file_url);

            outputStream.write((trainer.getT_id()+"").getBytes(StandardCharsets.UTF_8));//ID
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(trainer.getPassword().getBytes(StandardCharsets.UTF_8));//密码
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(trainer.getT_name().getBytes(StandardCharsets.UTF_8));//姓名
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(str_time.getBytes(StandardCharsets.UTF_8));//时间戳

            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //通过让登录过期来退出
    @Override
    public void logout(Trainer the_trainer, Context context) {
        try {
            long timecurrentTimeMillis = System.currentTimeMillis();//时间戳,用以判断登录是否过期
            long timestamp = 1640966400;//将登录时间改为2022年1月1日，让登录过期
            String str_time = timestamp+"";
            file_url = context.getFilesDir().getPath()+"/trainerState.txt";
            FileOutputStream outputStream = new FileOutputStream(file_url);

            outputStream.write((the_trainer.getT_id()+"").getBytes(StandardCharsets.UTF_8));//ID
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(the_trainer.getPassword().getBytes(StandardCharsets.UTF_8));//密码
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(the_trainer.getT_name().getBytes(StandardCharsets.UTF_8));//姓名
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(str_time.getBytes(StandardCharsets.UTF_8));//时间戳

            outputStream.close();
            callback.alreadyLogout(the_trainer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface DataBaseCallback{
        void successAllTrainerName(List<String> name_list);
        void successTrainerByName(Trainer trainer);
        void failureTrainerByName(Trainer trainer);

        void successInsertTrainer();
        void failureInsertTrainer();

        void successPokemonByTrainer(long trainer_id,List<Integer> pid_list);
        void successInsertPT();
        void failureInsertPT();

        void alreadyLogin(Trainer trainer);
        void notLogin();
        void alreadyLogout(Trainer trainer);
    }
}
