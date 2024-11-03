package com.namseox.st040_mecut.view.customview;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.collection.LongSparseArray;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.namseox.st040_mecut.R;

public class OptiTimeLineMergeView extends View {

    private Uri mVideoUri;
    private int mHeightView;
    private LongSparseArray<Bitmap> mBitmapList = null;

    public OptiTimeLineMergeView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptiTimeLineMergeView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHeightView = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minW, widthMeasureSpec, 1);

        final int minH = getPaddingBottom() + getPaddingTop() + mHeightView;
        int h = resolveSizeAndState(minH, heightMeasureSpec, 1);

        setMeasuredDimension(w, h);
    }

    int numThumbs = 0;

    @Override
    protected void onSizeChanged(final int w, int h, final int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (w != oldW) {
            getBitmap(w);
        }
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);

    }

    private void getBitmap(final int viewWidth) {
        OptiBackgroundExecutor.execute(new OptiBackgroundExecutor.Task("", 0L, "") {
                                           @SuppressLint("CheckResult")
                                           @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                           @Override
                                           public void execute() {
                                               try {
                                                   if (mVideoUri != null) {
                                                       LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();
                                                       try {
                                                           MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                                           mediaMetadataRetriever.setDataSource(getContext(), mVideoUri);

                                                           // Retrieve media data
                                                           long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                                                           // Set thumbnail properties (Thumbs are squares)
                                                           final int thumbWidth = mHeightView;
                                                           final int thumbHeight = mHeightView;

                                                           numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);

                                                           long interval = videoLengthInMs / numThumbs;
                                                           interval = 2000000;
                                                           numThumbs = (int) (videoLengthInMs / interval);

                                                           for (int i = 0; i < numThumbs; ++i) {
                                                               Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                                               try {
                                                                   bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
                                                               } catch (Exception e) {
                                                                   e.printStackTrace();
                                                               }
                                                               thumbnailList.put(i, bitmap);
                                                           }

                                                           mediaMetadataRetriever.release();
                                                           returnBitmaps(thumbnailList, numThumbs);
                                                       } catch (final Throwable e) {

                                                           Glide.with(getContext()).asBitmap().load(mVideoUri.toString()).into(new CustomTarget<Bitmap>() {
                                                               @Override
                                                               public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                   Bitmap bitmap = Bitmap.createScaledBitmap(resource, mHeightView, mHeightView, false);
                                                                   Log.d(TAG, "onResourceReady:0 " + bitmap);
                                                                   thumbnailList.put(0, bitmap);
                                                                   returnBitmaps(thumbnailList, 1);
                                                               }

                                                               @Override
                                                               public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                                   super.onLoadFailed(errorDrawable);
                                                                   Log.d(TAG, "onResourceReady: 30 ");

                                                               }

                                                               @Override
                                                               public void onLoadCleared(@Nullable Drawable placeholder) {
                                                                   Log.d(TAG, "onResourceReady: 2 ");
                                                               }
                                                           });
                                                       }

                                                   }

                                               } catch (final Throwable e) {
                                                   Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                               }
                                           }
                                       }
        );
    }

    private void returnBitmaps(final LongSparseArray<Bitmap> thumbnailList, int numThumbs) {
        OptiUiThreadExecutor.runTask("", new Runnable() {
                    @Override
                    public void run() {
                        if (!isCheck) {
                            isCheck = true;
                            mBitmapList = thumbnailList;
                            ViewGroup.LayoutParams layoutParams = getLayoutParams();
                            layoutParams.width = mHeightView * numThumbs + getContext().getResources().getDimensionPixelOffset(R.dimen._dp4);
                            layoutParams.height = mHeightView + getContext().getResources().getDimensionPixelOffset(R.dimen._dp4);
                            setLayoutParams(layoutParams);
                            invalidate();
                        }
                    }
                }
                , 0L);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
//        canvas.clipRect(0, clipOffsetY, canvas.getWidth(), canvas.getHeight());
        if (mVideoUri != null) {
            if (mBitmapList != null) {
                int x = 0;
                if (isSelected) {
                    Path path = new Path();
                    float radius = getContext().getResources().getDimension(R.dimen.corner_radius);  // Bo góc từ tài nguyên
                    RectF rectF = new RectF(getContext().getResources().getDimensionPixelOffset(R.dimen._dp2), 0, canvas.getWidth(), canvas.getHeight());
                    path.addRoundRect(rectF, radius, radius, Path.Direction.CW);
                    canvas.clipPath(path);
                    canvas.save();
                    setBackgroundResource(R.drawable.coner_white);
                } else {
                    canvas.save();
                    setBackgroundColor(Color.parseColor("#ffffff"));
                }

                for (int i = 0; i < mBitmapList.size(); i++) {
                    Bitmap bitmap = mBitmapList.get(i);

                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, x + getContext().getResources().getDimensionPixelOffset(R.dimen._dp2) - clipOffsetX, getContext().getResources().getDimensionPixelOffset(R.dimen._dp2), null);
                        x = x + bitmap.getWidth();
                    }
                }
            }
        } else {
            setVisibility(View.GONE);
        }

    }

    public boolean isSelected = false;
    public boolean isCheck = false;
    public int clipOffsetX = 0;
    public int clipOffsetY = 0;

    public void updateXY(int x, int y) {
        clipOffsetX = x;
        clipOffsetY = y;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = mHeightView * numThumbs + getContext().getResources().getDimensionPixelOffset(R.dimen._dp4) - clipOffsetX;
        layoutParams.height = mHeightView + getContext().getResources().getDimensionPixelOffset(R.dimen._dp4);
        setLayoutParams(layoutParams);

        invalidate();
    }

    public void updateWhenSelect(Boolean check) {
        isSelected = check;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return super.onTouchEvent(event);
    }

    public void setVideo(@NonNull Uri data) {
        mVideoUri = data;
    }
}
