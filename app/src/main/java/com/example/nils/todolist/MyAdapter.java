package com.example.nils.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyAdapter extends ArrayAdapter<ToDoListItem> {

    public MyAdapter(Context context, ArrayList<ToDoListItem> items) {
        super(context, R.layout.row_layout, items);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_layout, parent, false);

        final ToDoListItem toDoListItem = getItem(position);
        String item = "No entries yet";
        Boolean completed = false;
        if (toDoListItem != null) {
            item = toDoListItem.item;
            completed = toDoListItem.completed;
        }

        TextView textView = (TextView) view.findViewById(R.id.textView1);
        textView.setText(item);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setTag(Integer.valueOf(position)); // set the tag so we can identify the correct row in the listener
        checkBox.setChecked(completed); // set the status as we stored it

        return view;

    }

}
