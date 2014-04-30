package com.twlkyao.androidcloud;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.twlkyao.dao.DaoMaster;
import com.twlkyao.dao.DaoSession;
import com.twlkyao.dao.FileInfo;
import com.twlkyao.dao.FileInfoDao;
import com.twlkyao.dao.DaoMaster.DevOpenHelper;
import com.twlkyao.dao.FileInfoDao.Properties;
import com.twlkyao.utils.FileOperation;

import de.greenrobot.dao.query.QueryBuilder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<File> files; // The ArrayList to store the files
    private boolean isSDcard; // To indicate whether the file is the SDcard
    private LayoutInflater mInflater; // The LayoutInflater
      
    public FileListAdapter (Context context, ArrayList<File> files, boolean isSDcard) {
        this.context = context;
        this.files = files;
        this.isSDcard = isSDcard;
        mInflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount () {
        return files.size();
    }

    @Override
    public Object getItem (int position) {
        return files.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }
    
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        /**
         * The view is not initialized,initialize the view according to the layout file,
         * and bind it to tag.
         */
        if(convertView == null) { 
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.file_list_item, null); // Inflate from the layout file
            convertView.setTag(viewHolder);
            viewHolder.file_title = (TextView) convertView.findViewById(R.id.file_title); // Find the file_title textview
            viewHolder.file_icon = (ImageView) convertView.findViewById(R.id.file_icon); // Find the file_icon imageview
            viewHolder.file_path = (TextView) convertView.findViewById(R.id.file_path); // Find the file_path textview
        } else {
              viewHolder = (ViewHolder) convertView.getTag();
        }
        File file = (File) getItem(position);
        if(position == 0 && !isSDcard) { // Add the back to SDcard item
	        viewHolder.file_title.setText(R.string.back_to_sdcard);
	        viewHolder.file_icon.setImageResource(R.drawable.sdcard_icon);
	        viewHolder.file_path.setText(Environment.getExternalStorageDirectory().toString());
        }
        if(position == 1 && !isSDcard) { // Add the back to parent item
	        viewHolder.file_title.setText(R.string.back_to_parent);
	        viewHolder.file_icon.setImageResource(R.drawable.folder_up_icon);
	        viewHolder.file_path.setText(file.getPath());
        } else { // The current file path is the SDcard or the position is neither 0 nor 1
              String fileName = file.getName();
              viewHolder.file_title.setText(fileName);
              viewHolder.file_path.setText(file.getPath());
              if(file.isDirectory()) { // The variable file is a directory
                  viewHolder.file_icon.setImageResource(R.drawable.folder_icon);
              } else { // The variable file is a file
            	
//            	viewHolder.file_icon.setImageResource(R.drawable.file_icon);
            	
            	FileOperation fileOperation = new FileOperation();
            	String md5String = fileOperation.fileToMD5(file.getPath());
            	String sha1String = fileOperation.fileToSHA1(file.getPath());
            	
            	DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "encrypt_level_db", null);
            	SQLiteDatabase db = helper.getWritableDatabase();
            	DaoMaster daoMaster = new DaoMaster(db);
            	DaoSession daoSession = daoMaster.newSession();
                FileInfoDao fileInfoDao = daoSession.getFileInfoDao();
            	
                QueryBuilder<FileInfo> qb = fileInfoDao.queryBuilder();
                qb.where(qb.and(Properties.Sha1.eq(sha1String),
                		Properties.Md5.eq(md5String)));
                List<FileInfo> fileInfoList = qb.list();
                int size = fileInfoList.size();
                if(0 == size) { // The encrypt level has not been set.
                	viewHolder.file_icon.setImageResource(R.drawable.file_icon);
                } else { // The encrypt level is set.
                	String levelString = fileInfoList.get(0).getLevel();
                    int resID = context.getResources()
                    		.getIdentifier("file_icon_" + levelString, "drawable",
                    		context.getPackageName());
                    Drawable image = context.getResources().getDrawable(resID); 
                    viewHolder.file_icon.setImageDrawable(image);
                }
              }
          }
     
     return convertView;
}
    private class ViewHolder {
    	private ImageView file_icon; // The icon of the file
		private TextView file_title; // The title of the file
		private TextView file_path; // The path of the file
    }
}