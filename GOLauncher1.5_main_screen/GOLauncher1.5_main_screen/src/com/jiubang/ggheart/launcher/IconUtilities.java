/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.jiubang.ggheart.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherLogicProxy;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.FastBitmapDrawable;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;

/**
 * Various utilities shared amongst the Launcher's classes.
 */
public final class IconUtilities {
	private static int sIconWidth = -1;
	private static int sIconHeight = -1;

	// private final static int DEFAULTICONSIZE = 72; //默认图标数据大小

	private static final Paint sPAINT = new Paint();
	private static final Rect sBOUNDS = new Rect();
	private static final Rect sOLDBOUNDS = new Rect();
	private static Canvas sCanvas = new Canvas();
	
	private static Paint sPaint = null;
	private static Matrix sMatrix = null;
	private static PorterDuffXfermode sXfermode = null; //实现遮罩层 (mask)

	static {
		sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
	}

	/**
	 * 初始化图标大小
	 * 
	 * @param context
	 * @author luopeihuan
	 */
	private static void initIconSize(Context context) {
		if (sIconWidth == -1) {
			sIconWidth = sIconHeight = getIconSize(context);
		}
	}
	
	/**
	 * <br>功能简述:获取图标大小
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static int getIconSize(Context context) {
		int iconSize = 0;
		
		if (SettingProxy.getDesktopSettingInfo() != null) {
			DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
			iconSize = info.getIconRealSize();
		} else {
			final Resources resources = context.getResources();
			iconSize = (int) resources.getDimension(R.dimen.screen_icon_size);
			if (GoLauncherLogicProxy.isLargeIcon()) {
				iconSize = (int) resources
						.getDimension(R.dimen.screen_icon_large_size);
			} else if (Machine.isLephone()) {
				iconSize = Machine.LEPHONE_ICON_SIZE;
			}
		}
		return iconSize;
	}

	/**
	 * Returns a Drawable representing the thumbnail of the specified Drawable.
	 * The size of the thumbnail is defined by the dimension
	 * android.R.dimen.launcher_application_icon_size.
	 * 
	 * This method is not thread-safe and should be invoked on the UI thread
	 * only.
	 * 
	 * @param icon
	 *            The icon to get a thumbnail of.
	 * @param context
	 *            The application's context.
	 * 
	 * @return A thumbnail for the specified icon or the icon itself if the
	 *         thumbnail could not be created.
	 */
	public static Drawable createIconThumbnail(Drawable icon, Context context) {
		if (icon == null || context == null) {
			return null;
		}

		initIconSize(context);
		int width = sIconWidth;
		int height = sIconHeight;

		final Resources resources = context.getResources();

		float scale = 1.0f;
		if (icon instanceof PaintDrawable) {
			PaintDrawable painter = (PaintDrawable) icon;
			painter.setIntrinsicWidth(width);
			painter.setIntrinsicHeight(height);
		} else if (icon instanceof BitmapDrawable) {
			// Ensure the bitmap has a density.
			BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (null != bitmap && bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				bitmapDrawable.setTargetDensity(resources.getDisplayMetrics());
			}
		}
		int iconWidth = icon.getIntrinsicWidth();
		int iconHeight = icon.getIntrinsicHeight();

		if (width > 0 && height > 0) {
			if (width < iconWidth || height < iconHeight || scale != 1.0f) {
				final float ratio = (float) iconWidth / iconHeight;

				if (iconWidth > iconHeight) {
					height = (int) (width / ratio);
				} else if (iconHeight > iconWidth) {
					width = (int) (height * ratio);
				}

				final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE
						? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565;
				try {
					final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
					synchronized (sCanvas) {
						final Canvas canvas = sCanvas;
						canvas.setBitmap(thumb);
						// Copy the old bounds to restore them later
						// If we were to do oldBounds = icon.getBounds(),
						// the call to setBounds() that follows would
						// change the same instance and we would lose the
						// old bounds
						sOLDBOUNDS.set(icon.getBounds());
						final int x = (sIconWidth - width) / 2;
						final int y = (sIconHeight - height) / 2;
						icon.setBounds(x, y, x + width, y + height);
						icon.draw(canvas);
					}
					icon.setBounds(sOLDBOUNDS);
					icon = new BitmapDrawable(resources, thumb);
				} catch (OutOfMemoryError e) {
					OutOfMemoryHandler.handle();
					return null;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else if (iconWidth < width && iconHeight < height) {
				try {
					final Bitmap.Config c = Bitmap.Config.ARGB_8888;
					final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
					final float ratio = Math.min((float) width / iconWidth, (float) height
							/ iconHeight);
					final int scaledWidth = (int) (ratio * iconWidth);
					final int scaledHeight = (int) (ratio * iconHeight);

					synchronized (sCanvas) {
						sBOUNDS.set((sIconWidth - scaledWidth) / 2,
								(sIconHeight - scaledHeight) / 2, width, height);
						sOLDBOUNDS.set(0, 0, iconWidth, iconHeight);

						final Canvas canvas = sCanvas;
						final Paint paint = sPAINT;

						canvas.setBitmap(thumb);
						sOLDBOUNDS.set(icon.getBounds());
						final int x = (width - scaledWidth) / 2;
						final int y = (height - scaledHeight) / 2;
						icon.setBounds(x, y, x + scaledWidth, y + scaledHeight);
						icon.draw(canvas);

						paint.setDither(false);
						paint.setFilterBitmap(true);
					}
					icon.setBounds(sOLDBOUNDS);
					icon = new BitmapDrawable(resources, thumb);
				} catch (OutOfMemoryError e) {
					OutOfMemoryHandler.handle();
					return null;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		return icon;
	}

	public static Drawable createIconThumbnail(Drawable icon, int width, int height, Context context) {
		float scale = 1.0f;
		final Resources resources = context.getResources();
		if (icon instanceof PaintDrawable) {
			PaintDrawable painter = (PaintDrawable) icon;
			painter.setIntrinsicWidth(width);
			painter.setIntrinsicHeight(height);
		} else if (icon instanceof BitmapDrawable) {
			// Ensure the bitmap has a density.
			BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (null != bitmap && bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				bitmapDrawable.setTargetDensity(resources.getDisplayMetrics());
			}
		}
		int iconWidth = icon.getIntrinsicWidth();
		int iconHeight = icon.getIntrinsicHeight();

		if (width > 0 && height > 0) {
			if (width < iconWidth || height < iconHeight || scale != 1.0f) {
				final float ratio = (float) iconWidth / iconHeight;

				if (iconWidth > iconHeight) {
					height = (int) (width / ratio);
				} else if (iconHeight > iconWidth) {
					width = (int) (height * ratio);
				}

				final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE
						? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565;
				try {
					final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
					synchronized (sCanvas) {
						final Canvas canvas = sCanvas;
						canvas.setBitmap(thumb);
						// Copy the old bounds to restore them later
						// If we were to do oldBounds = icon.getBounds(),
						// the call to setBounds() that follows would
						// change the same instance and we would lose the
						// old bounds
						sOLDBOUNDS.set(icon.getBounds());
						final int x = (sIconWidth - width) / 2;
						final int y = (sIconHeight - height) / 2;
						icon.setBounds(x, y, x + width, y + height);
						icon.draw(canvas);
					}
					icon.setBounds(sOLDBOUNDS);
					icon = new BitmapDrawable(resources, thumb);
				} catch (OutOfMemoryError e) {
					OutOfMemoryHandler.handle();
					return null;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else if (iconWidth < width && iconHeight < height) {
				try {
					final Bitmap.Config c = Bitmap.Config.ARGB_8888;
					final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
					synchronized (sCanvas) {
						final Canvas canvas = sCanvas;
						canvas.setBitmap(thumb);
						sOLDBOUNDS.set(icon.getBounds());
						final int x = (width - iconWidth) / 2;
						final int y = (height - iconHeight) / 2;
						icon.setBounds(x, y, x + iconWidth, y + iconHeight);
						icon.draw(canvas);
					}
					icon.setBounds(sOLDBOUNDS);
					icon = new BitmapDrawable(resources, thumb);
				} catch (OutOfMemoryError e) {
					OutOfMemoryHandler.handle();
					return null;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		return icon;
	}

	/**
	 * Returns a Bitmap representing the thumbnail of the specified Bitmap. The
	 * size of the thumbnail is defined by the dimension
	 * android.R.dimen.launcher_application_icon_size.
	 * 
	 * This method is not thread-safe and should be invoked on the UI thread
	 * only.
	 * 
	 * @param bitmap
	 *            The bitmap to get a thumbnail of.
	 * @param context
	 *            The application's context.
	 * 
	 * @return A thumbnail for the specified bitmap or the bitmap itself if the
	 *         thumbnail could not be created.
	 */
	public static Bitmap createBitmapThumbnail(Bitmap bitmap, Context context) {
		initIconSize(context);
		int width = sIconWidth;
		int height = sIconHeight;

		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();

		if (width > 0 && height > 0) {
			if (width < bitmapWidth || height < bitmapHeight) {
				final float ratio = (float) bitmapWidth / bitmapHeight;

				if (bitmapWidth > bitmapHeight) {
					height = (int) (width / ratio);
				} else if (bitmapHeight > bitmapWidth) {
					width = (int) (height * ratio);
				}

				// Log.d("XViewFrame", "bitmap.getConfig() = " +
				// bitmap.getConfig());
				Bitmap.Config c = (width == sIconWidth && height == sIconHeight) ? bitmap
						.getConfig() : Bitmap.Config.ARGB_8888;
				if (null == c) {
					c = Bitmap.Config.ARGB_8888;
				}

				try {
					final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
					synchronized (sCanvas) {
						final Canvas canvas = sCanvas;
						final Paint paint = sPAINT;
						canvas.setBitmap(thumb);
						paint.setDither(false);
						paint.setFilterBitmap(true);
						sBOUNDS.set((sIconWidth - width) / 2, (sIconHeight - height) / 2, width,
								height);
						sOLDBOUNDS.set(0, 0, bitmapWidth, bitmapHeight);
						canvas.drawBitmap(bitmap, sOLDBOUNDS, sBOUNDS, paint);
					}
					return thumb;
				} catch (OutOfMemoryError e) {
					OutOfMemoryHandler.handle();
					return null;
				} catch (Throwable e) {
					e.printStackTrace();
				}

			} else if (bitmapWidth < width || bitmapHeight < height) {
				try {
					final Bitmap.Config c = Bitmap.Config.ARGB_8888;
					final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
					final float scale = Math.min((float) width / bitmapWidth, (float) height
							/ bitmapHeight);
					final int scaledWidth = (int) (scale * bitmapWidth);
					final int scaledHeight = (int) (scale * bitmapHeight);
					synchronized (sCanvas) {
						sBOUNDS.set((sIconWidth - scaledWidth) / 2,
								(sIconHeight - scaledHeight) / 2, width, height);
						sOLDBOUNDS.set(0, 0, bitmapWidth, bitmapHeight);

						final Canvas canvas = sCanvas;
						final Paint paint = sPAINT;
						canvas.setBitmap(thumb);
						paint.setDither(false);
						paint.setFilterBitmap(true);
						canvas.drawBitmap(bitmap, sOLDBOUNDS, sBOUNDS, paint);
					}
					return thumb;
				} catch (OutOfMemoryError e) {
					OutOfMemoryHandler.handle();
					return null;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		return bitmap;
	}

	/**
	 * @param icon
	 * @param context
	 * @return
	 */
	public static Drawable drawReflection(Drawable icon, Context context) {
		initIconSize(context);

		// The gap we want between the reflection and the original image
		final float scale = 1.30f;

		int width = sIconWidth;
		int height = sIconHeight;
		float ratio = sIconHeight / (sIconHeight * scale);
		Bitmap original;
		try {
			original = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			return icon;
		}
		final Canvas cv = new Canvas();
		cv.setBitmap(original);
		icon.setBounds(0, 0, width, height);
		icon.draw(cv);
		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// Create a Bitmap with the flip matix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage;
		try {
			reflectionImage = Bitmap.createBitmap(original, 0, height / 2, width, height / 2,
					matrix, false);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			return new FastBitmapDrawable(original);
		}

		// Create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection;
		try {
			bitmapWithReflection = Bitmap.createBitmap(width, (int) (height * scale),
					Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			return new FastBitmapDrawable(original);
		}

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the gap
		// Paint deafaultPaint = new Paint();
		// canvas.drawRect(0, height, width, height + reflectionGap,
		// deafaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height - 6, null);

		// Create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, original.getHeight(), 0,
				bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height - 6, width, bitmapWithReflection.getHeight(), paint);
		// Draw in the original image
		canvas.drawBitmap(original, 0, 0, null);
		original.recycle();
		original = null;
		reflectionImage.recycle();
		reflectionImage = null;
		try {
			return new FastBitmapDrawable(Bitmap.createScaledBitmap(bitmapWithReflection,
					Math.round(sIconWidth * ratio), sIconHeight, true));
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			return icon;
		}
	}

	/**
	 * @param icon
	 * @param context
	 * @param tint
	 * @return
	 */
	public static Drawable scaledDrawable(Drawable icon, Context context, boolean tint, float scale) {
		initIconSize(context);

		int width = sIconWidth;
		int height = sIconHeight;
		Bitmap original;
		try {
			original = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			return icon;
		}
		Canvas canvas = new Canvas(original);
		canvas.setBitmap(original);
		icon.setBounds(0, 0, width, height);
		icon.draw(canvas);

		if (tint) {
			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(width / 2, 0, width / 2, height, 0xCCFFFFFF,
					0x33FFFFFF, TileMode.CLAMP);
			paint.setShader(shader);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawRect(0, 0, width, height, paint);
		}
		try {
			Bitmap endImage = Bitmap.createScaledBitmap(original, (int) (width * scale),
					(int) (height * scale), true);
			original.recycle();
			original = null;
			return new FastBitmapDrawable(endImage);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			return icon;
		}
	}

	public static Bitmap centerToFit(Bitmap bitmap, int width, int height, Context context) {
		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();

		if (bitmapWidth < width || bitmapHeight < height) {
			int color = context.getResources().getColor(R.color.window_background);

			Bitmap centered = null;
			try {
				centered = Bitmap.createBitmap(bitmapWidth < width ? width : bitmapWidth,
						bitmapHeight < height ? height : bitmapHeight, Bitmap.Config.RGB_565);
			} catch (OutOfMemoryError e) {
				OutOfMemoryHandler.handle();
				return bitmap;
			}

			centered.setDensity(bitmap.getDensity());
			Canvas canvas = new Canvas(centered);
			canvas.drawColor(color);
			canvas.drawBitmap(bitmap, (width - bitmapWidth) / 2.0f, (height - bitmapHeight) / 2.0f,
					null);

			bitmap = centered;
		}

		return bitmap;
	}


	/**
	 * 获取标准图标大小，图标为正方形，高宽相同.
	 * 
	 * @author huyong
	 * @return
	 */
	public static int getStandardIconSize(final Context context) {
		initIconSize(context);
		return sIconWidth;
	}

	/**
	 * 合成图片
	 * 
	 * @author huyong
	 * @param base
	 *            ：合成图片底图
	 * @param cover
	 *            ：合成图片罩子
	 * @param drawable
	 *            ： 待合成的源图
	 * @param drawable
	 *            ： 合成图片蒙版
	 * @param scale
	 *            ：缩放比率
	 * @return
	 */
	public static BitmapDrawable composeAppIconDrawable(Bitmap base, BitmapDrawable cover, BitmapDrawable drawable, BitmapDrawable mask,
			float scale) {
		if (sCanvas == null || sPaint == null || sMatrix == null || sXfermode == null) {
			sCanvas = new Canvas();
			sCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG));
			sPaint = new Paint();
			sMatrix = new Matrix();
			sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
		}
		// 用于判断mask是否只影响图标还是图标与底座都合成,目前只为true 
		Context context = ApplicationProxy.getContext();
		boolean maskAppIcon = true;
		Bitmap tempBitmap = null;
		// 有底图或罩子
		if (base == null) {
			if (cover != null && cover.getBitmap() != null) {
				final Bitmap.Config config = cover.getOpacity() != PixelFormat.OPAQUE
						? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565;
				base = Bitmap.createBitmap(cover.getBitmap().getWidth(), cover.getBitmap()
						.getHeight(), config);
			}
			
			if (base == null) {
				return drawable;
			}
		}
		int width = base.getWidth();
		int height = base.getHeight();
		final float scaleWidth = scale * width; // 缩放后的宽大小
		final float scaleHeight = scale * height; // 缩放后的高大小
		final Bitmap midBitmap = drawable.getBitmap();
		float scaleFactorW = 0f; // 缩放后较原图的宽的比例
		float scaleFactorH = 0f; // 缩放后较原图的高的比例
		if (midBitmap != null) {
			final int realWidth = midBitmap.getWidth();
			final int realHeight = midBitmap.getHeight();
			scaleFactorW = scaleWidth / realWidth;
			scaleFactorH = scaleHeight / realHeight;
		}

		synchronized (sCanvas) {
			final Canvas canvas = sCanvas;
			final Paint paint = sPaint;
			final Matrix matrix = sMatrix;
			if (maskAppIcon) {
				tempBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			} else {
				tempBitmap = base;
			}
			canvas.setBitmap(tempBitmap);
			int saveId = canvas.save();
			paint.setAntiAlias(true);
			matrix.setScale(scaleFactorW, scaleFactorH);
			matrix.postTranslate((width - scaleWidth) / 2f, (height - scaleHeight) / 2f);
			canvas.drawBitmap(drawable.getBitmap(), matrix, paint);
			canvas.restoreToCount(saveId);
			if (!maskAppIcon && cover != null) {
				canvas.drawBitmap(cover.getBitmap(), 0, 0, null);
			}
			// 加上mask蒙版
			if (mask != null) {
				Xfermode xf = paint.getXfermode();
				paint.setXfermode(sXfermode);
				canvas.drawBitmap(mask.getBitmap(), 0, 0, paint);
				paint.setXfermode(xf);
			}
			Bitmap fitIcon = IconUtilities.createBitmapThumbnail(tempBitmap, context);
			if (maskAppIcon && base != null) {
				//底座需要放缩  	ADT-10807(功能表，更改图标大小后图标大小不统一)
				base = IconUtilities.createBitmapThumbnail(base, context);
				if (fitIcon == null) { //如果为空只画底座
					drawable = new BitmapDrawable(context.getResources(), base);
				} else {
					canvas.setBitmap(base);
					saveId = canvas.save();
					//居中
					// canvas.translate((width - fitIcon.getWidth()) / 2f, (height - fitIcon.getHeight()) / 2f);
					canvas.drawBitmap(fitIcon, 0, 0, paint);
					canvas.restoreToCount(saveId);
					if (cover != null) {
						float coverScale = (fitIcon.getWidth() + 0.1f) / cover.getBitmap().getWidth();;
						matrix.reset();
						matrix.setScale(coverScale, coverScale);
						canvas.drawBitmap(cover.getBitmap(), matrix, paint);
					}
					drawable = new BitmapDrawable(context.getResources(), base);
				}
			} else {
				drawable = new BitmapDrawable(context.getResources(), fitIcon);
			}
		}
		return drawable;
	}
	
}
