package mx.softux.ecobike;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;


public class StationsMapActivity extends StationsActivity implements ObservableScrollView.Callbacks {
    private static final String TAG = StationsMapActivity.class.getSimpleName();

    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;

    private float mMaxHeaderElevation;

    private ObservableScrollView mScrollView;
    private View mHeaderBox;
    private View mDetailsContainer;
    private View mPhotoViewContainer;

    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_map);

        final Toolbar toolbar = getActionBarToolbar();
//        toolbar.setNavigationIcon(R.drawable.ic_up);

        mMaxHeaderElevation = getResources().getDimensionPixelSize(R.dimen.session_detail_max_header_elevation);

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_session);
        mPhotoViewContainer = findViewById(R.id.session_photo_container);

        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Rect bound = new Rect();
                mPhotoViewContainer.getGlobalVisibleRect(bound);
                if (event.getAction() != MotionEvent.ACTION_UP && bound.contains((int) event.getX(), (int) event.getY())) {
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                    return false;
                } else {
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScrollView == null) {
            return;
        }

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            recomputePhotoAndScrollingMetrics();
        }
    };


    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = mScrollView.getHeight() - mHeaderHeightPixels;
//        mPhotoHeightPixels = (int) (mPhotoViewContainer.getMeasuredWidth() / PHOTO_ASPECT_RATIO);

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
            mPhotoViewContainer.requestLayout();
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);
        mHeaderBox.setTranslationY(newTop);

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress = Math.min(Math.max(UIUtils.getProgress(scrollY, 0, mPhotoHeightPixels), 0), 1);
        }

        ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stations_map, menu);
        return true;
    }

    @Override
    public void onApiServiceConnected(ApiService apiService) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.stations_map_fragment);
        if (fragment instanceof ApiServiceConnection) {
            ((ApiServiceConnection) fragment).onApiServiceConnected(apiService);
        }
    }
}
