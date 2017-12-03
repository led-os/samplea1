package com.go.util.graphics.effector.united;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.Xml;

import com.gau.go.launcherex.R;
import com.go.util.xml.XmlUtils;

/**
 * 滚屏特效XML解析类
 * @author yejijiong
 *
 */
public class EffectorParser {
	private final static String TAG_EFFECTORS = "effectors";
	private final static String TAG_EFFECTOR = "effector";
	
	public static SparseArray<EffectorInfo> getEffectors(Context context) {
		try {
			XmlResourceParser parser = context.getResources().getXml(R.xml.effector);
			AttributeSet attrs = Xml.asAttributeSet(parser);
			XmlUtils.beginDocument(parser, TAG_EFFECTORS);

			SparseArray<EffectorInfo> effectorMap = new SparseArray<EffectorInfo>();
			final int depth = parser.getDepth();
			int type;
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
					&& type != XmlPullParser.END_DOCUMENT) {
				if (type != XmlPullParser.START_TAG) {
					continue;
				}

				TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Effector);
				final String name = parser.getName();
				if (TAG_EFFECTOR.equals(name)) {
					EffectorInfo effectorInfo = getEffectorInfo(a, context);
					if (effectorInfo != null) {
						effectorMap.put(effectorInfo.mEffectorId, effectorInfo);
					}
				}
				a.recycle();
			}
			parser.close();
			parser = null;
			return effectorMap;
		} catch (XmlPullParserException e) {
		} catch (IOException e) {
		}
		return null;
	}
	
	private static EffectorInfo getEffectorInfo(TypedArray a, Context context) {
		if (a == null) {
			return null;
		}
		EffectorInfo info = new EffectorInfo();
		Resources resources = context.getResources();
		info.mEffectorId = a.getInt(R.styleable.Effector_effectorId, 0);
		info.mEffectorNameId = resources.getIdentifier(
				a.getString(R.styleable.Effector_effectorName), "string", context.getPackageName());
		info.mEffectorDrawableId = resources.getIdentifier(
				a.getString(R.styleable.Effector_drawableName), "drawable", context.getPackageName());
		info.mIsPrime = a.getBoolean(R.styleable.Effector_isPrime, false);
		info.mEffectorType = a.getInt(R.styleable.Effector_effectorType, 0);
		info.mIsBothSupprot = a.getBoolean(R.styleable.Effector_isBothSupport, false);
		return info;
	}
}
