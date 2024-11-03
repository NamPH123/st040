package com.namseox.st040_mecut.view.customview;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.namseox.st040_mecut.R;

public class OptiTimeLineView extends View {
    private Uri mVideoUri;
    private int mHeightView;
    private LongSparseArray<Bitmap> mBitmapList = null;
    public Long leftHandle = 0L;
    public Long rightHandle = 0L;
    public ScaleGestureDetector scaleGestureDetector;
    float scaleFactor = 0;


    public OptiTimeLineView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public OptiTimeLineView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (isSelected) {
                    scaleFactor = detector.getScaleFactor();
//                    if(scaleFactor<1){
//                        scaleFactor = scaleFactor-0.03f;
//                    }
                    if (scaleFactor > 1 && getWidth() < 8) {
                        updateXY(12);
                    } else {
                        updateXY((int) (getWidth() * scaleFactor - dp(4)));
                    }
                }
                return true;
            }
        });
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

    Bitmap bitmapDefault;
    public Bitmap getBitmapDefault() {
        Canvas canvas2 = new Canvas(bitmapDefault);
        canvas2.drawBitmap(bitmapDefault, 0, 0, purplePaint);
        return bitmapDefault;
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
                                                                   bitmapDefault = resource;
                                                                   Bitmap bitmap = Bitmap.createScaledBitmap(resource, mHeightView, mHeightView, false);
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

    Paint purplePaint = new Paint();

    public void setPaint(Paint paint) {
        purplePaint = paint;
        invalidate();
    }

    Bitmap bitmapOut;

    public Bitmap getBitmap() {
        return bitmapOut;
    }

    @SuppressLint("DrawAllocation")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (mVideoUri != null) {
            if (mBitmapList != null) {
                bitmapOut = mBitmapList.get(0);
                canvas.drawBitmap(bitmapOut, 0, 0, purplePaint);
                int x = 0;
                Path path = new Path();
                if (isSelected) {
                    float radius = getContext().getResources().getDimension(R.dimen.corner_radius);  // Bo góc từ tài nguyên
                    RectF rectF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
                    path.addRoundRect(rectF, radius, radius, Path.Direction.CW);
                    canvas.clipPath(path);
                    canvas.save();
                    Bitmap bitmap = mBitmapList.get(0);
                    if (bitmap != null) {
                        while (x < getWidth()) {
                            canvas.drawBitmap(bitmap, x + getContext().getResources().getDimensionPixelOffset(R.dimen._dp2), getContext().getResources().getDimensionPixelOffset(R.dimen._dp2), purplePaint);
                            x = x + bitmap.getWidth();
                        }
                    }
                    Paint note = new Paint();
                    note.setStrokeWidth(getContext().getResources().getDimensionPixelOffset(R.dimen._dp4));
                    note.setAntiAlias(true);
                    note.setShader(new LinearGradient(
                            0, 0f, getWidth() + dp(2), (float) mHeightView,
                            new int[]{
                                    ContextCompat.getColor(getContext(), R.color.startColor),
                                    ContextCompat.getColor(getContext(), R.color.endColor),
                                    ContextCompat.getColor(getContext(), R.color.startColor),
                                    ContextCompat.getColor(getContext(), R.color.endColor)
                            },
                            new float[]{0f, 0.25f, 0.75f, 1f},
                            Shader.TileMode.CLAMP
                    ));
                    note.setStyle(Paint.Style.STROKE);
                    note.setStrokeCap(Paint.Cap.ROUND);
                    canvas.drawPath(path, note);

                } else {
                    RectF rectF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
                    path.addRoundRect(rectF, 0, 0, Path.Direction.CW);
                    canvas.clipPath(path);
                    canvas.save();
                    Bitmap bitmap = mBitmapList.get(0);
                    if (bitmap != null) {
                        while (x < getWidth()) {
                            canvas.drawBitmap(bitmap, x + getContext().getResources().getDimensionPixelOffset(R.dimen._dp2), getContext().getResources().getDimensionPixelOffset(R.dimen._dp2), purplePaint);
                            x = x + bitmap.getWidth();
                        }
                    }
                    Paint note = new Paint();
                    note.setStrokeWidth(getContext().getResources().getDimensionPixelOffset(R.dimen._dp4));
                    note.setAntiAlias(true);
                    note.setColor(ContextCompat.getColor(getContext(), R.color.white));
                    note.setStyle(Paint.Style.STROKE);
                    note.setStrokeCap(Paint.Cap.ROUND);
                    canvas.drawPath(path, note);
                }


            }
        } else {
            setVisibility(View.GONE);
        }

    }

    public boolean isSelected = false;
    public boolean isCheck = false;
    public int clipOffsetX = 0;

    public void updateXY(int x) {
        clipOffsetX = x;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = mHeightView * numThumbs + getContext().getResources().getDimensionPixelOffset(R.dimen._dp4) + clipOffsetX;
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
//        if(isSelected){
//            scaleGestureDetector.onTouchEvent(event);
//        }
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

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 1;
        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 1;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private float dp(float value) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }
}
