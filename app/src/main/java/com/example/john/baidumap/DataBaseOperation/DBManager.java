package com.example.john.baidumap.DataBaseOperation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import baseclass.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android学习 on 2017/12/12.
 */

public class DBManager {
    private static DataBase db;
    public static final int XLH_UPDATE = 0;
    public static final int XLH_INSERT = 1;

    //公开操作
    //更新或创建数据库
    public static void CreateUpdateDataBase(Context context, String name,
                                            SQLiteDatabase.CursorFactory factory, int version){
        db = new DataBase(context, name, factory, version);
        db.getWritableDatabase();
    }

    //增加新用户
    public static boolean AddUser(User user){
        //用户存在，不进行添加
        if (isExist(user.getAccount()))
            return false;

        //用户为空，不添加
        if (user.isEmpty())
            return false;

        //正常添加
      return xlhWrite(user, XLH_INSERT);
    }

    //更新用户信息
    public static boolean UpdateUser(User user){
        //用户不存在，不进行更新
        if (!isExist(user.getAccount()))
            return false;

        //用户为空，不更新
        if (user.isEmpty())
            return false;

        //正常更新
        return xlhWrite(user, XLH_UPDATE);
    }

    //删除用户信息
    //这个函数有点问题，不应该仅通过账户进行删除，其实还应该匹对密码才对
    //但可以通过在外层验证密码，而不是在数据库这里验证密码来达成
    public static boolean DeleteUser(User user){
        //用户不存在，不进行更新
        if (!isExist(user.getAccount()))
            return false;

        SQLiteDatabase database = db.getWritableDatabase();
        database.delete("userinfo", "account = '" + user.getAccount() + "'", null);
        return true;
    }

    //提取用户
    public static User GetUser(String account){
        //用户不存在，无法提取
        if (!isExist(account))
            return null;
        List<User> allUsers = xlhRead(searchByAccount(account));
        User user;

        //序列化失败
        if (allUsers == null)
            return null;
        else if (!allUsers.isEmpty())
            user = allUsers.get(0);
        else
            return null;

        if (user != null && !user.isEmpty())
            return user;
        else
            return null;
    }

    //内部操作
    //检查用户是否存在
    private static boolean isExist(String account){
       if (searchByAccount(account) == null)
           return false;
       else
           return true;
    }

    //搜索指定用户
    private static Cursor searchByAccount(String account){
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.query(true, "userinfo", null, "account = '" + account + "'",
                null, null, null, null, null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }


    //序列化写入数据库，当flag是UPDATE的时候，进行更新
    //当flag是INSERT的时候，进行插入
    private static boolean xlhWrite(User user, int flag){
        if (flag != XLH_UPDATE && flag != XLH_INSERT)
            return false;

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try{
            SQLiteDatabase database = db.getWritableDatabase();
            ContentValues values = new ContentValues();
            ObjectOutputStream out = new ObjectOutputStream(arrayOutputStream);
            out.writeObject(user);
            out.writeObject(null);
            out.flush();

            byte[] data = arrayOutputStream.toByteArray();

            out.close();
            arrayOutputStream.close();

            values.put("account", user.getAccount());
            values.put("password", user.getPassword());
            values.put("record", data);

            long count;
            if (flag == XLH_UPDATE)
                count = database.update("userinfo", values, "account = '" +
                user.getAccount() + "'", null);
            else
                count = database.insert("userinfo", null, values);

            if (count < 0)
                return false;
            else
                return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static List<User> xlhRead(Cursor cursor){
        Cursor cs = cursor;

        if (!cs.moveToFirst())
            return null;

        List<User> allUsers = new ArrayList<>();

        try{
            SQLiteDatabase database = db.getReadableDatabase();
            do {
                byte[] data = cs.getBlob(cs.getColumnIndex("record"));
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
                ObjectInputStream in = new ObjectInputStream(arrayInputStream);
                User user = (User) in.readObject();
                if (user != null)
                    allUsers.add(user);
                in.close();
                arrayInputStream.close();
            }while (cs.moveToNext());
            cs.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        if (allUsers.isEmpty())
            return null;
        else
            return allUsers;
    }

}
