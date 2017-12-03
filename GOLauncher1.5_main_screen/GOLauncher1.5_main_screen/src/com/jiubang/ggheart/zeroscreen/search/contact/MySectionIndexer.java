package com.jiubang.ggheart.zeroscreen.search.contact;

import java.util.Arrays;

import android.widget.SectionIndexer;

/**
 * A section indexer that is configured with precomputed section titles and
 * their respective counts.
 */
public class MySectionIndexer implements SectionIndexer {

    private final String[] mSections;
    private final int[] mPositions;
    private final int mCount;

    /**
     * Constructor.
     *
     * @param sections a non-null array
     * @param counts a non-null array of the same size as <code>sections</code>
     */
    public MySectionIndexer(String[] sections, int[] counts) {
        if (sections == null || counts == null) {
            throw new NullPointerException();
        }

        if (sections.length != counts.length) {
            throw new IllegalArgumentException(
                    "The sections and counts arrays must have the same length");
        }

        mSections = sections;
        mPositions = new int[counts.length];
        int position = 0;
        for (int i = 0; i < counts.length; i++) {
            if (mSections[i] == null) {
                mSections[i] = " ";
            } else {
                mSections[i] = mSections[i].trim();
            }

            mPositions[i] = position;
            position += counts[i];
        }
        mCount = position;
    }

    public Object[] getSections() {
        return mSections;
    }

    public int getPositionForSection(int section) {
        if (section < 0 || section >= mSections.length) {
            return -1;
        }

        return mPositions[section];
    }

    public int getSectionForPosition(int position) {
        if (position < 0 || position >= mCount) {
            return -1;
        }

        int index = Arrays.binarySearch(mPositions, position);

        /*
         * Consider this example: section positions are 0, 3, 5; the supplied
         * position is 4. The section corresponding to position 4 starts at
         * position 3, so the expected return value is 1. Binary search will not
         * find 4 in the array and thus will return -insertPosition-1, i.e. -3.
         * To get from that number to the expected value of 1 we need to negate
         * and subtract 2.
         */
        return index >= 0 ? index : -index - 2;
    }
    
    public int getPositionForSection(String sectionName) {
    	int section = -1;
    	for (int i = 0; i < mSections.length; i++) {
			if (mSections[i].equals(sectionName)) {
				section = i;
				break;
			}
		}
    	return getPositionForSection(section);
    }
}
