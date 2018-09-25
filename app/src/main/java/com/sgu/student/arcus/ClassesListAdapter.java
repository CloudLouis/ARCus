package com.sgu.student.arcus;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sgu.student.arcus.db.Database;
import com.sgu.student.arcus.db.dao.ClassesDao;
import com.sgu.student.arcus.db.entity.ClassesEntity;

import java.util.List;

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ViewHolder> {

    private Context context;
    private List<ClassesEntity> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    ClassesListAdapter(Context context, List<ClassesEntity> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }
    ClassesListAdapter(){}

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.classes_list_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ClassesEntity classEntity = mData.get(position);
        holder.myTextView.setText(classEntity.getName());

        holder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database database = Room.databaseBuilder(context, Database.class, "mainDB").allowMainThreadQueries().build();
                ClassesDao classesDao = database.getClassesDao();

                classesDao.updateName(holder.nameEditField.getText().toString(), classEntity.getC_id());
                database.close();

                FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                ft.detach(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("currentFragment")).attach(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("currentFragment")).commit();
            }
        });

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.nameEditField.setVisibility(View.GONE);
                holder.confirmButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.GONE);

                holder.myTextView.setVisibility(View.VISIBLE);
                holder.buttonViewOption.setVisibility(View.VISIBLE);
                holder.classDesc.setVisibility(View.VISIBLE);
            }
        });
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.classes_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.classes_change_name:
                                holder.nameEditField.setVisibility(View.VISIBLE);
                                holder.confirmButton.setVisibility(View.VISIBLE);
                                holder.cancelButton.setVisibility(View.VISIBLE);

                                holder.myTextView.setVisibility(View.GONE);
                                holder.buttonViewOption.setVisibility(View.GONE);
                                holder.classDesc.setVisibility(View.GONE);
                                break;
                            case R.id.classes_delete:
                                Database database = Room.databaseBuilder(context, Database.class, "mainDB").allowMainThreadQueries().build();
                                ClassesDao classesDao = database.getClassesDao();

                                List<ClassesEntity> classes = classesDao.retrieveByName(holder.myTextView.getText().toString());
                                classesDao.delete(classes.get(0));
                                database.close();

                                FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                ft.detach(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("currentFragment")).attach(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("currentFragment")).commit();

                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }



    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myTextView;
        public AppCompatImageView buttonViewOption;
        public TextView nameEditField;
        public TextView classDesc;
        public Button confirmButton;
        public Button cancelButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameEditField = itemView.findViewById(R.id.edit_name);
            myTextView = itemView.findViewById(R.id.class_name);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
            classDesc = itemView.findViewById(R.id.class_desc);
            confirmButton = itemView.findViewById(R.id.confirm_class_name_change);
            cancelButton = itemView.findViewById(R.id.cancel_class_name_change);


        }
    }

}
