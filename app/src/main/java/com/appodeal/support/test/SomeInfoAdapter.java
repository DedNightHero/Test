package com.appodeal.support.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.List;

public class SomeInfoAdapter extends BaseAdapter {
    NativeAdViewNewsFeed nav_nf;
    NativeAd nativeAd;
    private LayoutInflater layoutInflater;
    private List<SomeInfo> someInfoList;

    public SomeInfoAdapter(Context context, List<SomeInfo> someInfoList) {
        this.someInfoList = someInfoList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return someInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return someInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null ){
            if (position%3==1){
                convertView = layoutInflater.inflate(R.layout.ad_item, parent, false);
                nativeAd = Appodeal.getNativeAds(1).get(0);
                nav_nf = convertView.findViewById(R.id.native_ad_view_news_feed);
                nav_nf.setNativeAd(nativeAd);
            }
            else {
                convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
            }
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SomeInfo someInfo = someInfoList.get(position);
        try{
            viewHolder.nameView.setText(someInfo.getName());
            viewHolder.descriptionView.setText(someInfo.getDescription());
        } catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView nameView, descriptionView;

        ViewHolder(View view) {
            nameView = view.findViewById(R.id.name);
            descriptionView = view.findViewById(R.id.description);
        }
    }
}