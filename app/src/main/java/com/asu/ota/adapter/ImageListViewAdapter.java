package com.asu.ota.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.asu.ota.activity.ImageActivity;
import com.asu.ota.activity.OtaActivity;
import com.asu.ota.R;
import com.asu.ota.utils.CommonRequest;
import com.asu.ota.model.ImageBean;

import org.json.JSONObject;

import java.util.List;

public class ImageListViewAdapter extends BaseAdapter{
    /**
     * Context
     */
    private Context mContext;

    /**
     * 数据
     */
    private List<ImageBean> versionBeanList;

    /**
     * 构造函数
     *
     * @param context         context
     * @param versionBeanList versionBeanList
     */
    public ImageListViewAdapter(Context context, List<ImageBean> versionBeanList) {
        this.mContext = context;
        this.versionBeanList = versionBeanList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return versionBeanList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = View.inflate(mContext, R.layout.image_listview, null);
        }

        ImageBean versionBean = versionBeanList.get(position);
        if (versionBean == null) {
            versionBean = new ImageBean("NoName");
        }

        //更新数据
        final TextView nameTextView = (TextView) view.findViewById(R.id.showProName);
        nameTextView.setText(versionBean.getVersion());

        final int removePosition = position;
        final String name = versionBeanList.get(removePosition).getVersion();

        //删除按钮点击事件
        Button deleteButton = (Button) view.findViewById(R.id.showImgDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = ImageActivity.sHelper.getWritableDatabase();

                int dbid = 0;
                Cursor cursor = db.rawQuery("select dbid from package where version=?",new String[]{name});
                while (cursor.moveToNext()) {
                     dbid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
                }
                try{
                    String url  = "/image/version/delete?id="+dbid;
                    String result = new CommonRequest().sendDelete(url);
                    JSONObject jo = new JSONObject(new String(result));
                    Integer code = (Integer)jo.get("code");
                    if(code==0){
                        //数据库删除
                        db.delete("Package", "version = ? and productid = ?", new String[]{name,ImageActivity.sProductId+""});

                        deleteButtonAction(removePosition);

                        //重新加载列表
                        ImageActivity.query(db);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //ota按钮点击事件
        Button otaButton = (Button) view.findViewById(R.id.showOtaButton);
        otaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ImageActivity.sContext, OtaActivity.class);
                intent.putExtra("productId", ImageActivity.sProductId);
                intent.putExtra("version",name);
                ImageActivity.sContext.startActivity(intent);
            }
        });


        return view;
    }

    private void deleteButtonAction(int position) {
        versionBeanList.remove(position);

        //notifyDataSetChanged();
    }
}
