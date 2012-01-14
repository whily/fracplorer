# NDK makefile.
#
# Author:
#   Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
#
# License: 
#   GNU General Public License v2
#   http://www.gnu.org/licenses/gpl-2.0.html
# Copyright (C) 2011-2012 Yujian Zhang

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := mandelbrot
LOCAL_SRC_FILES := mandelbrot.c
LOCAL_CFLAGS    += -Wall

include $(BUILD_SHARED_LIBRARY)
