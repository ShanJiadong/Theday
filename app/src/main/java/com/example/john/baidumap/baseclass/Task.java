package com.example.john.baidumap.baseclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by android学习 on 2017/12/12.
 */

public class Task implements Serializable {
    private int begin_year;
    private int begin_month;
    private int begin_day;

    private int end_year;
    private int end_month;
    private int end_day;
    //private int end_hour;
    //private int end_minnute;

    private String task_name;
    private String task_content;
    private String task_attention;

    private List<Location> task_location;

    private boolean flag_finished;

/***************************************************************************************/
/*********************************初始化部分开始****************************************/

    public Task(){
        //初始化创建时间
        Calendar calendar = Calendar.getInstance();
        begin_year = calendar.get(Calendar.YEAR);
        begin_month = calendar.get(Calendar.MONTH);
        begin_day = calendar.get(Calendar.DAY_OF_MONTH);

        //无条件下默认创建一周任务
        //若创建日期为年末七天内
        if (begin_month == 12 && begin_day >= 23){
            end_year = begin_year + 1;
            end_month = 1;
            end_day = 7;
        }
        //不是第一种特殊情况，且不是月末
         else if (begin_day < 23) {
            end_day = begin_day + 7;
            end_month = begin_month;
            end_year = begin_year;
        }
        //不是第一种特殊情况，但在月末
        else {
            end_day = 7;
            end_month = begin_month + 1;
            end_year = begin_year;
        }

        task_name = "";
        task_content = "";
        task_attention = "";
        task_location = new ArrayList<>();
        flag_finished = false;
    }

    public Task(int end_year, int end_month, int end_day, String task_name, String task_content,
                String task_attention, List<Location> task_location){
        //初始化创建时间
        Calendar calendar = Calendar.getInstance();
        begin_year = calendar.get(Calendar.YEAR);
        begin_month = calendar.get(Calendar.MONTH);
        begin_day = calendar.get(Calendar.DAY_OF_MONTH);

        //初始化截止日期
        this.end_year = end_year;
        this.end_month = end_month;
        this.end_day = end_day;

        //初始化任务内容
        this.task_name = task_name;
        this.task_content = task_content;
        this.task_attention = task_attention;

        //初始化任务位置
        this.task_location = task_location;
        flag_finished = false;
    }

    public Task(int end_year, int end_month, int end_day, String task_name,
                String task_content, String task_attention){
        //初始化创建时间
        Calendar calendar = Calendar.getInstance();
        begin_year = calendar.get(Calendar.YEAR);
        begin_month = calendar.get(Calendar.MONTH);
        begin_day = calendar.get(Calendar.DAY_OF_MONTH);

        //初始化截止日期
        this.end_year = end_year;
        this.end_month = end_month;
        this.end_day = end_day;

        //初始化任务内容
        this.task_name = task_name;
        this.task_content = task_content;
        this.task_attention = task_attention;

        //初始化任务位置
        this.task_location = new ArrayList<>();
        flag_finished = false;
    }

/*********************************初始化部分结束****************************************/
/***************************************************************************************/


/***************************************************************************************/
/*********************************修改操作部分开始****************************************/

    public boolean setTaskName(String name){
        if (name.isEmpty())
            return false;
        this.task_name = name;
        return true;
    }

    public boolean setTaskContent(String content){
        if (content.isEmpty())
            return false;
        this.task_content = content;
        return true;
    }

    public boolean setTaskAttention(String attention){
        if (attention.isEmpty())
            return false;
        this.task_attention = attention;
        return true;
    }

    public boolean changeTime(int end_year, int end_month, int end_day){
        //获得当前时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //最多只允许添加本日任务，若时间早于当前时间，则当做错误，不修改任务时间
        if (end_day < year || (end_day == year && end_month < month) ||
                (end_year == year && end_month == month && end_day < day))
            return false;

        //满足条件，对任务时间进行修改
        this.end_day = end_day;
        this.end_month = end_month;
        this.end_year = end_year;
        return true;
    }

    public boolean addLocation(Location location){
        //若这个地址是空的，则不予以添加
        //若任务已关闭，也不允许添加
        if (location.getLatitude() == 0 && location.getLongitude() == 0 ||
                flag_finished == true)
            return false;

        //添加满足条件的地址
        this.task_location.add(location);
        return true;
    }

    public void close(){
        flag_finished = true;
    }

/*********************************修改操作部分结束**************************************/
/***************************************************************************************/


/***************************************************************************************/
/*********************************读取操作部分开始**************************************/

    public int getBeginYear(){
        return this.begin_year;
    }

    public int getBeginMonth(){
        return this.begin_month;
    }

    public int getBeginDay(){
        return this.begin_day;
    }

    public int getEndYear() {
        return this.end_year;
    }

    public int getEndMonth() {
        return this.end_month;
    }

    public int getEndDay() {
        return this.end_day;
    }

    public String getTaskName(){
        return this.task_name;
    }

    public String getTaskContent(){
        return this.task_content;
    }

    public String getTaskAttention(){
        return this.task_content;
    }

    //获取整个任务时间长度，单位为天
    public int TaskWholeLength(){
        int begin, end;
        if (this.end_year == this.begin_year){
            if (this.end_month == this.begin_month)
                return end_day - begin_day;

            else {
                begin = getdays(begin_month, begin_day);
                end = getdays(end_month, end_day);
                return end - begin;
            }
        }
        else {
            int sub = 365 * (this.end_year - this.begin_year);
            begin = getdays(begin_month, begin_day);
            end = getdays(end_month, end_day);
            return end + sub - begin;
        }
    }

    //获取距离截止日期的时间，单位为天
    public int TaskRestTime(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int begin, end;
        if (year == this.begin_year){
            if (month == this.begin_month)
                return day - begin_day;

            else {
                begin = getdays(begin_month, begin_day);
                end = getdays(month, day);
                return end - begin;
            }
        }
        else {
            int sub = 365 * (year - this.begin_year);
            begin = getdays(begin_month, begin_day);
            end = getdays(month, day);
            return end + sub - begin;
        }
    }

    public boolean isEmpty(){
        if (task_name.isEmpty() && task_content.isEmpty())
            return true;
        else
            return false;
    }

    public boolean isClose(){
        return flag_finished;
    }

/*********************************读取操作部分结束**************************************/
/***************************************************************************************/

//内部操作函数
    private int getdays(int month, int day){
        int len = 0;
        switch (month){
            case 1:
                len = day;
                break;
            case 2:
                len = day + 31;
                break;
            case 3:
                len = day + 31 + 28;
                break;
            case 4:
                len = day + 31 + 28 + 31;
                break;
            case 5:
                len = day + 31 + 28 + 31 + 30;
                break;
            case 6:
                len = day + 31 + 28 + 31 + 30 + 31;
                break;
            case  7:
                len = day + 31 + 28 + 31 + 30 + 31 + 30;
                break;
            case 8:
                len = day + 31 + 28 + 31 + 30 + 31 + 30 + 31;
                break;
            case 9:
                len = day + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31;
                break;
            case 10:
                len = day + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30;
                break;
            case 11:
                len = day + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31;
                break;
            case 12:
                len = day + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30;
                break;
                default:
                    len = 0;
                    break;
        }
        return len;
    }
}
