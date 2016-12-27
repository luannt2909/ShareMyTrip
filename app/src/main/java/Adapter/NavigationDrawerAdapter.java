package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyentanluan.sharemytrip.R;

import java.util.Collections;
import java.util.List;

import Model.NavDrawerItem;

/**
 * Created by Nguyen Tan Luan on 11/9/2016.
 */

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.data = data;
        this.context = context;
        inflater=inflater.from(context);
    }
    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.nav_drawer_row,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.icon_nav.setImageResource(current.getIcon());
        holder.txtTitle.setText(current.getTitle());

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView icon_nav;
        public MyViewHolder(View itemView) {
            super(itemView);
            icon_nav=(ImageView)itemView.findViewById(R.id.icon_nav);
            txtTitle=(TextView)itemView.findViewById(R.id.txttitle);
        }
    }
}
