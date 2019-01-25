package projects.android.acupuncturepoint.Views.DongyTriBenh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import projects.android.acupuncturepoint.Models.WebHTML.DongYTriBenh;
import projects.android.acupuncturepoint.R;

public class DongYTrIBenhAdapter  extends BaseAdapter {

    private List<DongYTriBenh> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public DongYTrIBenhAdapter(Context aContext, List<DongYTriBenh> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_item_vi_thuoc, null);
            holder = new ViewHolder();

            holder.vithuoc = (TextView) convertView.findViewById(R.id.nameVithuoc);
            holder.linkVithuoc = convertView.findViewById(R.id.linkVithuoc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DongYTriBenh dongYTriBenh = this.listData.get(position);
        holder.vithuoc.setText(dongYTriBenh.getName());
        holder.linkVithuoc.setText(dongYTriBenh.getLink());
        return convertView;
    }

    // Tìm ID của Image ứng với tên của ảnh (Trong thư mục mipmap).
    public int getMipmapResIdByName(String resName) {
        String pkgName = context.getPackageName();
        // Trả về 0 nếu không tìm thấy.
        int resID = context.getResources().getIdentifier(resName, "mipmap", pkgName);
        return resID;
    }

    static class ViewHolder {
        TextView vithuoc;
        TextView linkVithuoc;
    }


}
