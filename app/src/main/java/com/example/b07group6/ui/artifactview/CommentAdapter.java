package com.example.b07group6.ui.artifactview;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07group6.R;
import com.example.b07group6.construct.Comment;
import com.google.firebase.Timestamp;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
    Context context;
    List<Comment> commentList;
    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }
    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_comments, parent, false);
        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        holder.username.setText(commentList.get(position).getUsername());
        holder.commentBodyText.setText(commentList.get(position).getText());
        Timestamp firebaseTime = commentList.get(position).getTimestamp();
        // get time in relative format (e.g. 5 mins ago)
        if (firebaseTime != null) {
            String relativeTimeString = DateUtils.getRelativeTimeSpanString(
                    firebaseTime.toDate().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString();

            holder.postDate.setText(relativeTimeString);
        } else {
            holder.postDate.setText("Just now"); // Fallback if comment was just posted
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, postDate, commentBodyText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.Username);
            postDate = itemView.findViewById(R.id.PostDate);
            commentBodyText = itemView.findViewById(R.id.commentBodyText);
        }
    }
}
