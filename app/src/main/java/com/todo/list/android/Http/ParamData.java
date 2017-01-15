package com.todo.list.android.Http;

/**
 * Created by kevin on 2016/12/23.
 */
public class ParamData<T> {

    private T data;

    public ParamData(T value){
        setData(value);
    }

    public void setData(T value){
        this.data = value;
    }

    public T getData(){
        return data;
    }
}
