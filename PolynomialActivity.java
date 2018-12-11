package com.example.m.wielomian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;

import android.widget.Button;


import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class PolynomialActivity extends Activity {
    float a;
    float b;
    float c;
    float field;
    PolynomialView polynomialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polynomial);
        Intent intent = getIntent();
        a = intent.getIntExtra("A", 0);
        b = intent.getIntExtra("B", 0);
        c = intent.getIntExtra("C", 0);

        LinearLayout linearLayout = new LinearLayout(this);
        polynomialView = new PolynomialView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        polynomialView.setLayoutParams(layoutParams);

        Button buttonBack = new Button(this);
        buttonBack.setText("Back");
        linearLayout.addView(buttonBack);

        Button buttonSave = new Button(this);
        buttonSave.setText("Save");
        linearLayout.addView(buttonSave);

        linearLayout.addView(polynomialView);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linearLayout);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(PolynomialActivity.this, MainActivity.class);
                myIntent.putExtra("field", field);
                startActivity(myIntent);
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Bitmap polynomialViewImage = getBitmapFromView(polynomialView);
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                externalStorageDirectory = new File(externalStorageDirectory
                        , "polynomial" + System.currentTimeMillis() + ".png");
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(externalStorageDirectory);
                    polynomialViewImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                } catch (Exception e) {

                } finally {
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });

    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap output = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return output;
    }

    public class PolynomialView extends View {
        private Paint polynomialPaint;
        private Paint descriptionPaint;
        private int minXvalues = 4;
        private int width;
        private int height;
        float scaledX;
        int originX;
        float scaledY;
        int originY;

        float touchX;
        float touchY;

        public PolynomialView(Context context) {
            super(context);
            polynomialPaint = new Paint();
            polynomialPaint.setAntiAlias(true);
            polynomialPaint.setColor(Color.RED);
            polynomialPaint.setStrokeWidth(3);
            descriptionPaint = new Paint();
            descriptionPaint.setColor(Color.RED);
            descriptionPaint.setStrokeWidth(3);
            descriptionPaint.setTextSize(40);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            width = w;
            height = h;
            scale();
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //draw axes
            canvas.drawLine(originX, 0, originX, height, polynomialPaint);
            canvas.drawLine(0, originY, width, originY, polynomialPaint);
            //draw vertical axis arrow
            canvas.drawLine(originX, 0, originX - 10, 10, polynomialPaint);
            canvas.drawLine(originX, 0, originX + 10, 10, polynomialPaint);
            //draw horizontal axis arrow
            canvas.drawLine(width - 10, originY - 10, width, originY, polynomialPaint);
            canvas.drawLine(width - 10, originY + 10, width, originY, polynomialPaint);

            //draw x and y descriptions
            canvas.drawText("X", width - 40, originY - 40, descriptionPaint);
            canvas.drawText("Y", originX + 20, 0 + 30, descriptionPaint);
            //draw values on axes, horizontal right, horizontal left, vertical top, vertical bottom
            DrawAxis(canvas, polynomialPaint, originX, originY, scaledX, width, 1);
            DrawAxis(canvas, polynomialPaint, originX, originY, scaledX, width, -1);
            DrawAxis(canvas, polynomialPaint, originY, originX, scaledY, height, 1);
            DrawAxis(canvas, polynomialPaint, originY, originX, scaledY, height, -1);

            //draw function
            for (int i = 0; i < width; i++) {
                int value = Math.round(function((i - originX) * scaledX) / (-scaledY) + originY);
                canvas.drawPoint(i, value, polynomialPaint);
            }
        }

        private void DrawAxis(Canvas canvas, Paint paint,
                              int drawnAxisOrigin, int otherAxisOrigin,
                              float scaledPixel, int maxSize,
                              int multiplier) {
            int currentPoint = 0;
            for (int i = drawnAxisOrigin; isBetween(i, 0, height); i += multiplier * Math.round(1 / scaledY)) {
                if (maxSize == height) {
                    canvas.drawLine(otherAxisOrigin - 5, i, otherAxisOrigin + 5, i, paint);
                    canvas.drawText(Integer.toString(currentPoint), otherAxisOrigin + 7, i, paint);
                } else {
                    canvas.drawLine(i, otherAxisOrigin - 5, i, otherAxisOrigin + 5, paint);
                    canvas.drawText(Integer.toString(currentPoint), i, otherAxisOrigin - 7, paint);
                }
                currentPoint = currentPoint + 1 * multiplier;
            }
        }

        boolean isTouched = false;
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchX = eventX;
                    touchY = eventY;
                    //return true;
                    if (isBetween(originX,(int)(touchX *0.9),(int)(touchX*1.1))
                            && isBetween(originY,(int)(touchY *0.9),(int)(touchY*1.1))) {
                    isTouched = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchX = eventX;
                    touchY = eventY;
                    if(isTouched){
                        originX = (int) touchX;
                        originY = (int) touchY;
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    touchX = eventX;
//                    touchY = eventY;
                    isTouched = false;
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }

        private float function(float x) {
            return a * x * x + b * x + c;
        }

        private boolean isBetween(int value, int min, int max) {
            return value >= min && value <= max;
        }

        private float delta() {
            return b * b - 4 * a * c;
        }

        private void scale() {
            float delta = delta();
            float peakX = 0;
            if (a!=0) peakX = -b / (2 * a);
            float maxX1 = peakX - minXvalues;
            float maxX2 = peakX + minXvalues;
            if (peakX > minXvalues) {
                maxX1 = -1;
                maxX2 = 2 * peakX + 1;
            }
            if (peakX < -minXvalues) {
                maxX1 = 2 * peakX - 1;
                maxX2 = 1;
            }
            float localWidth = maxX2 - maxX1;
            scaledX = localWidth / width;
            originX = Math.round(Math.abs(maxX1) / scaledX);

            float peakY = 0;
            if(a!=0) peakY = -delta / (4 * a);
            float maxY1 = function(maxX1);
            float upValue;
            float downValue;
            if (delta <= 0) {
                if (a < 0) {
                    upValue = 1;
                    downValue = maxY1;
                } else {
                    upValue = maxY1;
                    downValue = -1;
                }
            } else {
                if (a < 0) {
                    upValue = peakY + 1;
                    downValue = maxY1;
                } else {
                    upValue = maxY1;
                    downValue = peakY - 1;
                }
            }
            float localHeight = upValue - downValue;
            scaledY = localHeight / height;
            originY = Math.round(upValue / scaledY);
//            if (delta <= 0) {
//                field = 0;
//            } else {
//
////                float x1 = (float) (-b + Math.sqrt(delta)) / (2 * a);
////                float y1 = integral(x1);
////                float x2 = (float) (-b - Math.sqrt(delta)) / (2 * a);
////                float y2 = integral(x2);
////                field = y1 - y2;
//            }
        }

//        private float integral(float x) {
//            return (float) ((1 / 3) * a * x * x * x + 0.5 * b * x * x + c * x);
//        }
    }
}