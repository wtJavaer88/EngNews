package net.widget.act.abs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * 强制子类必须实现getView2方法, 也就是变相的getView
 * 
 * @author cpr216
 * 
 */
public abstract class MyActAdapter extends BaseAdapter implements Filterable
{
    protected List<AutoCompletable> autoItems;
    protected List<AutoCompletable> mFilterItems;
    protected ArrayFilter mArrayFilter;
    protected int maxMatch;
    protected Context context;

    public MyActAdapter(Context context, List<AutoCompletable> autoItems,
            int maxMatch)
    {
        this.context = context;
        this.autoItems = autoItems;
        this.maxMatch = maxMatch;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return autoItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return autoItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * 留给子类重写
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getView2(position, convertView, parent);
    }

    protected abstract View getView2(int position, View convertView,
            ViewGroup parent);

    @Override
    public Filter getFilter()
    {
        if (mArrayFilter == null)
        {
            mArrayFilter = new ArrayFilter();
        }
        return mArrayFilter;
    }

    private class ArrayFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();
            if (mFilterItems == null)
            {
                mFilterItems = new ArrayList<AutoCompletable>(autoItems);
            }
            // 如果没有过滤条件
            if (constraint == null || constraint.length() == 0)
            {
                results.values = mFilterItems;
                results.count = mFilterItems.size();
            }
            else
            {
                List<AutoCompletable> retList = new ArrayList<AutoCompletable>();
                // 过滤条件
                String str = constraint.toString().toLowerCase();
                AutoCompletable book;
                // 循环变量数据源，如果有属性满足过滤条件，则添加到result中
                for (int i = 0; i < mFilterItems.size(); i++)
                {
                    book = mFilterItems.get(i);
                    if (book.match(str))
                    {
                        retList.add(book);
                    }
                    if (retList.size() >= maxMatch)
                    {
                        break;
                    }
                }
                results.values = retList;
                results.count = retList.size();
            }
            return results;
        }

        // 在这里返回过滤结果
        @Override
        protected void publishResults(CharSequence constraint,
                FilterResults results)
        {
            // notifyDataSetInvalidated()，会重绘控件（还原到初始状态）
            // notifyDataSetChanged()，重绘当前可见区域
            // autoItems表示当前显示的列表, 每次都要更新
            autoItems = (List<AutoCompletable>) results.values;
            if (autoItems.size() > 0)
            {
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated();
            }
        }

    }

}
