package com.todo.list.android;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.todo.list.android.Database.TaskDao;
import com.todo.list.android.Database.TaskTable;
import com.todo.list.android.Http.HttpLoader;
import com.todo.list.android.Http.LoadingStatus;
import com.todo.list.android.Http.ParamData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kai on 17/1/15.
 */
public class Controller {

    private LoadingStatus loadingStatus;
    public List<TaskTable> data = new ArrayList<>();
    private TaskDao taskDao;

    public Controller(Context context, LoadingStatus loadingStatus){
        this.loadingStatus = loadingStatus;
        taskDao = new TaskDao(context);
    }

    public void loadData(){
        data = taskDao.getAll(true);
        if(data.size() > 0){
            loadingStatus.LoadingSuccess();
        }
        new HttpLoader()
                .setUrl("https://sheetsu.com/apis/v1.0/6085f8f15ded")
                .setHandler(handler, 1, -1)
                .setGet()
                .build()
                .start();
    }

    public void addData(String datetime, String task, boolean isFinish){
        TaskTable taskTable = new TaskTable();
        taskTable.setSync(false);
        taskTable.setDatetime(datetime);
        taskTable.setFinish(isFinish);
        taskTable.setTask(task);
        taskDao.insert(taskTable);
        data.add(taskTable);
        loadingStatus.LoadingUpdate();
        new HttpLoader()
                .setUrl("https://sheetsu.com/apis/v1.0/6085f8f15ded")
                .setHandler(handler, 2, -2)
                .set_paramData("datetime", new ParamData(datetime))
                .set_paramData("task", new ParamData(task))
                .set_paramData("isFinish", new ParamData(isFinish))
                .setExtraData(taskTable)
                .setPost()
                .build()
                .start();
    }

    public void delData(TaskTable taskTable){
        data.remove(taskTable);
        loadingStatus.LoadingUpdate();
        new HttpLoader()
                .setUrl("https://sheetsu.com/apis/v1.0/6085f8f15ded/datetime/" + taskTable.getDatetime())
                .setHandler(handler, 3, -3)
                .setExtraData(taskTable)
                .setDelete()
                .build()
                .start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try{
                        Log.d("result", msg.getData().getString(HttpLoader.EXECUTE_RESULT));
                        data.clear();
                        JSONArray array = new JSONArray(msg.getData().getString(HttpLoader.EXECUTE_RESULT));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            TaskTable taskTable = new TaskTable();
                            taskTable.setDatetime(json.optString("datetime"));
                            taskTable.setTask(json.optString("task"));
                            taskTable.setFinish(json.optBoolean("isFinish"));
                            taskTable.setSync(true);
                            List<TaskTable> list = taskDao.getByFildValue("datetime", taskTable.getDatetime());
                            if(list.size() == 0){
                                taskDao.insert(taskTable);
                            }
                            data.add(taskTable);
                        }
                        loadingStatus.LoadingSuccess();
                        List<TaskTable> syncList = taskDao.getByFildValue("isSync", false);
                        for (int i = 0; i < syncList.size(); i++) {
                            TaskTable taskTable = syncList.get(i);
                            new HttpLoader()
                                    .setUrl("https://sheetsu.com/apis/v1.0/6085f8f15ded")
                                    .setHandler(handler, 2, -2)
                                    .set_paramData("datetime", new ParamData(taskTable.getDatetime()))
                                    .set_paramData("task", new ParamData(taskTable.getTask()))
                                    .set_paramData("isFinish", new ParamData(taskTable.isFinish()))
                                    .setExtraData(taskTable)
                                    .setPost()
                                    .build()
                                    .start();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case -1:
                    break;
                case 2:
                    TaskTable addTask = (TaskTable) msg.getData().getSerializable("Data");
                    addTask.setSync(true);
                    taskDao.update(addTask);
                    break;
                case 3:
                    TaskTable delTaskSuccess = (TaskTable) msg.getData().getSerializable("Data");
                    taskDao.deleteById(delTaskSuccess.getId());
                    break;
            }
        }
    };

}
