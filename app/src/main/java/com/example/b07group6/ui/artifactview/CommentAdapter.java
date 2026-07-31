package com.example.b07group6.ui.artifactview;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07group6.R;
import com.example.b07group6.construct.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
    Context context;
    List<Comment> commentList;
    onCommentLongClickListener longClickListener;

    public interface onCommentLongClickListener {
        void onLongClick(int position);
    }
    public CommentAdapter(Context context, List<Comment> commentList, onCommentLongClickListener longClickListener) {
        this.context = context;
        this.commentList = commentList;
        this.longClickListener = longClickListener;
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
        Long timestamp = commentList.get(position).getTimestamp();
        // get time in relative format (e.g. 5 mins ago)
        if (timestamp != null) {
            Log.d("CommentsAdapter", "Setting timestamp");
            String relativeTimeString = DateUtils.getRelativeTimeSpanString(
                    timestamp,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString();
            holder.postDate.setText(relativeTimeString);
        } else {
            holder.postDate.setText("Just now"); // Fallback if comment was just posted
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int newPosition = holder.getBindingAdapterPosition();
                if (longClickListener != null && newPosition != RecyclerView.NO_POSITION) {
                    longClickListener.onLongClick(newPosition);
                }
                return true;
            }
        });
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
