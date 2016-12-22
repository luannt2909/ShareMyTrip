package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nguyentanluan.sharemytrip.R;

import java.util.List;

import Model.Post;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nguyen Tan Luan on 11/9/2016.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    List<Post> data;
    Context mContext;
    LayoutInflater inflater;

    public FriendAdapter(List<Post> data, Context Context) {
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
        Post currentuser=data.get(position);
        holder.txtusername.setText(currentuser.getAuthour());
        holder.txtdescription.setText(currentuser.getDescription());
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
