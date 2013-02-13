/**
 * Fractal View.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.fracplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Fractal extends View {
  private static final String TAG = "Fractal";
  private static int cores;

  private float prevX, prevY;
  private float oldDist;
  
  public enum TouchState { NONE, DRAG, ZOOM };
  private TouchState touchState = TouchState.NONE;
  
  private double xCenter = -0.7, yCenter = 0.0, magnification = 1.0;
  private double pixelX, pixelY;
  private double deltaX, deltaY;
  private double xMin, yMax;
  private int width;
  private static final int iterationLowerLimit = 128;
  private static final int iterationUpperLimit = 4096;
  private int iterationMax = iterationLowerLimit;
  private final double bailout = 128;
  private final double il = 1.0 / Math.log(2.0);
  private final double lp = Math.log(Math.log(bailout));
  private int[] table = new int[401];
  private Context appContext;

  public Fractal(Context context, AttributeSet attrs) {
    super(context, attrs);
    appContext = context; 

    cores = Runtime.getRuntime().availableProcessors();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        prevX = event.getX();
        prevY = event.getY();
        touchState = TouchState.DRAG;
        break;
       
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
        touchState = TouchState.NONE;
        break;
      
      case MotionEvent.ACTION_POINTER_DOWN:
        oldDist = spacing(event);
        if (oldDist > 10f) {
          touchState = TouchState.ZOOM;
        }  
        break;

      case MotionEvent.ACTION_MOVE:
        if (touchState == TouchState.DRAG) {
          xCenter -= (event.getX() - prevX) * pixelX;
          yCenter += (event.getY() - prevY) * pixelY;
        } else if (touchState == TouchState.ZOOM) {
          float newDist = spacing(event);
          if (newDist > 10f) {
            if (newDist > oldDist) {
              magnification *= 2.0;
              iterationMax = Math.min(iterationMax << 1, iterationUpperLimit);
            } else {
              magnification *= 0.5;
              iterationMax = Math.max(iterationMax >> 1, iterationLowerLimit);
            }
          }          
        }
        invalidate();
        break;
    }

    return true;
  }  
  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    width = canvas.getWidth();
    int height = canvas.getHeight();
    int[] colors = new int[width * height];
    // Scaling the pixel. For Mandelbrot set, -2.5 <= x <= 1, -1 << y << 1.
    deltaX = 3.5 / magnification;
    deltaY = deltaX * height / width;
    xMin = xCenter - deltaX / 2.0;
    yMax = yCenter + deltaY / 2.0;
    pixelX = deltaX / width;
    pixelY = deltaY / height;

    setupColorMapping();
    
    long now = System.currentTimeMillis();
    
    // Screen coordinate with (0,0) as (left, top) while (width - 1, height - 1) as 
    // (right, bottom).
    rectangle(0, 0, width - 1, height - 1, colors);
    
    for (int i = 0; i < width; ++i) {
      for (int j = 0; j < height; ++j) {
        //colors[j * width + i] = color(iteration(i, j));
      }
    }
    
    double timeDiff = (System.currentTimeMillis() - now) / 1000.0;
    
    canvas.drawBitmap(colors, 0, width, 0.0f, 0.0f, width, height, false, null);
        
    Log.v(TAG, "Used " + timeDiff + " seconds per draw.");
  }
  
  // Correct Java % behavior: returns negative when dividend is negative.
  private int mod(int a, int b) {
    int r = a % b;
    return (r < 0) ? r + b : r;
  }
  
  // Return the iteration count.
  private double iteration(int screenX, int screenY) {
    return mandelbrot(xMin + pixelX * screenX, yMax - pixelY * screenY, 
    									iterationMax, bailout, il, lp);
  }
  
  // Return black color value.
  private int black() {
  	return Color.rgb(0, 0, 0);
  }
  
  // Return true if iteration represents black color.
  private boolean blackIteration(double iteration) {
  	return iteration < 0;
  }
                         
  // Return color from iteration value.
  private int color(double iteration) {
  	return blackIteration(iteration) 
  				 ? black()
        	 : table[mod((int)(45.0 * Math.log(iteration)), 400)]; 
  }
   
  private void setupColorMapping() {
    // Gradient according to the parameter file shown in
    //   http://en.wikipedia.org/wiki/File:Mandel_zoom_00_mandelbrot_set.jpg

    // Control points.
    int cp[][] = new int[][] {
      // index  red  green  blue
      {      0,  13,     3,   69 }, // Added
      {     15,   3,     4,   83 }, // Added
      {     28,   0,     7,  100 },
      {     56,   5,    37,  158 }, // Added
      {     92,  32,   107,  203 },
      {    142, 144,   213,  242 }, // Added
      {    196, 237,   255,  255 },
      {    224, 248,   246,  191 }, // Added
      {    254, 253,   223,   67 }, // Added
      {    285, 255,   170,    0 },
      {    310, 225,   101,    5 }, // Added
      {    341, 136,    28,   22 }, // Added
      {    371,  49,     2,   48 },
      {    400,  69,     3,   12 }
    };
    int color[] = new int[3]; // For red, green, and blue.
    int n = 14;
    
    for (int i = 0; i < n; ++i) {
      table[cp[i][0]] = Color.rgb(cp[i][1], cp[i][2], cp[i][3]);
    }
    for (int i = 0; i < n - 1; ++i) {
      double x0 = cp[i][0], x1 = cp[i + 1][0];
      for (int j = cp[i][0] + 1; j < cp[i + 1][0]; ++j) {
        for (int k = 0; k < 3; ++k) {
          // Linear interpolation.
          color[k] = (int)(cp[i][k + 1] + (j - x0) 
                           * (cp[i + 1][k + 1] - cp[i][k + 1]) / (x1 - x0));
        }
        table[j] = Color.rgb(color[0], color[1], color[2]);
      }
    }
  }
  
  // We assume that if the edges of a rectangle are all black, 
  // then the content is all black (not sure whether this is true...)
  // We then recursively check by dividing the rectangle into four 
  // pieces.
  private void rectangle(int left, int top, int right, int bottom, int[] colors) {
  	// Nothing to draw.
  	if ((left > right) || (top > bottom)) {
  		return;
  	}
  	
  	// One line or two lines to draw.
  	if ((right <= left + 1) || (bottom <= top + 1)) {
  		for (int i = left; i <= right; ++i) {
  			for (int j = top; j <= bottom; ++j) {
  				colors[j * width + i] = color(iteration(i, j));
  			}
  		}
  		return;
  	}
  	
  	boolean allBlack = true; // TRUE if all edges are black.
  	// Draw top & bottom edges (including vertices).
  	for (int i = left; i <= right; ++i) {
  		double iteration = iteration(i, top);
  		if (!blackIteration(iteration)) {
  			allBlack = false;
  		}
  		colors[top * width + i] = color(iteration);
  		if (bottom > top) {
  			iteration = iteration(i, bottom);
    		if (!blackIteration(iteration)) {
    			allBlack = false;
    		}
    		colors[bottom * width + i] = color(iteration);
  		}
  	}
  	// Draw left & right edges.
  	for (int j = top + 1; j <= bottom - 1; ++j) {
  		double iteration = iteration(left, j);
  		if (!blackIteration(iteration)) {
  			allBlack = false;
  		}
  		colors[j * width + left] = color(iteration);
  		if (bottom > top) {
  			iteration = iteration(right, j);
    		if (!blackIteration(iteration)) {
    			allBlack = false;
    		}
    		colors[j * width + right] = color(iteration);
  		}
  	}  	
  	
  	if (allBlack) {
  		for (int i = left + 1; i < right; ++i) {
  			for (int j = top + 1; j < bottom; ++ j) {
  				colors[j * width + i] = black();
  			}
  		}
  		if ((right >= left + 10) && (bottom >= top + 10)) {
        Log.v(TAG, "All black (" + (right - left) + "," + (bottom - top) + ")");
  		}
  	} else {
  		// Split along the longer edge.
  		if (right - left > bottom - top) {
  			rectangle(left + 1, top + 1, (left + right) / 2, bottom - 1, colors);
  			rectangle((left + right) / 2 + 1, top + 1, right - 1, bottom - 1, colors);
  		} else {
  			rectangle(left + 1, top + 1, right - 1, (top + bottom) / 2, colors);
  			rectangle(left + 1, (top + bottom) / 2 + 1, right - 1, bottom - 1, colors);
  		}
  	}
  }
 
  // Calculate how far two fingers are.
  // From http://www.zdnet.com/blog/burnette/how-to-use-multi-touch-in-android-2-part-6-implementing-the-pinch-zoom-gesture/1847
  private float spacing(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return FloatMath.sqrt(x * x + y * y);
  }  

  public native double mandelbrot(double x0, double y0, int interationMax, 
                                  double bailout, double il, double lp);

  static {
    System.loadLibrary("mandelbrot");
  }
}
