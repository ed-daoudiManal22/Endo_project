package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Models.Post;
import com.example.myapplication.R;

import java.util.List;

public class ForumAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> postList;

    public ForumAdapter(Context context, List<Post> postList) {
        super(context, R.layout.item_post, postList);
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.postTextView = convertView.findViewById(R.id.text_view_post);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Post post = postList.get(position);
        viewHolder.postTextView.setText(post.getPostText());

        return convertView;
    }

    private static class ViewHolder {
        TextView postTextView;
    }
}
