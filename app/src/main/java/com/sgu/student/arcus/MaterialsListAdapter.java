package com.sgu.student.arcus;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
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
import com.sgu.student.arcus.db.dao.MaterialsDao;
import com.sgu.student.arcus.db.entity.MaterialsEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MaterialsListAdapter extends RecyclerView.Adapter<MaterialsListAdapter.ViewHolder> {

    private Context context;
    private List<MaterialsEntity> mData;
    private LayoutInflater mInflater;
    private MaterialsPage parentFragment;

    // data is passed into the constructor
    MaterialsListAdapter(Context context, List<MaterialsEntity> data, MaterialsPage parentFragment) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.parentFragment = parentFragment;
    }
    MaterialsListAdapter(){}

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.materials_list_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MaterialsEntity materialsEntity = mData.get(position);
        holder.myTextView.setText(materialsEntity.getTitle());
        holder.filePath.setText(materialsEntity.getPath());


        holder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database database = Room.databaseBuilder(context, Database.class, "mainDB").allowMainThreadQueries().build();
                MaterialsDao materialsDao = database.getMaterialsDao();

                materialsDao.updateTitle(holder.nameEditField.getText().toString(), materialsEntity.getM_id());
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
                holder.filePath.setVisibility(View.VISIBLE);
            }
        });
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.materials_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.materials_change_name:
                                holder.nameEditField.setVisibility(View.VISIBLE);
                                holder.confirmButton.setVisibility(View.VISIBLE);
                                holder.cancelButton.setVisibility(View.VISIBLE);

                                holder.myTextView.setVisibility(View.GONE);
                                holder.buttonViewOption.setVisibility(View.GONE);
                                holder.filePath.setVisibility(View.GONE);
                                break;
                            case R.id.materials_delete:
                                Database database = Room.databaseBuilder(context, Database.class, "mainDB").allowMainThreadQueries().build();
                                MaterialsDao materialsDao = database.getMaterialsDao();

                                List<MaterialsEntity> classes = materialsDao.retrieveByTitle(holder.myTextView.getText().toString());
                                materialsDao.delete(classes.get(0));
                                database.close();

                                FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                ft.detach(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("currentFragment")).attach(((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("currentFragment")).commit();

                                break;
                            case R.id.materials_open:
                                open_file("com.sgu.student.arcus.provider", holder.filePath.getText().toString());

                                break;
                            case R.id.materials_share:
                                try {
                                    shareFile(holder.filePath.getText().toString());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }
    public void open_file(String authority, String file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (file.contains(".doc") || file.contains(".docx")) {
            // Word document
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "application/msword");
        } else if(file.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "application/pdf");
        } else if(file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "application/vnd.ms-powerpoint");
        } else if(file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "application/vnd.ms-excel");
        } else if(file.toString().contains(".zip") || file.toString().contains(".rar")) {
            // ZIP Files
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "application/zip");
        } else if(file.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "application/rtf");
        } else if(file.toString().contains(".wav") || file.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "audio/x-wav");
        } else if(file.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "image/gif");
        } else if(file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "image/jpeg");
        } else if(file.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "text/plain");
        } else if(file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(FileProvider.getUriForFile(context, authority,new File(file)), "*/*");
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

    }

    public void shareFile(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        File f = new File(filePath);
        String name = f.getName();
        parentFragment.sendFile(f, name);
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
        public TextView filePath;
        public Button confirmButton;
        public Button cancelButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameEditField = itemView.findViewById(R.id.edit_title);
            myTextView = itemView.findViewById(R.id.materials_title);
            buttonViewOption = itemView.findViewById(R.id.materialsListOptions);
            filePath = itemView.findViewById(R.id.materials_path);
            confirmButton = itemView.findViewById(R.id.confirm_material_title_change);
            cancelButton = itemView.findViewById(R.id.cancel_material_title_change);


        }
    }

}
