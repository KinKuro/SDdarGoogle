package studies.kinkuro.sddargoogle.Talk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import studies.kinkuro.sddargoogle.R;

/**
 * Created by alfo6-2 on 2018-05-18.
 */

public class TalkAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<TalkItem> items;

    public TalkAdapter(Context context, ArrayList<TalkItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        VH holder =new VH(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH)holder;
        TalkItem item = items.get(position);

        vh.tvNo.setText(item.getNo()+"번째 글");
        vh.tvTitle.setText(item.getTitle());
        vh.tvName.setText(item.getName());
        vh.tvMsg.setText(item.getMsg());
        vh.tvDate.setText(item.getDate());
        Glide.with(context).load(item.getImgUrl()).error(Glide.with(context).load(R.drawable.no_image)).into(vh.ivImg);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VH extends RecyclerView.ViewHolder{

        TextView tvNo, tvTitle, tvName, tvMsg, tvDate;
        ImageView ivImg;

        public VH(View itemView) {
            super(itemView);

            tvNo = itemView.findViewById(R.id.tv_no_talk);
            tvTitle = itemView.findViewById(R.id.tv_title_talk);
            tvName = itemView.findViewById(R.id.tv_name_talk);
            tvMsg = itemView.findViewById(R.id.tv_msg_talk);
            tvDate = itemView.findViewById(R.id.tv_date_talk);
            ivImg = itemView.findViewById(R.id.iv_img_talk);
        }
    }
}
