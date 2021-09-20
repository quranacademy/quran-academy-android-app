package org.quranacademy.quran.ui.widgets.wordbywordview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import org.quranacademy.quran.ui.wordbywordview.R;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    /**
     * Special value for the child view spacing.
     * SPACING_AUTO means that the actual spacing is calculated according to the size of the
     * container and the number of the child views, so that the child views are placed evenly in
     * the container.
     */
    public static final int SPACING_AUTO = -65536;

    /**
     * Special value for the horizontal spacing of the child views in the last row
     * SPACING_ALIGN means that the horizontal spacing of the child views in the last row keeps
     * the same with the spacing used in the row above. If there is only one row, this value is
     * ignored and the spacing will be calculated according to childSpacing.
     */
    public static final int SPACING_ALIGN = -65537;

    private static final int SPACING_UNDEFINED = -65538;

    protected static final int UNSPECIFIED_GRAVITY = -1;

    private static final int ROW_VERTICAL_GRAVITY_AUTO = -65536;

    private static final boolean DEFAULT_FLOW = true;
    private static final int DEFAULT_CHILD_SPACING = 0;
    private static final int DEFAULT_CHILD_SPACING_FOR_LAST_ROW = SPACING_UNDEFINED;
    private static final float DEFAULT_ROW_SPACING = 0;
    private static final boolean DEFAULT_RTL = false;
    private static final int DEFAULT_MAX_ROWS = Integer.MAX_VALUE;

    private boolean flow = DEFAULT_FLOW;
    private int childSpacing = DEFAULT_CHILD_SPACING;
    private int minChildSpacing = DEFAULT_CHILD_SPACING;
    private int childSpacingForLastRow = DEFAULT_CHILD_SPACING_FOR_LAST_ROW;
    private float rowSpacing = DEFAULT_ROW_SPACING;
    private float adjustedRowSpacing = DEFAULT_ROW_SPACING;
    private boolean rtl = DEFAULT_RTL;
    private int maxRows = DEFAULT_MAX_ROWS;
    private int gravity = UNSPECIFIED_GRAVITY;
    private int rowVerticalGravity = ROW_VERTICAL_GRAVITY_AUTO;
    private int exactMeasuredHeight;

    private List<Float> horizontalSpacingForRow = new ArrayList<>();
    private List<Integer> heightForRow = new ArrayList<>();
    private List<Integer> widthForRow = new ArrayList<>();
    private List<Integer> childNumForRow = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FlowLayout, 0, 0);
        try {
            flow = a.getBoolean(R.styleable.FlowLayout_flFlow, DEFAULT_FLOW);
            try {
                childSpacing = a.getInt(R.styleable.FlowLayout_flChildSpacing, DEFAULT_CHILD_SPACING);
            } catch (NumberFormatException e) {
                childSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_flChildSpacing, (int) dpToPx(DEFAULT_CHILD_SPACING));
            }
            try {
                minChildSpacing = a.getInt(R.styleable.FlowLayout_flMinChildSpacing, DEFAULT_CHILD_SPACING);
            } catch (NumberFormatException e) {
                minChildSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_flMinChildSpacing, (int) dpToPx(DEFAULT_CHILD_SPACING));
            }
            try {
                childSpacingForLastRow = a.getInt(R.styleable.FlowLayout_flChildSpacingForLastRow, SPACING_UNDEFINED);
            } catch (NumberFormatException e) {
                childSpacingForLastRow = a.getDimensionPixelSize(R.styleable.FlowLayout_flChildSpacingForLastRow, (int) dpToPx(DEFAULT_CHILD_SPACING));
            }
            try {
                rowSpacing = a.getInt(R.styleable.FlowLayout_flRowSpacing, 0);
            } catch (NumberFormatException e) {
                rowSpacing = a.getDimension(R.styleable.FlowLayout_flRowSpacing, dpToPx(DEFAULT_ROW_SPACING));
            }
            maxRows = a.getInt(R.styleable.FlowLayout_flMaxRows, DEFAULT_MAX_ROWS);
            rtl = a.getBoolean(R.styleable.FlowLayout_flRtl, DEFAULT_RTL);
            gravity = a.getInt(R.styleable.FlowLayout_android_gravity, UNSPECIFIED_GRAVITY);
            rowVerticalGravity = a.getInt(R.styleable.FlowLayout_flRowVerticalGravity, ROW_VERTICAL_GRAVITY_AUTO);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        horizontalSpacingForRow.clear();
        heightForRow.clear();
        widthForRow.clear();
        childNumForRow.clear();

        int measuredHeight = 0, measuredWidth = 0, childCount = getChildCount();
        int rowWidth = 0, maxChildHeightInRow = 0, childNumInRow = 0;
        final int rowSize = widthSize - getPaddingLeft() - getPaddingRight();
        int rowTotalChildWidth = 0;
        final boolean allowFlow = widthMode != MeasureSpec.UNSPECIFIED && flow;
        final int childSpacing = this.childSpacing == SPACING_AUTO && widthMode == MeasureSpec.UNSPECIFIED
                ? 0 : this.childSpacing;
        final float tmpSpacing = childSpacing == SPACING_AUTO ? minChildSpacing : childSpacing;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams childParams = child.getLayoutParams();
            int horizontalMargin = 0, verticalMargin = 0;
            if (childParams instanceof MarginLayoutParams) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, measuredHeight);
                MarginLayoutParams marginParams = (MarginLayoutParams) childParams;
                horizontalMargin = marginParams.leftMargin + marginParams.rightMargin;
                verticalMargin = marginParams.topMargin + marginParams.bottomMargin;
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }

            int childWidth = child.getMeasuredWidth() + horizontalMargin;
            int childHeight = child.getMeasuredHeight() + verticalMargin;
            if (allowFlow && rowWidth + childWidth > rowSize) { // Need flow to next row
                // Save parameters for current row
                horizontalSpacingForRow.add(
                        getSpacingForRow(childSpacing, rowSize, rowTotalChildWidth, childNumInRow));
                childNumForRow.add(childNumInRow);
                heightForRow.add(maxChildHeightInRow);
                widthForRow.add(rowWidth - (int) tmpSpacing);
                if (horizontalSpacingForRow.size() <= maxRows) {
                    measuredHeight += maxChildHeightInRow;
                }
                measuredWidth = Math.max(measuredWidth, rowWidth);

                // Place the child view to next row
                childNumInRow = 1;
                rowWidth = childWidth + (int) tmpSpacing;
                rowTotalChildWidth = childWidth;
                maxChildHeightInRow = childHeight;
            } else {
                childNumInRow++;
                rowWidth += childWidth + tmpSpacing;
                rowTotalChildWidth += childWidth;
                maxChildHeightInRow = Math.max(maxChildHeightInRow, childHeight);
            }
        }

        // Measure remaining child views in the last row
        if (childSpacingForLastRow == SPACING_ALIGN) {
            // For SPACING_ALIGN, use the same spacing from the row above if there is more than one
            // row.
            if (horizontalSpacingForRow.size() >= 1) {
                horizontalSpacingForRow.add(
                        horizontalSpacingForRow.get(horizontalSpacingForRow.size() - 1));
            } else {
                horizontalSpacingForRow.add(
                        getSpacingForRow(childSpacing, rowSize, rowTotalChildWidth, childNumInRow));
            }
        } else if (childSpacingForLastRow != SPACING_UNDEFINED) {
            // For SPACING_AUTO and specific DP values, apply them to the spacing strategy.
            horizontalSpacingForRow.add(
                    getSpacingForRow(childSpacingForLastRow, rowSize, rowTotalChildWidth, childNumInRow));
        } else {
            // For SPACING_UNDEFINED, apply childSpacing to the spacing strategy for the last row.
            horizontalSpacingForRow.add(
                    getSpacingForRow(childSpacing, rowSize, rowTotalChildWidth, childNumInRow));
        }

        childNumForRow.add(childNumInRow);
        heightForRow.add(maxChildHeightInRow);
        widthForRow.add(rowWidth - (int) tmpSpacing);
        if (horizontalSpacingForRow.size() <= maxRows) {
            measuredHeight += maxChildHeightInRow;
        }
        measuredWidth = Math.max(measuredWidth, rowWidth);

        if (childSpacing == SPACING_AUTO) {
            measuredWidth = widthSize;
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            measuredWidth = measuredWidth + getPaddingLeft() + getPaddingRight();
        } else {
            measuredWidth = Math.min(measuredWidth + getPaddingLeft() + getPaddingRight(), widthSize);
        }

        measuredHeight += getPaddingTop() + getPaddingBottom();
        int rowNum = Math.min(horizontalSpacingForRow.size(), maxRows);
        float rowSpacing = this.rowSpacing == SPACING_AUTO && heightMode == MeasureSpec.UNSPECIFIED
                ? 0 : this.rowSpacing;
        if (rowSpacing == SPACING_AUTO) {
            if (rowNum > 1) {
                adjustedRowSpacing = (heightSize - measuredHeight) / (rowNum - 1);
            } else {
                adjustedRowSpacing = 0;
            }
            measuredHeight = heightSize;
        } else {
            adjustedRowSpacing = rowSpacing;
            if (rowNum > 1) {
                measuredHeight = heightMode == MeasureSpec.UNSPECIFIED
                        ? ((int) (measuredHeight + adjustedRowSpacing * (rowNum - 1)))
                        : (Math.min((int) (measuredHeight + adjustedRowSpacing * (rowNum - 1)),
                        heightSize));
            }
        }

        exactMeasuredHeight = measuredHeight;

        measuredWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : measuredWidth;
        measuredHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : measuredHeight;

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft(), paddingRight = getPaddingRight(),
                paddingTop = getPaddingTop(), paddingBottom = getPaddingBottom();

        int x = rtl ? (getWidth() - paddingRight) : paddingLeft;
        int y = paddingTop;

        int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        int horizontalGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;

        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL: {
                int offset = (b - t - paddingTop - paddingBottom - exactMeasuredHeight) / 2;
                y += offset;
                break;
            }
            case Gravity.BOTTOM: {
                int offset = b - t - paddingTop - paddingBottom - exactMeasuredHeight;
                y += offset;
                break;
            }
            default:
                break;
        }

        int horizontalPadding = paddingLeft + paddingRight, layoutWidth = r - l;
        x += getHorizontalGravityOffsetForRow(horizontalGravity, layoutWidth, horizontalPadding, 0);

        int verticalRowGravity = rowVerticalGravity & Gravity.VERTICAL_GRAVITY_MASK;

        int rowCount = childNumForRow.size(), childIdx = 0;
        for (int row = 0; row < rowCount; row++) {
            int childNum = childNumForRow.get(row);
            int rowHeight = heightForRow.get(row);
            float spacing = horizontalSpacingForRow.get(row);
            for (int i = 0; i < childNum && childIdx < getChildCount(); ) {
                //получаем слова в обратном порядке
                View child = getChildAt(childIdx + childNum - i - 1);
                if (child.getVisibility() == GONE) {
                    continue;
                } else {
                    i++;
                }

                LayoutParams childParams = child.getLayoutParams();
                int marginLeft = 0, marginTop = 0, marginBottom = 0, marginRight = 0;
                if (childParams instanceof MarginLayoutParams) {
                    MarginLayoutParams marginParams = (MarginLayoutParams) childParams;
                    marginLeft = marginParams.leftMargin;
                    marginRight = marginParams.rightMargin;
                    marginTop = marginParams.topMargin;
                    marginBottom = marginParams.bottomMargin;
                }

                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                int tt = y + marginTop;
                if (verticalRowGravity == Gravity.BOTTOM) {
                    tt = y + rowHeight - marginBottom - childHeight;
                } else if (verticalRowGravity == Gravity.CENTER_VERTICAL) {
                    tt = y + marginTop + (rowHeight - marginTop - marginBottom - childHeight) / 2;
                }
                int bb = tt + childHeight;
                if (rtl) {
                    int l1 = x - marginRight - childWidth;
                    int r1 = x - marginRight;
                    child.layout(l1, tt, r1, bb);
                    x -= childWidth + spacing + marginLeft + marginRight;
                } else {
                    int l2 = x + marginLeft;
                    int r2 = x + marginLeft + childWidth;
                    child.layout(l2, tt, r2, bb);
                    x += childWidth + spacing + marginLeft + marginRight;
                }
            }
            childIdx += childNum;
            x = rtl ? (getWidth() - paddingRight) : paddingLeft;
            x += getHorizontalGravityOffsetForRow(
                    horizontalGravity, layoutWidth, horizontalPadding, row + 1);
            y += rowHeight + adjustedRowSpacing;
        }
    }

    private int getHorizontalGravityOffsetForRow(int horizontalGravity, int parentWidth, int horizontalPadding, int row) {
        if (childSpacing == SPACING_AUTO || row >= widthForRow.size()
                || row >= childNumForRow.size() || childNumForRow.get(row) <= 0) {
            return 0;
        }

        int offset = 0;
        switch (horizontalGravity) {
            case Gravity.CENTER_HORIZONTAL:
                offset = (parentWidth - horizontalPadding - widthForRow.get(row)) / 2;
                break;
            case Gravity.RIGHT:
                offset = parentWidth - horizontalPadding - widthForRow.get(row);
                break;
            default:
                break;
        }
        return offset;
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * Returns whether to allow child views flow to next row when there is no enough space.
     *
     * @return Whether to flow child views to next row when there is no enough space.
     */
    public boolean isFlow() {
        return flow;
    }

    /**
     * Sets whether to allow child views flow to next row when there is no enough space.
     *
     * @param flow true to allow flow. false to restrict all child views in one row.
     */
    public void setFlow(boolean flow) {
        this.flow = flow;
        requestLayout();
    }

    /**
     * Returns the horizontal spacing between child views.
     *
     * @return The spacing, either {@link FlowLayout#SPACING_AUTO}, or a fixed size in pixels.
     */
    public int getChildSpacing() {
        return childSpacing;
    }

    /**
     * Sets the horizontal spacing between child views.
     *
     * @param childSpacing The spacing, either {@link FlowLayout#SPACING_AUTO}, or a fixed size in
     *                     pixels.
     */
    public void setChildSpacing(int childSpacing) {
        this.childSpacing = childSpacing;
        requestLayout();
    }

    /**
     * Returns the horizontal spacing between child views of the last row.
     *
     * @return The spacing, either {@link FlowLayout#SPACING_AUTO},
     * {@link FlowLayout#SPACING_ALIGN}, or a fixed size in pixels
     */
    public int getChildSpacingForLastRow() {
        return childSpacingForLastRow;
    }

    /**
     * Sets the horizontal spacing between child views of the last row.
     *
     * @param childSpacingForLastRow The spacing, either {@link FlowLayout#SPACING_AUTO},
     *                               {@link FlowLayout#SPACING_ALIGN}, or a fixed size in pixels
     */
    public void setChildSpacingForLastRow(int childSpacingForLastRow) {
        this.childSpacingForLastRow = childSpacingForLastRow;
        requestLayout();
    }

    /**
     * Returns the vertical spacing between rows.
     *
     * @return The spacing, either {@link FlowLayout#SPACING_AUTO}, or a fixed size in pixels.
     */
    public float getRowSpacing() {
        return rowSpacing;
    }

    /**
     * Sets the vertical spacing between rows in pixels. Use SPACING_AUTO to evenly place all rows
     * in vertical.
     *
     * @param rowSpacing The spacing, either {@link FlowLayout#SPACING_AUTO}, or a fixed size in
     *                   pixels.
     */
    public void setRowSpacing(float rowSpacing) {
        this.rowSpacing = rowSpacing;
        requestLayout();
    }

    /**
     * Returns the maximum number of rows of the FlowLayout.
     *
     * @return The maximum number of rows.
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * Sets the height of the FlowLayout to be at most maxRows tall.
     *
     * @param maxRows The maximum number of rows.
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
        requestLayout();
    }

    public void setGravity(int gravity) {
        if (this.gravity != gravity) {
            this.gravity = gravity;
            requestLayout();
        }
    }

    public void setRowVerticalGravity(int rowVerticalGravity) {
        if (this.rowVerticalGravity != rowVerticalGravity) {
            this.rowVerticalGravity = rowVerticalGravity;
            requestLayout();
        }
    }

    public boolean isRtl() {
        return rtl;
    }

    public void setRtl(boolean rtl) {
        this.rtl = rtl;
        requestLayout();
    }

    public int getMinChildSpacing() {
        return minChildSpacing;
    }

    public void setMinChildSpacing(int minChildSpacing) {
        this.minChildSpacing = minChildSpacing;
        requestLayout();
    }

    public int getRowsCount() {
        return childNumForRow.size();
    }

    private float getSpacingForRow(int spacingAttribute, int rowSize, int usedSize, int childNum) {
        float spacing;
        if (spacingAttribute == SPACING_AUTO) {
            if (childNum > 1) {
                spacing = (rowSize - usedSize) / (childNum - 1);
            } else {
                spacing = 0;
            }
        } else {
            spacing = spacingAttribute;
        }
        return spacing;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}