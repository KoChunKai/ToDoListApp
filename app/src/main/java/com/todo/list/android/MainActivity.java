package com.todo.list.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.todo.list.android.Database.TaskTable;
import com.todo.list.android.Http.LoadingStatus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoadingStatus {

    Controller controller;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter());
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                controller.loadData();
            }
        });
        controller = new Controller(this, this);
        controller.loadData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.addview, null);
                final EditText et_datetime = (EditText) view.findViewById(R.id.et_datetime);
                final EditText et_task =  (EditText) view.findViewById(R.id.et_task);
                final CheckBox ck_isFinish =  (CheckBox) view.findViewById(R.id.ck_isFinish);
                new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.addData(et_datetime.getText().toString(), et_task.getText().toString(), ck_isFinish.isChecked());
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    public void setRefreshing(final boolean isRefresh){
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(isRefresh);
            }
        });
    }

    @Override
    public void LoadingSuccess() {
        setRefreshing(false);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void LoadingUpdate() {
        setRefreshing(false);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void LoadingFailure() {
        setRefreshing(false);
    }

    public class Holder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_datetime)
        TextView tv_datetime;
        @Bind(R.id.tv_task)
        TextView tv_task;
        @Bind(R.id.tv_status)
        TextView tv_status;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("DELETE")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    controller.delData(controller.data.get(getAdapterPosition()));
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return false;
                }
            });
        }
    }

    public class Adapter extends RecyclerView.Adapter<Holder>{

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.itemview, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            TaskTable object = controller.data.get(position);
            holder.tv_datetime.setText(object.getDatetime());
            holder.tv_task.setText(object.getTask());
            holder.tv_status.setText(String.valueOf(object.isFinish()));
        }

        @Override
        public int getItemCount() {
            return controller.data.size();
        }
    }
}
