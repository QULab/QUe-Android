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
package org.que.activities.fragments;

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
import org.que.activities.R;

/**
 * Represents the Fragment for the map images.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class MapImageFragment extends Fragment implements View.OnTouchListener {

  /**
   * The argument key for the fragment.
   */
  public static final String ARG_MAP_IMAGE_FRAGMENT = "mapImageFrag";
  /**
   * The tag for the map image.
   */
  private static final String TAG_MAP_IMAGE = "mapImage";
  /**
   * The tag for the map bitmap.
   */
  private static final String TAG_MAP_BITMAP = "mapBitmap";
  /**
   * The drawable resource id.
   */
  private int imgRes = 0;
  /**
   * The bitmap of the drawable.
   */
  private Bitmap bitmap;
  /**
   * The image view which shows the drawable.
   */
  private ImageView image = null;

  /**
   * The enum TOUCH_MODE indicates the mode of the current touching.
   */
  private enum TOUCH_MODE {

    NONE, ZOOM, DRAG
  }
  /**
   * The current touch mode.
   */
  private TOUCH_MODE mode;
  /**
   * The new and old distance and scale which will be used for the scaling of
   * the image.
   */
  private float newDis, oldDis, scale;
  /**
   * The point which indicates the point of the finger on the screen.
   */
  private PointF p = new PointF();
  
  /**
   * The matrix which will be used to transform the bitmap.
   */
  private Matrix m = new Matrix();
  
  /**
   * The saved matrix which contains the pre state of the bitmap.
   */
  private Matrix saved = new Matrix();
  
  /**
   * The maximum of the zoom level.
   */
  private static final float MAX_ZOOM = 5f;
  
  /**
   * The minimum of the zoom level.
   */
  private static final float MIN_ZOOM = 1f;

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
      }
    });

    return rootView;
  }

  /**
   * Creates from the drawable resource id a bitmap object.
   * 
   * @return the bitmap object
   */
  private Bitmap createBitmap() {
    if (bitmap == null && imgRes != 0) {
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeResource(getResources(), imgRes, o);
      if (checkSizeIfRotationIsNeeded(o)) {
        bitmap = rotatedImage();
      }
      return scaleImage(image, o, bitmap);
    }
    return bitmap;
  }

  /**
   * Checks the dimensions of the bitmap if the with is larger than the height
   * the bitmap should be rotated.
   * 
   * @param o the bitmap options which contains the bitmap informations
   * @return true if rotations is needed, false otherwise
   */
  private boolean checkSizeIfRotationIsNeeded(BitmapFactory.Options o) {
    int w = o.outWidth;//bitm.getWidth();
    int h = o.outHeight;//bitm.getHeight();
    return w > h;
  }

  /**
   * Rotates the bitmap and returns the rotated bitmap, uses the drawable
   * resource id to create a bitmap.
   * 
   * @return the rotated bitmap
   */
  private Bitmap rotatedImage() {
    Matrix m = new Matrix();
    m.postRotate(270);

    Bitmap b = BitmapFactory.decodeResource(getResources(), imgRes);
    Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
    if (b != b2) {
      b.recycle();
    }
    return b2;
  }

  /**
   * Scales the bitmap to save memory and to show the hole image in the image view.
   * 
   * @param imageView the image view which shows the bitmap
   * @param o the bitmap options which contains the bitmap informations
   * @param btm the bitmap which should be scaled
   * @return the scaled bitmap
   */
  private Bitmap scaleImage(ImageView imageView, BitmapFactory.Options o, Bitmap btm) {

    int viewWidth = imageView.getWidth();
    int viewHeight = imageView.getHeight();
    int imgWidth = o.outWidth;//bitm.getWidth();
    int imgHeight = o.outHeight;//bitm.getHeight();

    float scaleX = viewWidth / (float) imgWidth;
    float scaleY = viewHeight / (float) imgHeight;
    
    if (btm == null) {
      btm = BitmapFactory.decodeResource(getResources(), imgRes);
    }

    Bitmap b2 = Bitmap.createScaledBitmap(btm, (int) (scaleX * imgWidth), (int) (scaleY * imgHeight), false);
    if (btm != b2) {
      btm.recycle();
    }
    return b2;

  }

  /**
   * From the android developer documentation:
   * 
   * "To tell the decoder to subsample the image, loading a smaller version into memory,
   * set inSampleSize to true in your BitmapFactory.Options object.
   * For example, an image with resolution 2048x1536 that is decoded with an
   * inSampleSize of 4 produces a bitmap of approximately 512x384. 
   * Loading this into memory uses 0.75MB rather than 12MB for the full image 
   * (assuming a bitmap configuration of ARGB_8888). 
   * Here’s a method to calculate a sample size value that is a power of two
   * based on a target width and height:"
   *
   * @see http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
   * @param options the bitmap options which contains the information of the bitmap
   * @param reqWidth the required width of the bitmap
   * @param reqHeight the required height of the bitmap
   * @return the sample size which should be used
   */
  public static int calculateInSampleSize(BitmapFactory.Options options,
                                          int reqWidth, int reqHeight) {
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

  /**
   * Clears the current bitmap.
   */
  public void clear() {
    bitmap.recycle();
    bitmap = null;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  /**
   * The onTouch method which will be called if an onTouch event appears.
   * 
   * @param view the view which was touched
   * @param event the touch event
   * @return true if the touch event was handled, false otherwise
   */
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
          m.postScale(scale, scale, p.x, p.y);
          float[] values = new float[9];
          m.getValues(values);
          float mscale = values[Matrix.MSCALE_X];
          if (mscale < MIN_ZOOM) {
            m.setScale(MIN_ZOOM, MIN_ZOOM, p.x, p.y);
          } else if (mscale > MAX_ZOOM) {
            m.setScale(MAX_ZOOM, MAX_ZOOM, p.x, p.y);
          }
        } else if (mode == TOUCH_MODE.DRAG) {
          m.set(saved);
          m.postTranslate(event.getX() - p.x, event.getY() - p.y);
        }
        consumed = true;
        break;
      default:
        consumed = false;

    }
    image.setImageMatrix(m);
    return consumed;
  }

  /**
   * Calculates the middle point between the first and second touch event.
   * 
   * @param p the first touch event
   * @param event the current touch event
   * @return the mid point between both touch events
   */
  private PointF calcMidPoint(PointF p, MotionEvent event) {
    float px = Math.abs(event.getX() - p.x) / 2;
    float py = Math.abs(event.getY() - p.y) / 2;
    float newx = event.getX() > p.x ? p.x + px : event.getX() + px;
    float newy = event.getY() > p.y ? p.y + py : event.getY() + py;
    p.set(newx, newy);
    return p;
  }

  /**
   * Calculates the euclidean distance between two points.
   * 
   * @param event the touch event which contains the point informations
   * @return the distance
   */
  private float euclideanDistance(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return FloatMath.sqrt(x * x + y * y);
  }
}
