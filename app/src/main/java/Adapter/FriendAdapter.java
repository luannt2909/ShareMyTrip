package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nguyentanluan.sharemytrip.Fragment_Home;
import com.example.nguyentanluan.sharemytrip.LoginActivity;
import com.example.nguyentanluan.sharemytrip.R;

import java.util.List;

import Model.Post;
import Model.User;
import Modules.OnPassData;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nguyen Tan Luan on 11/9/2016.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    String key;
    List<Post> data;
    Context mContext;
    LayoutInflater inflater;
    OnPassData listener;

    public FriendAdapter(List<Post> data, String key, Context Context, OnPassData listener) {
        this.data = data;
        this.key=key;
        this.mContext = Context;
        this.listener=listener;
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
        final Post currentuser=data.get(position);
        //holder.imgavatar.setImageBitmap(findUser());
        holder.txtusername.setText(currentuser.getAuthour());
        holder.txtdescription.setText(currentuser.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPassData(currentuser.getUrlRoad());
            }
        });
    }
    private Bitmap findUser(){
        User user= Fragment_Home.key_user.get(key);
        if(user!=null) {
            if (user.getUseravatar() != null)
                return LoginActivity.StringToBitMap(user.getUseravatar());
        }
        /*for (Map.Entry<String,Post> entry: Fragment_Friends.listpost.entrySet()) {
            if(entry.getValue().equals(post)) {
                int a=Fragment_Home.key_user.size();
                User user=Fragment_Home.key_user.get(entry.getKey());
                return user;
                //return Fragment_Home.key_user.get(entry.getKey());

            }
        }*/
        Bitmap bitmap= BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_profile);
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class FriendViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgavatar;
        TextView txtusername;
        TextView txtdescription;
        public FriendViewHolder(View itemView) {
            super(itemView);
            imgavatar=(CircleImageView)itemView.findViewById(R.id.imgfriendavatar);
            txtusername=(TextView)itemView.findViewById(R.id.txtfriendname);
            txtdescription=(TextView)itemView.findViewById(R.id.txtdescription);

        }
    }
}
