package com.namseox.st040_mecut.utils;

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
import com.namseox.st040_mecut.view.customview.OptiBackgroundExecutor;
import com.namseox.st040_mecut.view.customview.OptiUiThreadExecutor;

import java.util.Objects;

public class OptiCutVideo extends View {
    private Uri mVideoUri;
    private int mHeightView;
    float margin;
    private LongSparseArray<Bitmap> mBitmapList = new LongSparseArray<>();
    Drawable drawableLeft;
    Drawable drawableRight;


    public OptiCutVideo(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptiCutVideo(@NonNull Context context) {
        this(context, null, 0);
    }

    public OptiCutVideo(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        drawableRight = Objects.requireNonNull(context.getDrawable(R.drawable.ic_left_cut_video));
        drawableLeft = Objects.requireNonNull(context.getDrawable(R.drawable.ic_right_cut_video));
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
    int checkFirst = 0;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (checkFirst != 2) {
            checkFirst += 1;
            rightHandle = (long) getMeasuredWidth();
        }

        getBitmap(getMeasuredWidth());

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

                                                           numThumbs = ((viewWidth) / thumbWidth);

                                                           long interval = videoLengthInMs / numThumbs;
                                                           numThumbs = (int) (videoLengthInMs / interval);
                                                           margin = viewWidth - numThumbs * thumbWidth;


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
                        mBitmapList.clear();
                        mBitmapList = thumbnailList;
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                        layoutParams.height = mHeightView + getContext().getResources().getDimensionPixelOffset(R.dimen._dp4);
                        layoutParams.width = mHeightView * numThumbs + getContext().getResources().getDimensionPixelOffset(R.dimen._dp24);
                        layoutParams.setMargins((int) (margin / 2), 0, (int) (margin / 2), 0);
                        setLayoutParams(layoutParams);
                        checkUpdate = true;

                        invalidate();
                    }
                }
                , 0L);
    }

    public Long leftHandle = 0L;
    public Long leftLine = 0L;
    Boolean checkLeft = false;
    public Long rightHandle = 0L;
    Boolean checkRight = false;
    Boolean checkLine = false;
    private Paint playedStatePainting = new Paint();
    Boolean checkUpdate = false;

    {
        playedStatePainting.setAntiAlias(true);
        playedStatePainting.setColor(ContextCompat.getColor(getContext(), R.color.color_transparen));
        playedStatePainting.setStyle(Paint.Style.FILL);
    }

    @SuppressLint("DrawAllocation")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (mVideoUri != null) {
            checkUpdate = false;
            if (mBitmapList != null) {
                float x = dp(11);
                for (int i = 0; i < mBitmapList.size(); i++) {
                    Bitmap bitmap = mBitmapList.get(i);

                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, x + getContext().getResources().getDimensionPixelOffset(R.dimen._dp2) - clipOffsetX, getContext().getResources().getDimensionPixelOffset(R.dimen._dp2), null);
                        x = x + bitmap.getWidth();
                    }
                }
                Bitmap xxx = drawableToBitmap(drawableLeft);
                Bitmap yyy = drawableToBitmap(drawableRight);

                Path path = new Path();
//                RectF rectF = new RectF(
//                        xxx.getWidth() + dp(1f) + leftHandle,
//                        dp(2),
//                        rightHandle + dp(0) - yyy.getWidth(),
//                        mHeightView + dp(2)
//                );

//rect trai
                RectF rectFLeft = new RectF(
                        xxx.getWidth(),
                        dp(2),
                        leftHandle + dp(0)  + xxx.getWidth(),
                        mHeightView + dp(2)
                );
                path.addRoundRect(rectFLeft, 0, 0, Path.Direction.CW);
//                Log.d(TAG, "onDraw...........: "+(rightHandle-yyy.getWidth())   + "..."+(getMeasuredWidth()-yyy.getWidth()));
//rect phai
                RectF rectFRight = new RectF(
                        rightHandle-yyy.getWidth(),
                        dp(2),
                        getMeasuredWidth()-yyy.getWidth() ,
                        mHeightView + dp(2)
                );
                path.addRoundRect(rectFRight, 0, 0, Path.Direction.CW);

