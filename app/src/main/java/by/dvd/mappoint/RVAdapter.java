package by.dvd.mappoint;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PointViewHolder> {

    public static class PointViewHolder extends RecyclerView.ViewHolder {

        CardView cv;

        TextView tvGroupItem, tvItem, tvItemDescription;
        ImageView ivImgPoint;
        TextView tvIdDB, tvDate;

        CheckBox checkBox;

        PointViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            tvGroupItem = (TextView)itemView.findViewById(R.id.group_item);
            tvItem = (TextView)itemView.findViewById(R.id.item);
            tvItemDescription = (TextView)itemView.findViewById(R.id.description);
            ivImgPoint = (ImageView)itemView.findViewById(R.id.img_point);

            tvIdDB = (TextView)itemView.findViewById(R.id.tvIdDB);
            tvDate = (TextView)itemView.findViewById(R.id.tvDate);

            checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
        }
    }

    List<Point> points;

    RVAdapter(List<Point> points){
        this.points = points;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PointViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        PointViewHolder pvh = new PointViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PointViewHolder pointViewHolder, final int i) {
        pointViewHolder.tvGroupItem.setText(points.get(i).groupItem);
        pointViewHolder.tvItem.setText(points.get(i).it);
        pointViewHolder.tvItemDescription.setText(points.get(i).descript);
        pointViewHolder.ivImgPoint.setImageResource(points.get(i).imgPoint);

        pointViewHolder.tvIdDB.setText(points.get(i).idDB);
        pointViewHolder.tvDate.setText(points.get(i).dateDB);

        MainActivity.chBox.add(pointViewHolder.checkBox);

        pointViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                for (CheckBox m : MainActivity.chBox){
                    if (m.isChecked()) {
                        m.setChecked(false);
                    }
                }

                if(isChecked) {
                    buttonView.setChecked(true);
                    MainActivity.ID_DB_ONCLICK = points.get(i).idDB;
                    String id = points.get(i).idDB;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return points.size();
    }
}