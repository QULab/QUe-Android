/*
 * Copyright 2014 Quality and Usability Lab, Telekom Innvation Laboratories, TU Berlin..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tel.quenference.activities.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import de.tel.quenference.activities.R;

/**
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class MapImageFragment extends Fragment implements View.OnTouchListener {

    public static final String ARG_MAP_IMAGE_FRAGMENT = "mapImageFrag";
    private static final String TAG_MAP_IMAGE = "mapImage";
    private static final String TAG_MAP_BITMAP = "mapBitmap";
    private int imgRes;
    private Bitmap bitmap;
    private ImageView image = null;

    enum TOUCH_MODE {

        NONE, ZOOM, DRAG
    }

    private TOUCH_MODE mode;
    private float newDis, oldDis, scale;
    private PointF p = new PointF();

    public MapImageFragment() {
        imgRes = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            savedInstanceState = getArguments();
    }

        if (null != savedInstanceState) {
            imgRes = savedInstanceState.getInt(TAG_MAP_IMAGE);
            bitmap = savedInstanceState.getParcelable(TAG_MAP_BITMAP);
        }

        if (imgRes == 0) {
            imgRes = savedInstanceState.getInt(ARG_MAP_IMAGE_FRAGMENT);
    }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_image, container, false);


        image = (ImageView) rootView.findViewById(R.id.map_image);
        image.setOnTouchListener(this);

        image.post(new Runnable() {
            public void run() {
                if (bitmap == null) {
                    bitmap = createBitmap();
                }

                image.setImageBitmap(bitmap);
                startWidth = bitmap.getWidth();
                startHeight = bitmap.getHeight();

            }
        });

        return rootView;
    }

    private Bitmap createBitmap() {
        if (bitmap == null && imgRes != 0) {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imgRes, o);
            if (checkSizeIfRotationIsNeeded(o))
                bitmap = rotatedImage();
            return scaleImage(image, o, bitmap);
    }
        return null;
    }

    private boolean checkSizeIfRotationIsNeeded(BitmapFactory.Options o) {
        int w = o.outWidth;//bitm.getWidth();
        int h = o.outHeight;//bitm.getHeight();
        return w > h;
    }

    private Bitmap rotatedImage() {
        Matrix m = new Matrix();
        m.postRotate(270);

        Bitmap b = BitmapFactory.decodeResource(getResources(), imgRes);
        Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
        if (b != b2)
            b.recycle();
        return b2;
    }

    private Bitmap scaleImage(ImageView imageView, BitmapFactory.Options o, Bitmap btm) {

        int viewWidth = imageView.getWidth();
        int viewHeight = imageView.getHeight();
        int imgWidth = o.outWidth;//bitm.getWidth();
        int imgHeight = o.outHeight;//bitm.getHeight();

        float scaleX = viewWidth / (float) imgWidth;
        float scaleY = viewHeight / (float) imgHeight;

//    o.inSampleSize = calculateInSampleSize(o, viewWidth, viewHeight);
//    o.inJustDecodeBounds = false;
//    return BitmapFactory.decodeResource(getResources(), imgRes, o);
//    Bitmap b = BitmapFactory.decodeResource(getResources(), imgRes);
        if (btm == null)
            btm = BitmapFactory.decodeResource(getResources(), imgRes);

        Bitmap b2 = Bitmap.createScaledBitmap(btm, (int) (scaleX * imgWidth), (int) (scaleY * imgHeight), false);
        if (btm != b2)
            btm.recycle();
        return b2;

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
    }

        return inSampleSize;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TAG_MAP_BITMAP, bitmap);
        outState.putInt(TAG_MAP_IMAGE, imgRes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clear();
    }

    @Override
    public void onResume() {
//    if (bitmap == null && image != null)
//      bitmap = createBitmap();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void clear() {
        bitmap.recycle();
        bitmap = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private Matrix m = new Matrix();
    private Matrix saved = new Matrix();
    private static final float MAX_ZOOM = 5f;
    private static final float MIN_ZOOM = 1f;
    private int startWidth = 0;
    private int startHeight = 0;
    private float current_width = 0;
    private float current_height = 0;

    public boolean onTouch(View view, MotionEvent event) {
        Boolean consumed;
        ImageView image = (ImageView) view;
        switch (event.getAction() & MotionEvent.ACTION_MASK) //bitwise and with mask
        {
            case MotionEvent.ACTION_DOWN:
                saved.set(m);
                p.set(event.getX(), event.getY());
                mode = TOUCH_MODE.DRAG;
                consumed = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = TOUCH_MODE.NONE;
                consumed = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                saved.set(m);
                mode = TOUCH_MODE.ZOOM;
                oldDis = euclideanDistance(event);
                p = calcMidPoint(p, event);
                consumed = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == TOUCH_MODE.ZOOM) {
                    m.set(saved);
                    newDis = euclideanDistance(event);
                    scale = newDis / oldDis;
                    Log.d(MapImageFragment.class.getName(), "Scale: " + scale);
                    m.postScale(scale, scale, p.x, p.y);
                    float[] values = new float[9];
                    m.getValues(values);
                    float mscale = values[Matrix.MSCALE_X];
                    Log.d(MapImageFragment.class.getName(), "Matrix scale:\n " + mscale);
                    if (mscale < MIN_ZOOM) {
                        m.setScale(MIN_ZOOM, MIN_ZOOM, p.x, p.y);
                    } else if (mscale > MAX_ZOOM) {
                        m.setScale(MAX_ZOOM, MAX_ZOOM, p.x, p.y);
                    }
                    Log.d(MapImageFragment.class.getName(), "Matrix:\n " + m.toString());
                } else if (mode == TOUCH_MODE.DRAG) {
                    m.set(saved);
                    Log.d(MapImageFragment.class.getName(), "Point:\n " + p.x + "\t" + p.y);
                    Log.d(MapImageFragment.class.getName(), "Event:\n " + event.getX() + "\t" + event.getY());
                    Log.d(MapImageFragment.class.getName(), "Before Matrix:\n " + m.toString());
                    m.postTranslate(event.getX() - p.x, event.getY() - p.y);
                    Log.d(MapImageFragment.class.getName(), "After Matrix:\n " + m.toString());
                    Log.d(MapImageFragment.class.getName(), "Height image:\n " + startHeight);
                    Log.d(MapImageFragment.class.getName(), "Width image:\n " + startWidth);

        }
                consumed = true;
                break;
            default:
                consumed = false;

    }
        image.setImageMatrix(m);
        return consumed;
    }

    private PointF calcMidPoint(PointF p, MotionEvent event) {
        float px = Math.abs(event.getX() - p.x) / 2;
        float py = Math.abs(event.getY() - p.y) / 2;
        float newx = event.getX() > p.x ? p.x + px : event.getX() + px;
        float newy = event.getY() > p.y ? p.y + py : event.getY() + py;
        p.set(newx, newy);
        return p;
    }

    private float euclideanDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
}
