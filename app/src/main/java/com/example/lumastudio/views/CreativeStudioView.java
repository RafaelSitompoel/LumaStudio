package com.example.lumastudio.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreativeStudioView extends View {

    private Paint paint;
    private Path currentPath;
    private final List<Path> paths = new ArrayList<>();
    private final List<Path> undonePaths = new ArrayList<>();
    private Bitmap backgroundBitmap;

    public CreativeStudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#E8A0BF"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(12f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        currentPath = new Path();
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        this.backgroundBitmap = bitmap;
        invalidate();
    }

    public void setBrushColor(int color) {
        paint.setColor(color);
    }

    public void undo() {
        if (!paths.isEmpty()) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (!undonePaths.isEmpty()) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }
        for (Path p : paths) {
            canvas.drawPath(p, paint);
        }
        canvas.drawPath(currentPath, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath.moveTo(x, y);
                undonePaths.clear();
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                paths.add(currentPath);
                currentPath = new Path();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public Bitmap exportBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth() > 0 ? getWidth() : 800, getHeight() > 0 ? getHeight() : 1066, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }
}