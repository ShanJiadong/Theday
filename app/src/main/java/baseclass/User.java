package baseclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android学习 on 2017/12/12.
 */

public class User implements Serializable {
    private String Account;
    private String Password;
    private String NickName;
    private String Gender;

    private int gender_flag;
    private static final int BOY = 1;
    private static final int GIRL = 0;

    List<Task> AllTask;
    List<Task> FinishedTask;
    List<Task> CurrentTask;

/************************************************************************************************/
/****************************初始化部分开始******************************************************/

    public User(String account, String password, String nickName, String gender){
        this.Account = account;
        this.Password = password;
        this.NickName = nickName;
        this.Gender = gender;

        if (Gender.equals("男") || Gender.equals("BOY") ||
                Gender.equals("boy") ||  Gender.equals("男生") ||
                Gender.equals("男人") || Gender.equals("Boy") ||
                Gender.equals("BOy") || Gender.equals("bOY") ||
                Gender.equals("bOy") || Gender.equals("boY") ||
                Gender.equals("BoY"))
            gender_flag = BOY;
        else
            gender_flag = GIRL;

        AllTask = new ArrayList<>();
        FinishedTask = new ArrayList<>();
        CurrentTask = new ArrayList<>();
    }

    public User(){
        Account = "";
        Password = "";
        NickName = "";
        Gender = "boy";
        AllTask = new ArrayList<>();
        FinishedTask = new ArrayList<>();
        CurrentTask = new ArrayList<>();
    }

/****************************初始化部分结束******************************************************/
/************************************************************************************************/


/************************************************************************************************/
/****************************读取操作部分开始****************************************************/

    public String getAccount(){
        return Account;
    }

    public String getPassword(){
        return Password;
    }

    public String getNickName(){
        return NickName;
    }

    public boolean isBoy(){
        if (gender_flag == BOY)
            return true;
        else
            return false;
    }

    //获取任务进度
    public double TaskSchedule(){
        if (AllTask.isEmpty())
            return -1;
        else {
            if (FinishedTask.isEmpty())
                return 0;
            else if (FinishedTask.size() == AllTask.size())
                return 1;
            else {
                return (FinishedTask.size() / AllTask.size());
            }
        }
    }

    public List<Task> getAllTask(){
        return AllTask;
    }

    public List<Task> getFinishedTask(){
        return FinishedTask;
    }

    public List<Task> getCurrentTask(){
        return CurrentTask;
    }

    public boolean isEmpty(){
        if (Account.isEmpty() && Password.isEmpty())
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        String word;
        if (NickName.isEmpty())
            word = "欢迎" + Account;
        else
            word = "欢迎" + NickName;

        if (isBoy())
            word += "先生！";
        else
            word += "女士！";
        return word;
    }

/****************************读取操作部分结束****************************************************/
/************************************************************************************************/


/************************************************************************************************/
/****************************修改操作部分结束****************************************************/

    public void setNickName(String name){
        this.NickName = name;
    }

    public void copy(User user){
       this.Account = user.getAccount();
       this.Password = user.getPassword();
       this.NickName = user.getNickName();
       this.AllTask = user.getAllTask();
       this.FinishedTask = user.getFinishedTask();
       this.CurrentTask = user.getCurrentTask();
       if (user.isBoy()){
           this.Gender = "boy";
           this.gender_flag = BOY;
       }
       else {
           this.Gender = "girl";
           this.gender_flag = GIRL;
       }
    }

    public boolean AddTask(Task task){
        //空白任务以及已结束的任务，不允许添加
        if (task.isEmpty() || task.isClose())
            return false;
        AllTask.add(task);
        CurrentTask.add(task);
        return true;
    }

    //完成任务时，修改数据
    public boolean CompleteTask(Task task){
        //如果任务是空的，则无法进行操作
        if (task.isEmpty())
            return false;
        if (CurrentTask.contains(task) && AllTask.contains(task) && !FinishedTask.contains(task)){
            CurrentTask.remove(task);
            AllTask.remove(task);

            task.close();
            AllTask.add(task);
            FinishedTask.add(task);
            return true;
        }
        else
            return false;
    }

    //按照条件弹出Current中的任务,不存在目标时返回null
    public Task getTask(String task_name){
        for (Task task : CurrentTask){
            if (task.getTaskName().equals(task_name))
                return task;
        }
        return null;
    }

    //按照条件弹出满足的task列表
    public List<Task> getTask(String task_name, String content){
       List<Task> list = new ArrayList<>();
       for (Task task : CurrentTask){
           if (task.getTaskName().equals(task_name))
               list.add(task);
       }
       return list;
    }

/****************************修改操作部分结束****************************************************/
/************************************************************************************************/

}
