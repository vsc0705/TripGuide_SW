package com.example.trip2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    public ListViewAdapter(){}

    @Override
    public int getCount(){
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //복붙시 레이아웃 변경 주의
            convertView = inflater.inflate(R.layout.person_listview,parent,false);
        }

        //키워드를 여러개 가져올 수 있도록 리스트뷰 추후 수정 예정
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView keyword = (TextView) convertView.findViewById(R.id.keyword);

        ListViewItem listViewItem = listViewItemList.get(position);

        name.setText(listViewItem.getName());
        keyword.setText(listViewItem.getKeyword());
        return convertView;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Object getItem(int position){
        return listViewItemList.get(position);
    }

    public void addItem(String name, String keyword){
        ListViewItem item = new ListViewItem();

        item.setName(name);
        item.setKeyword(keyword);

        listViewItemList.add(item);
    }




}
