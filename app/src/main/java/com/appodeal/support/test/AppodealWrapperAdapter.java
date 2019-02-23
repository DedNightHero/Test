package com.appodeal.support.test;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeAdView;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.NativeIconView;
import com.appodeal.ads.NativeMediaView;

import java.util.List;

/**
 * Wrapper adapter to show Native Ad in recycler view with fixed step
 */
public class AppodealWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NativeCallbacks {

    private static final int DEFAULT_NATIVE_STEP = 5;

    private static final int VIEW_HOLDER_NATIVE_AD_TYPE = 600;


    private RecyclerView.Adapter<RecyclerView.ViewHolder> userAdapter;
    private int nativeStep = DEFAULT_NATIVE_STEP;

    private SparseArray<NativeAd> nativeAdList = new SparseArray<>();

    /**
     * @param userAdapter user adapter
     * @param nativeStep step show {@link com.appodeal.ads.NativeAd}
     */
    public AppodealWrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> userAdapter, int nativeStep) {
        this.userAdapter = userAdapter;
        this.nativeStep = nativeStep + 1;

        userAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();

                AppodealWrapperAdapter.this.notifyDataSetChanged();

                fillListWithAd();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                AppodealWrapperAdapter.this.notifyDataSetChanged();

                fillListWithAd();
            }
        });

        Appodeal.setNativeCallbacks(this);

        fillListWithAd();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_HOLDER_NATIVE_AD_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_native_ads, parent, false);
            return new NativeCustomAdViewHolder(view);
        } else {
            return userAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NativeCustomAdViewHolder) {
            ((NativeCustomAdViewHolder) holder).fillNative(nativeAdList.get(position));
        } else {
            userAdapter.onBindViewHolder(holder, getPositionInUserAdapter(position));
        }
    }

    @Override
    public int getItemCount() {
        int resultCount = 0;

        resultCount += getNativeAdsCount();
        resultCount += getUserAdapterItemCount();

        return resultCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isNativeAdPosition(position)) {
            return VIEW_HOLDER_NATIVE_AD_TYPE;
        } else {
            return userAdapter.getItemViewType(getPositionInUserAdapter(position));
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder instanceof NativeCustomAdViewHolder) {
            ((NativeCustomAdViewHolder) holder).unregisterViewForInteraction();
        }
    }

    /**
     * Destroy all used native ads
     */
    public void destroyNativeAds() {
        if (nativeAdList != null) {
            for (int i = 0; i < nativeAdList.size(); i++) {
                NativeAd nativeAd = nativeAdList.valueAt(i);
                nativeAd.destroy();
            }

            nativeAdList.clear();
        }
    }

    @Override
    public void onNativeLoaded() {
        fillListWithAd();
    }

    @Override
    public void onNativeFailedToLoad() {

    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {

    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {

    }

    @Override
    public void onNativeExpired() {

    }


    /**
     * @return count of loaded ads {@link com.appodeal.ads.NativeAd}
     */
    private int getNativeAdsCount() {
        if (nativeAdList != null) {
            return nativeAdList.size();
        }

        return 0;
    }

    /**
     * @return user items count
     */
    private int getUserAdapterItemCount() {
        if (userAdapter != null) {
            return userAdapter.getItemCount();
        }

        return 0;
    }

    /**
     * @param position index in wrapper adapter
     * @return {@code true} if item by position is {@link com.appodeal.ads.NativeAd}
     */
    private boolean isNativeAdPosition(int position) {
        return nativeAdList.get(position) != null;
    }

    /**
     * Method for searching position in user adapter
     * @param position index in wrapper adapter
     * @return index in user adapter
     */
    private int getPositionInUserAdapter(int position) {
        return position - Math.min(nativeAdList.size(), position / nativeStep);
    }

    /**
     * Method for filling list with {@link com.appodeal.ads.NativeAd}
     */
    private void fillListWithAd() {
        int insertPosition = findNextAdPosition();

        NativeAd nativeAd;
        while (canUseThisPosition(insertPosition) && (nativeAd = getNativeAdItem()) != null) {
            nativeAdList.put(insertPosition, nativeAd);
            notifyItemInserted(insertPosition);

            insertPosition = findNextAdPosition();
        }
    }

    /**
     * Get native ad item
     * @return {@link com.appodeal.ads.NativeAd}
     */
    @Nullable
    private NativeAd getNativeAdItem() {
        List<NativeAd> ads = Appodeal.getNativeAds(1);
        return !ads.isEmpty() ? ads.get(0) : null;
    }

    /**
     * Method for finding next position suitable for {@link com.appodeal.ads.NativeAd}
     * @return position for next native ad view
     */
    private int findNextAdPosition() {
        if (nativeAdList.size() > 0) {
            return nativeAdList.keyAt(nativeAdList.size() - 1) + nativeStep;
        }
        return nativeStep - 1;
    }

    /**
     * @param position index in wrapper adapter
     * @return {@code true} if you can add {@link com.appodeal.ads.NativeAd} to this position
     */
    private boolean canUseThisPosition(int position) {
        return nativeAdList.get(position) == null && getItemCount() > position;
    }


    /**
     * View holder for create custom {@link com.appodeal.ads.native_ad.views.NativeAdView}
     */
    static class NativeCustomAdViewHolder extends RecyclerView.ViewHolder {

        private NativeAdView nativeAdView;
        private TextView tvTitle;
        private TextView tvDescription;
        private RatingBar ratingBar;
        private Button ctaButton;
        private NativeIconView nativeIconView;
        private TextView tvAgeRestrictions;
        private NativeMediaView nativeMediaView;
        private FrameLayout providerViewContainer;

        NativeCustomAdViewHolder(View itemView) {
            super(itemView);

            nativeAdView = itemView.findViewById(R.id.native_item);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ratingBar = itemView.findViewById(R.id.rb_rating);
            ctaButton = itemView.findViewById(R.id.b_cta);
            nativeIconView = itemView.findViewById(R.id.icon);
            providerViewContainer = itemView.findViewById(R.id.provider_view);
            tvAgeRestrictions = itemView.findViewById(R.id.tv_age_restriction);
            nativeMediaView = itemView.findViewById(R.id.appodeal_media_view_content);
        }

        void fillNative(NativeAd nativeAd) {
            tvTitle.setText(nativeAd.getTitle());
            tvDescription.setText(nativeAd.getDescription());

            if (nativeAd.getRating() == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
            } else {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(nativeAd.getRating());
                ratingBar.setStepSize(0.1f);
            }

            ctaButton.setText(nativeAd.getCallToAction());

            View providerView = nativeAd.getProviderView(nativeAdView.getContext());
            if (providerView != null) {
                if (providerView.getParent() != null && providerView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) providerView.getParent()).removeView(providerView);
                }
                providerViewContainer.removeAllViews();
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                providerViewContainer.addView(providerView, layoutParams);
            }

            if (nativeAd.getAgeRestrictions() != null) {
                tvAgeRestrictions.setText(nativeAd.getAgeRestrictions());
                tvAgeRestrictions.setVisibility(View.VISIBLE);
            } else {
                tvAgeRestrictions.setVisibility(View.GONE);
            }

            if (nativeAd.containsVideo()) {
                nativeAdView.setNativeMediaView(nativeMediaView);
            } else {
                nativeMediaView.setVisibility(View.GONE);
            }


            nativeAdView.setTitleView(tvTitle);
            nativeAdView.setDescriptionView(tvDescription);
            nativeAdView.setRatingView(ratingBar);
            nativeAdView.setCallToActionView(ctaButton);
            nativeAdView.setNativeIconView(nativeIconView);
            nativeAdView.setProviderView(providerView);

            nativeAdView.registerView(nativeAd);
            nativeAdView.setVisibility(View.VISIBLE);
        }

        void unregisterViewForInteraction() {
            nativeAdView.unregisterViewForInteraction();
        }

    }
}