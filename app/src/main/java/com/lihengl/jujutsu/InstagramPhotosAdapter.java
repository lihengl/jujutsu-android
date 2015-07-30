package com.lihengl.jujutsu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;


public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {


    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transformation transformation = new RoundedTransformationBuilder().cornerRadiusDp(30).oval(false).build();
        InstagramPhoto photo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        TextView tvCreatedTime = (TextView) convertView.findViewById(R.id.tvCreatedTime);
        tvCreatedTime.setText(photo.createdTime);

        TextView tvLikesCount = (TextView) convertView.findViewById(R.id.tvLikesCount);
        tvLikesCount.setText(String.valueOf(photo.likesCount));

        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvUsername.setText(photo.username);

        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        tvCaption.setText(photo.caption);

        ImageView ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
        ivProfile.setImageResource(0);
        Picasso.with(getContext()).load(photo.profileUrl).transform(transformation).into(ivProfile);

        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
        ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageUrl).into(ivPhoto);

        return convertView;
    }

}
