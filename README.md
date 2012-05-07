Fracplorer
==========

Fracplorer is an Android application to explore Fractal images (mainly
[Mandelbrot set](http://en.wikipedia.org/wiki/Mandelbrot_set)) for
now.  It is currently under development.

For more information about Fracplorer, please go to
  <https://github.com/whily/fracplorer>

Wiki pages can be found at
  <https://wiki.github.com/whily/fracplorer>

Installation
------------

Development
-----------

The following tools are needed to build Fracplorer from source:

* JDK version 6/7 from <http://www.java.com> if Java is not available. 
  Note that JDK is preinstalled on Mac OS X and available via package manager
  on many Linux systems. 
* Android SDK r19 and NDK r7.
* [Inkscape](http://inkscape.org) and [ImageMagick](http://www.imagemagick.org)
  to generate icons.

Fracplorer can be built with either [Ant] (http://en.wikipedia.org/wiki/Apache_Ant)
or Eclipse. For Eclipse, simply import the project. For Ant, type the following
commands at the project directory checked out (assuming debug version):

1. android update project -p .
2. ./genart
3. ant debug

License
-------

Fracplorer is released under GNU General Public License v2, whose information
is available at:
  <http://www.gnu.org/licenses/gpl-2.0.html>

