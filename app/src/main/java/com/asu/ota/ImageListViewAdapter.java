package com.asu.ota;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asu.ota.http.Request;

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
                SQLiteDatabase db = ImageActivity.helper.getWritableDatabase();

                int dbid = 0;
                Cursor cursor = db.rawQuery("select dbid from package where version=?",new String[]{name});
                while (cursor.moveToNext()) {
                     dbid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
                }
                try{
                    String url  = "http://192.168.11.220:8089/image/version/delete?id="+dbid;
                    String result = new Request().sendDelete(url);
                    JSONObject jo = new JSONObject(new String(result));
                    Integer code = (Integer)jo.get("code");
                    if(code==0){
                        //数据库删除
                        db.delete("Package", "version = ? and productid = ?", new String[]{name,ImageActivity.productId+""});

                        deleteButtonAction(removePosition);

                        //重新加载列表
                        ImageActivity.query(db);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //修改按钮点击事件
        Button updateButton = (Button) view.findViewById(R.id.showOtaButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 加载输入框的布局文件
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout layout = (LinearLayout) inflater.inflate(
                        R.layout.input_edit, null);

                EditText inputEditOldname = layout
                        .findViewById(R.id.input_edit_oldname);
                inputEditOldname.setText(name);
                new AlertDialog.Builder(mContext)
                        /* 弹出窗口的最上头文字 */
                        .setTitle("修改一条数据")
                        /* 设置弹出窗口的图式 */
                        .setIcon(android.R.drawable.ic_dialog_info)
                        /* 设置弹出窗口的信息 */
                        .setMessage("请输入修改的内容")
                        .setView(layout)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface, int i) {
                                        EditText inputEditNewname = layout
                                                .findViewById(R.id.input_edit_newname);

                                        String nname = inputEditNewname.getText()
                                                .toString();

                                        if (nname == null|| nname.equals("")) {
                                            Toast.makeText(mContext,
                                                    "请输入要修改的产品名称", Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            SQLiteDatabase db = ImageActivity.helper.getWritableDatabase();
                                            try{
                                                int dbid = 0;
                                                Cursor cursor = db.rawQuery("select dbid from product where name=?",new String[]{name});
                                                while (cursor.moveToNext()) {
                                                    dbid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
                                                }
                                                String url  = "http://192.168.11.220:8089/product/edit?id="+dbid+"&name="+nname+"&comment=";
                                                String result = new Request().sendPut(url);
                                                JSONObject jo = new JSONObject(new String(result));
                                                Integer code = (Integer)jo.get("code");
                                                if(code == 0){
                                                    ContentValues values = new ContentValues();
                                                    values.put("name",nname);
                                                    db.update("Product",values,"name=?",new String[] {name});
                                                    //重新加载列表
                                                    ImageActivity.query(db);

                                                    Toast.makeText(
                                                            mContext,
                                                            "数据修改为:" + nname + "",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                                    public void onClick(
                                            DialogInterface dialoginterface, int i) {
                                        Toast.makeText(mContext,
                                                "取消了修改数据", Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                }).show();


            }
        });


        return view;
    }

    private void deleteButtonAction(int position) {
        versionBeanList.remove(position);

        //notifyDataSetChanged();
    }
}
