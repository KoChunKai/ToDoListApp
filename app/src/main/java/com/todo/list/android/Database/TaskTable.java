package com.todo.list.android.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Kai on 17/1/15.
 */

@DatabaseTable(tableName = "Task")
public class TaskTable implements Serializable{

    @DatabaseField(generatedId = true) private int id;
    @DatabaseField(columnName = "datetime") private String datetime;
    @DatabaseField(columnName = "task") private String task;
    @DatabaseField(columnName = "isFinish") private boolean isFinish;
    @DatabaseField(columnName = "isSync") private boolean isSync;

    public TaskTable() {

    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDatetime(){
        return datetime;
    }

    public void setDatetime(String datetime){
        this.datetime = datetime;
    }

    public String getTask(){
        return task;
    }

    public void setTask(String task){
        this.task = task;
    }

    public boolean isFinish(){
        return isFinish;
    }

    public void setFinish(boolean isFinish){
        this.isFinish = isFinish;
    }

    public boolean isSync(){
        return isSync;
    }

    public void setSync(boolean isSync){
        this.isSync = isSync;
    }
}
