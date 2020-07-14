package android.Mentor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ANSHUL on 10-10-2017.
 */

public class instAppArrayAdapter extends ArrayAdapter<String> {

    Context context;
    List<String> name;
    List<Drawable> icon;
    List<TheApp> app;
    instAppArrayAdapter(Context context,List<String> str, List<TheApp> app)
    {
        super(context, -1,str);
        this.context=context;
        this.app=app;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("In adapter","HI");
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=layoutInflater.inflate(R.layout.inst_app_list_item,parent,false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        ImageView status = (ImageView) rowView.findViewById(R.id.imageView2);
        TextView time= (TextView) rowView.findViewById(R.id.Time);
        //textView.setText(name.get(position));
        textView.setText(app.get(position).getName());
        Log.d("text view text",app.get(position).getName());
        //imageView.setImageDrawable(icon.get(position));
        imageView.setImageDrawable(app.get(position).getIcon());
        time.setText("No usage data in last 24 hours");
        time.setTextColor(rowView.getResources().getColor( R.color.LawnGreen));
        if (!(app.get(position).getForgroundTime() ==0))
        {
            if(app.get(position).getMinute()==0 && app.get(position).getHour()==0 && app.get(position).getSecond()!=0)
                time.setText(app.get(position).getSecond()+" Seconds");
            else if (app.get(position).getHour()==0 &&app.get(position).getMinute()!=0)
                time.setText(app.get(position).getMinute()+" Minute "+app.get(position).getSecond()+" Seconds");
            else if (app.get(position).getHour()!=0)
                time.setText(app.get(position).getHour()+" Hour "+app.get(position).getMinute()+" Minute "+app.get(position).getSecond()+" Seconds");

            time.setTextColor(rowView.getResources().getColor( R.color.Gold));
        }

        if(!app.get(position).isLocked())
        status.setImageResource(R.drawable.ic_lock_open_black_24dp);
        else
            status.setImageResource(R.drawable.ic_lock_black_24dp);

        return rowView;
    }
}
