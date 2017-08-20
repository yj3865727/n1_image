package com.lequan.n1.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationUtils {
	public static void showAlpha(View v) {
		v.setVisibility(0);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(200);
		v.startAnimation(alphaAnimation);
	}

	public static void hideAlpha(View v) {
		v.setVisibility(8);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(200);
		v.startAnimation(alphaAnimation);
	}
	public static Animation loadAnimation(Context context, int id)
            throws NotFoundException {

        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            return createAnimationFromXml(context, parser);
        } catch (XmlPullParserException ex) {
            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex) {
            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } finally {
            if (parser != null) parser.close();
        }
    }
	private static Animation createAnimationFromXml(Context c, XmlPullParser parser)
            throws XmlPullParserException, IOException {

        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
    }
	   private static Animation createAnimationFromXml(Context c, XmlPullParser parser,
	            AnimationSet parent, AttributeSet attrs) throws XmlPullParserException, IOException {

	        Animation anim = null;

	        // Make sure we are on a start tag.
	        int type;
	        int depth = parser.getDepth();

	        while (((type=parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
	               && type != XmlPullParser.END_DOCUMENT) {

	            if (type != XmlPullParser.START_TAG) {
	                continue;
	            }

	            String  name = parser.getName();

	            if (name.equals("set")) {
	                anim = new AnimationSet(c, attrs);
	                createAnimationFromXml(c, parser, (AnimationSet)anim, attrs);
	            } else if (name.equals("alpha")) {
	                anim = new AlphaAnimation(c, attrs);
	            } else if (name.equals("scale")) {
	                anim = new ScaleAnimation(c, attrs);
	            }  else if (name.equals("rotate")) {
	                anim = new RotateAnimation(c, attrs);
	            }  else if (name.equals("translate")) {
	                anim = new TranslateAnimation(c, attrs);
	            } else {
	                throw new RuntimeException("Unknown animation name: " + parser.getName());
	            }

	            if (parent != null) {
	                parent.addAnimation(anim);
	            }
	        }

	        return anim;

	    }
}
