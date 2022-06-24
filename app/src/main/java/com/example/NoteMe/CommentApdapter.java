package com.example.NoteMe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommentApdapter extends RecyclerView.Adapter<CommentApdapter.CommentViewHolder> {
    private List<Comment> listComment;
    private ArrayList<String> tieude;
    private Context context;
    private CommentApdapter.updateclick mUpdateclick;
    public CommentApdapter(){};
    public interface updateclick {
        void onClickDelete(Comment comment);
    }


    public void setData(List<Comment> list, updateclick mUpdateclick) {
        this.listComment = list;
        this.mUpdateclick = mUpdateclick;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutcomment, parent, false);
        return new CommentViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull  CommentApdapter.CommentViewHolder holder, int position) {
        Comment comment = listComment.get(position);
        if (comment == null) {
            return;
        }
        holder.ten.setText(comment.getName());
        holder.sdt.setText("Nội dung: "+ comment.getNd());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateclick.onClickDelete(comment);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listComment != null) {
            return listComment.size();
        }
        return 0;

    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgavt, update, delete;

        private TextView ten, ngaysinh, sdt;
        public CommentViewHolder(@NonNull  View itemView) {
            super(itemView);
            imgavt = itemView.findViewById(R.id.avataruser);
            ten = itemView.findViewById(R.id.nameuser);
            ngaysinh = itemView.findViewById(R.id.tuoi);
            sdt = itemView.findViewById(R.id.sophone);
            delete = itemView.findViewById(R.id.delete);

        }
    }
}