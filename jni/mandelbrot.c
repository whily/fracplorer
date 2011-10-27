/**
 * C file to calculate Mandelbrot count.
 *
 * Author:  
 *   Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011 Yujian Zhang
 */

#include <math.h>
#include <jni.h>

/* Return true if point (x, y) is within period-2 bulb. */
int insidePeriod2Bulb(double x, double y);

/* Return true if point (x, y) is within main cardioid. */
int insideCardioid(double x, double y);

/* Return -1 if maxIteration is reached, otherwise, smoothed iteration count. */
jdouble Java_net_whily_android_fracplorer_Fractal_mandelbrot
    (JNIEnv* env, jobject thiz, jdouble x0, jdouble y0,
     jint iterationMax, jdouble bailout, jdouble il, jdouble lp) {
  int inSet = -1;
  double ZERO = 0.0;
  double x = 0.0, y = 0.0;
  int iteration = 0;
		
  /* Periodicity checking according to:
     http://en.wikipedia.org/wiki/User:Simpsons_contributor/periodicity_checking */
  int check = 3;
  int checkCounter = 0;
  int update = 10;
  int updateCounter = 0;
  double hx = 0.0;
  double hy = 0.0;

  if (insideCardioid(x0, y0) || insidePeriod2Bulb(x0, y0))
    return inSet;

  while ((x * x + y * y < bailout) && (iteration < iterationMax)) {
    double xtemp = x * x - y * y + x0;
    y = 2.0 * x * y + y0;
    x = xtemp;
    iteration++;
			
    /* Periodicity checking. */
    double xDiff = fabs(x - hx);
    if (xDiff < ZERO) {
      double yDiff = fabs(y - hy);
      if (yDiff < ZERO) {
        return inSet;
      }
    }

    /* Update history. */
    if (check == checkCounter) {
      checkCounter = 0;

      /* Double the value of check. */
      if (update == updateCounter) {
        updateCounter = 0;
        check *= 2;
      }
      updateCounter++;

      hx = x;
      hy = y;
    } 
    checkCounter++;			
  }

  double modulus = sqrt(x* x + y * y);
  /* Continuous coloring used by Ultrafractal as described in
     http://www.fractalforums.com/programming/what-rangeprecision-for-fractional-escape-counts-for-mandelbrotjulia-sets/ */
  double mu = 1.0 * (iteration + il * lp - il * log(log(modulus)));
	  
  return (iteration == iterationMax) ? inSet : mu;
}
	
int insidePeriod2Bulb(double x, double y) {
  /* Algorithm from http://en.wikipedia.org/wiki/Mandelbrot_set#Optimizations */
  double x1 = x + 1.0;
  return x1 * x1  + y * y < 0.0625;
}
	
int insideCardioid(double x, double y) {
  /* Algorithm from http://en.wikipedia.org/wiki/Mandelbrot_set#Optimizations */
  double x1 = x - 0.25, yy = y * y;
  double q = x1 * x1 + yy;
  return q * (q + x1) < 0.25 * yy;
}
