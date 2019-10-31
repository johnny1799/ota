package com.asu.ota;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wzj on 2017/2/11.
 */
public class ListViewAdapter extends BaseAdapter
{
    /**
     * Context
     */
    private Context mContext;

    /**
     * 数据
     */
    private List<ProductBean> productBeanList;

    /**
     * 构造函数
     * @param context context
     * @param productBeanList productBeanList
     */
    public ListViewAdapter(Context context,List<ProductBean> productBeanList)
    {
        this.mContext = context;
        this.productBeanList = productBeanList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount()
    {
        return productBeanList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position)
    {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position)
    {
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        if (convertView != null)
        {
            view = convertView;
        }
        else
        {
            view = View.inflate(mContext, R.layout.product_listview, null);
        }

        ProductBean productBean = productBeanList.get(position);
        if (productBean == null)
        {
            productBean = new ProductBean("NoName","NoDesc");
        }

        //更新数据
        final TextView nameTextView = (TextView) view.findViewById(R.id.showStuName);
        nameTextView.setText(productBean.getName());

        TextView descTextView = (TextView)view.findViewById(R.id.showStuDesc);
        descTextView.setText(productBean.getDesc());

        final int removePosition = position;

        //删除按钮点击事件
        Button deleteButton = (Button)view.findViewById(R.id.showDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteButtonAction(removePosition);
            }
        });


        return view;
    }

    private void deleteButtonAction(int position)
    {
        productBeanList.remove(position);

        notifyDataSetChanged();
    }
}
