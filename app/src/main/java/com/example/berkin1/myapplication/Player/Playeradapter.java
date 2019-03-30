package com.example.berkin1.myapplication.Player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.berkin1.myapplication.R;
import com.example.berkin1.myapplication.RxBus;

import java.util.ArrayList;

public class Playeradapter extends ArrayAdapter<Playeritem> {

    private final LayoutInflater inflater;
    private final Context context;
    private final ArrayList<Playeritem> persons;
    private ViewHolder holder;

    public Playeradapter(Context context, ArrayList<Playeritem> persons) {
        super(context, 0, persons);
        this.context = context;
        this.persons = persons;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Playeritem getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return persons.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_view_item, null);

            holder = new ViewHolder();
            holder.personImage = (ImageView) convertView.findViewById(R.id.person_image);
            holder.personNameLabel = (TextView) convertView.findViewById(R.id.person_name_label);
            holder.personAddressLabel = (TextView) convertView.findViewById(R.id.person_address_label);
            convertView.setTag(holder);

        } else {
            //Get viewholder we already created
            holder = (ViewHolder) convertView.getTag();
        }

        final Playeritem person = persons.get(position);
        if (person != null) {
            holder.personImage.setImageResource(person.getPhotoId());
            holder.personNameLabel.setText(person.getName());
            holder.personAddressLabel.setText(person.getAddress());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RxBus.get().post("eat",person.getAddress());
                }
            });
        }
        return convertView;
    }

    //View Holder Pattern for better performance
    private static class ViewHolder {
        TextView personNameLabel;
        TextView personAddressLabel;
        ImageView personImage;

    }
}