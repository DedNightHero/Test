package com.appodeal.support.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

public class ListAdapter extends BaseAdapter {
    private List<NativeAd> nativeAds;
    private Context context;

    public ListAdapter(Context context, List<NativeAd> nativeAds) {
        this.nativeAds = nativeAds;
        this.context = context;
    }

    @Override
    public int getCount() {
        return nativeAds.size();
    }

    @Override
    public NativeAd getItem(int position) {
        return nativeAds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nav_nf = convertView.findViewById(R.id.native_ad_view_news_feed);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nav_nf.setNativeAd(getItem(position));

        return convertView;
    }

    private static class ViewHolder {
        private NativeAdViewNewsFeed nav_nf;
    }
}