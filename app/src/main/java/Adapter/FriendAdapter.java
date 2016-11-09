package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyentanluan.sharemytrip.R;

import java.util.List;

import Model.User;

/**
 * Created by Nguyen Tan Luan on 11/9/2016.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    List<User> data;
    Context mContext;
    LayoutInflater inflater;

    public FriendAdapter(List<User> data, Context Context) {
        this.data = data;
        this.mContext = Context;
        inflater=inflater.from(mContext);
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_friend_row,parent,false);
        FriendViewHolder viewHolder=new FriendViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        User currentuser=data.get(position);
        holder.txtusername.setText(currentuser.getUserName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public class FriendViewHolder extends RecyclerView.ViewHolder{
        ImageView imgavatar;
        TextView txtusername;
        public FriendViewHolder(View itemView) {
            super(itemView);
            imgavatar=(ImageView)itemView.findViewById(R.id.imgfriendavatar);
            txtusername=(TextView)itemView.findViewById(R.id.txtfriendname);
        }
    }
}
