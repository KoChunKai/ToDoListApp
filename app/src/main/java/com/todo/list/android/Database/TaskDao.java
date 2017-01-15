package com.todo.list.android.Database;

import android.content.Context;

/**
 * Created by Kai on 17/1/15.
 */
public class TaskDao extends BaseDao<TaskTable, Integer>{

    public TaskDao(Context context) {
        super(context, TaskTable.class);
    }

}
