/**
 * 
 */
package com.go.util.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;

/**
 * 图片工具类 封装一些对MImage的操作以方便使用
 * 
 * @author dengweiming
 * 
 */
public class ImageUtil {
	public final static int TILEMODE = 0;
	public final static int STRETCHMODE = 1;
	public final static int CENTERMODE = 2;

	/**
	 * 绘制平铺图片 指定一个矩形和图片，这个图片根据自己大小填充矩形 当图片大小大于矩形框时，默认拉伸图片与矩形大小一样
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */

	public static void drawTileImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		if (bitmap.getWidth() > (right - left) || bitmap.getHeight() > (bottom - top)) {
			// 图片比矩形大时，让图片自动拉伸成矩形大小
			drawStretchImage(canvas, bitmap, left, top, right, bottom, paint);
		} else {
			Rect rect = new Rect(0, 0, right - left, bottom - top);
			BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT,
					Shader.TileMode.REPEAT);
			Shader shaderBak = paint.getShader();
			paint.setShader(shader);
			canvas.save();
			canvas.translate(left, top);
			canvas.drawRect(rect, paint);
			paint.setShader(shaderBak);
			canvas.restore();
		}
	}

	/**
	 * 绘制图片于矩形中间 指定一个矩形和图片，这个图片绘制在矩形中间 如果图片大于矩形时，图片将以fit to widow的方式显示
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */
	public static void drawCenterImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		int offsetx = 0;
		int offsety = 0;
		int imageW;
		int imageH;
		int newWidth;
		int newHeight;

		imageW = bitmap.getWidth();
		imageH = bitmap.getHeight();
		newWidth = right - left;
		newHeight = bottom - top;

		canvas.save();
		if (imageW > newWidth || imageH > newHeight) // 图片大于矩形时
		{
			float factor;
			if (newWidth * imageH > newHeight * imageW) {
				// 以宽度为主进行缩放
				factor = (float) newHeight / imageH;
				offsetx = (newWidth - (int) (factor * imageW)) / 2;
				offsety = (newHeight - (int) (factor * imageH)) / 2;
				canvas.translate(left + offsetx, top + offsety);
				canvas.scale(factor, factor);

			} else {
				// 以高度为主进行缩放
				factor = (float) newWidth / imageW;
				offsetx = (newWidth - (int) (factor * imageW)) / 2;
				offsety = (newHeight - (int) (factor * imageH)) / 2;
				canvas.translate(left + offsetx, top + offsety);
				canvas.scale(factor, factor);
			}
		} else {
			offsetx = (newWidth - imageW) / 2;
			offsety = (newHeight - imageH) / 2;
			canvas.translate(left + offsetx, top + offsety);
		}
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();
	}

	/**
	 * 绘制拉伸图片 将图片拉伸至矩形大小显示
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */
	public static void drawStretchImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		final float scaleFactorW = (right - left) / (float) bitmap.getWidth();
		final float scaleFactorH = (bottom - top) / (float) bitmap.getHeight();

		canvas.save();
		canvas.translate(left, top);
		canvas.scale(scaleFactorW, scaleFactorH);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();
	}

	public static void computeStretchMatrix(Matrix matrix, int w, int h, int left, int top,
			int right, int bottom) {
		matrix.setTranslate(left, top);
		// if (Machine.isM9()){
		matrix.preScale((float) (1.01 * (right - left) / w), (float) (1.01 * (bottom - top) / h));
		// }else{
		// matrix.preScale((right - left) / (float)w, (bottom - top) /
		// (float)h);
		// }
	}

	/**
	 * 绘制图片 根据mode的不同绘制不同方式的图片
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param mode
	 *            0 ：平铺； 1 ：拉伸； 2：居中
	 * @param left
	 *            渐变区域的左边界
	 * @param top
	 *            渐变区域的上边界
	 * @param right
	 *            渐变区域的右边界
	 * @param bottom
	 *            渐变区域的下边界
	 * @param paint
	 *            画笔，当平铺时paint不能为null
	 */

	public static void drawImage(Canvas canvas, Drawable pic, int mode, int left, int top,
			int right, int bottom, Paint paint) {
		if (pic instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) pic).getBitmap();
			if ((bitmap != null) && (!bitmap.isRecycled())) {
				switch (mode) {
					case TILEMODE :
						drawTileImage(canvas, bitmap, left, top, right, bottom, paint);
						break;

					case STRETCHMODE :
						drawStretchImage(canvas, bitmap, left, top, right, bottom, paint);
						break;

					case CENTERMODE :
						drawCenterImage(canvas, bitmap, left, top, right, bottom, paint);
						break;
				}
			}
		} else if (pic instanceof NinePatchDrawable) {
			pic.setBounds(left, top, right, bottom);
			pic.draw(canvas);
		}
	}

	/**
	 * 绘制图片于矩形中间 指定一个矩形和图片，这个图片绘制在矩形中间 如果图片大于矩形时，图片将以fit to widow的方式显示
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */
	public static void drawFitImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		if (bitmap == null) {
			return;
		}
		int offsetx = 0;
		int offsety = 0;
		int imageW;
		int imageH;
		int newWidth;
		int newHeight;

		imageW = bitmap.getWidth();
		imageH = bitmap.getHeight();
		newWidth = right - left;
		newHeight = bottom - top;

		canvas.save();
		canvas.clipRect(left, top, right, bottom);
		float factor;
		if (newWidth * imageH < newHeight * imageW) {
			// 以宽度为主进行缩放
			factor = (float) newHeight / imageH;
			offsetx = (newWidth - (int) (factor * imageW)) / 2;
			offsety = (newHeight - (int) (factor * imageH)) / 2;
			canvas.translate(left + offsetx, top + offsety);
			canvas.scale(factor, factor);

		} else {
			// 以高度为主进行缩放
			factor = (float) newWidth / imageW;
			offsetx = (newWidth - (int) (factor * imageW)) / 2;
			offsety = (newHeight - (int) (factor * imageH)) / 2;
			canvas.translate(left + offsetx, top + offsety);
			canvas.scale(factor, factor);
		}
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();
	}
	/**
	 * 将tag画到原图的右上角
	 * @param context
	 * @param drawable
	 * @param tag
	 * @return
	 */
	public static Drawable drawRightTopTag(Context context, Drawable drawable, Drawable tag) {
		int drawableHeight = drawable.getIntrinsicHeight();
		int drawableWidth = drawable.getIntrinsicWidth();
		Bitmap bmp = Bitmap.createBitmap(drawableWidth, drawableHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		int h = tag.getIntrinsicHeight();
		int w = tag.getIntrinsicWidth();
		drawable.setBounds(0, 0, drawableWidth, drawableHeight);
		drawable.draw(canvas);
		canvas.save();
		canvas.translate(drawableWidth - w - DrawUtils.dip2px(5), DrawUtils.dip2px(5));
		tag.setBounds(0, 0, w, h);
		tag.draw(canvas);
		canvas.restore();
		BitmapDrawable bmd = new BitmapDrawable(bmp);
		bmd.setTargetDensity(context.getResources().getDisplayMetrics());
		return bmd;
	}
	/**
	 * 两张图对齐画
	 * @param context
	 * @param drawable
	 * @param tag
	 * @return
	 */
	public static Drawable drawCoverImage(Context context, Drawable drawable, Drawable tag) {
		int drawableHeight = drawable.getIntrinsicHeight();
		int drawableWidth = drawable.getIntrinsicWidth();
		Bitmap bmp = Bitmap.createBitmap(drawableWidth, drawableHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		int h = tag.getIntrinsicHeight();
		int w = tag.getIntrinsicWidth();
		drawable.setBounds(0, 0, drawableWidth, drawableHeight);
		drawable.draw(canvas);
		tag.setBounds(0, 0, w, h);
		tag.draw(canvas);
		BitmapDrawable bmd = new BitmapDrawable(bmp);
		bmd.setTargetDensity(context.getResources().getDisplayMetrics());
		return bmd;
	}
	/**
	 * 获得圆角图片的方法
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		if (bitmap == null) {
			return null;
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	/**
	 * 通过传入的大小返回对应的图片(缩放/剪切等操作)
	 * @param bitmap 要缩放的图片
	 * @param width 要缩放的宽度
	 * @param height 要缩放的高度
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		if (bitmap == null) {
			return bitmap;
		}
		//获取Bitmap宽度
		int width = bitmap.getWidth();
		//获取Bitmap高度
		int height = bitmap.getHeight();
		if (width == newWidth && height == newHeight) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		//参考Bitmap高度获取缩放比例(高度)
		float scale = newHeight / (float) height;
		//如果缩放出来的宽度少于需要的宽度,则参照宽度比例缩放Bitmap.
		if (width * scale < newWidth) {
			scale = newWidth / (float) width;
		}
		//设置缩放比例
		matrix.postScale(scale, scale);
		if (width * scale - newWidth > 0 || height * scale - newHeight > 0) {
			// 缩放并截取图片
			return Bitmap.createBitmap(Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true), width * scale - newWidth > 0 ? (int) (width * scale - newWidth) / 2 : 0, height * scale - newHeight > 0 ? (int) (height * scale - newHeight) / 2 : 0, newWidth, newHeight);
		}
		//缩放图片
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}	
}