//xam trai
                canvas.drawRect(xxx.getWidth() + dp(0), 0, leftHandle + xxx.getWidth() + dp(1), mHeightView + dp(2), playedStatePainting);
                //xam phai
                canvas.drawRect(rightHandle + dp(2) - yyy.getWidth(), 0, getMeasuredWidth() - yyy.getWidth() + dp(1), mHeightView + dp(2), playedStatePainting);


                Paint note = new Paint();
                note.setStrokeWidth(dp(2f));
                note.setAntiAlias(true);
                note.setColor(ContextCompat.getColor(getContext(), R.color.white));
                note.setStyle(Paint.Style.STROKE);
                note.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawPath(path, note);
                canvas.drawBitmap(
                        xxx,
                        leftHandle+xxx.getWidth(),
                        mHeightView / 2 - xxx.getHeight() / 2 + dp(2),
                        null
                );
                canvas.drawBitmap(
                        yyy,
                        rightHandle + dp(0) - yyy.getWidth()*2,
                        mHeightView / 2 - xxx.getHeight() / 2 + dp(4),
                        null
                );

                Paint notPlayedStatePainting = new Paint();

                {
                    notPlayedStatePainting.setStrokeWidth(2f);
                    notPlayedStatePainting.setAntiAlias(true);
                    notPlayedStatePainting.setShader(new LinearGradient(
                            leftLine , 0f, leftLine  + dp(2), (float) mHeightView,
                            new int[]{
                                    ContextCompat.getColor(getContext(), R.color.startColor),
                                    ContextCompat.getColor(getContext(), R.color.endColor),
                                    ContextCompat.getColor(getContext(), R.color.startColor),
                                    ContextCompat.getColor(getContext(), R.color.endColor)
                            },
                            new float[]{0f, 0.25f, 0.75f, 1f},
                            Shader.TileMode.CLAMP
                    ));
                }
//                if (leftLine < rightHandle-xxx.getWidth()-dp(2)) {

                canvas.drawRect( leftLine+xxx.getWidth(), dp(2), leftLine +xxx.getWidth() + dp(2), mHeightView + dp(2), notPlayedStatePainting);



//                }else {
//                    canvas.drawRect(rightHandle-xxx.getWidth()-dp(2), +dp(2), rightHandle-xxx.getWidth()-dp(0), mHeightView + dp(2), notPlayedStatePainting);
//
//                }
            }
        } else {
            setVisibility(View.GONE);
        }

    }

    public boolean isSelected = false;
    public boolean isCheck = false;
    public int clipOffsetX = 0;
    public int clipOffsetY = 0;

    public interface OnCustomTouchListener {
        void onTouchEvent(MotionEvent event);
        void onChangeLine(float i);
    }

    public void setOnCustomTouchListener(OnCustomTouchListener listener) {
        this.listener = listener;
    }

    private OnCustomTouchListener listener;

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (event.getX() > leftHandle - dp(30f) && event.getX() < leftHandle + dp(30f)) {
                            checkRight = false;
                            checkLine = false;
                            checkLeft = true;
                        }
                        if (event.getX() > rightHandle - dp(30f) && event.getX() < rightHandle + dp(30f)) {
                            checkLeft = false;
                            checkLine = false;
                            checkRight = true;
                        }
                        if (event.getX() > leftLine - dp(0f) && event.getX() < leftLine + dp(30f)) {
                            checkLeft = false;
                            checkRight = false;
                            checkLine = true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        checkLeft = false;
                        checkRight = false;
                        checkLine = false;
                        listener.onTouchEvent(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (checkLeft) {
                            if (event.getX() < rightHandle - dp(30f) && event.getX() > 0) {
                                changeLeft(event.getX());
                            }
                        } else {
                            if (checkRight) {
                                if (event.getX() > leftHandle + dp(30f) && event.getX() < getMeasuredWidth()) {
                                    changeRight(event.getX());
                                }
                            } else {
                                if (checkLine) {
                                    if (event.getX() > 0+ dp(8f) && event.getX() < getMeasuredWidth()-dp(8) ) {
                                        changeLine(event.getX());
                                        listener.onChangeLine(event.getX());
                                    }
                                }
                            }
                        }

                        break;

                    default:
                        break;
                }
                return true; // Trả về true để thông báo rằng sự kiện đã được xử lý
            }
        });
    }

    public void changeLeft(float i) {
        leftHandle = (long) i;
        checkUpdate = true;
        invalidate();
    }

    public void changeRight(float i) {
        rightHandle = (long) i;
        checkUpdate = true;
        invalidate();
    }

    public void changeLine(float i) {
        leftLine = (long) i;
        checkUpdate = true;
        invalidate();
    }

    public void updateWhenSelect(Boolean check) {
        isSelected = check;
        checkUpdate = true;
        Log.d(TAG, "onMeasur_____e:5");
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
        rightHandle = (long) getMeasuredWidth();
        leftLine = 0l;
        leftHandle = 0l;
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
