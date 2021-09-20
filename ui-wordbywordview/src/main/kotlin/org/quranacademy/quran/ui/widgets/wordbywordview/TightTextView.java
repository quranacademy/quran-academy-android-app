package org.quranacademy.quran.ui.widgets.wordbywordview;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Tightly wraps the text when setting the maxWidth.
 *
 * @author sky
 * <p>
 * https://stackoverflow.com/questions/10913384/how-to-make-textview-wrap-its-multiline-content-exactly
 * <p>
 * Данный класс решает проблему, когда текст пословного перевода большой
 * и занимает несколько строк. В этом случае текст может не занимать всю ширину.
 * В пословном переводе максимальная ширина текста перевода равна трети экрана, чтобы
 * не было так, что перевод слова занял весь экран, т. е. maxWidth = screenSize / 3.
 * Иногда бывает так, что текст длинный и многострочный, из-за чего TextView занял максимальную ширину,
 * но сам  многострочный текст не занимает всю ширину. Пример:
 * <p>
 * ______________________
 * |                      |
 * |   то (постарайтесь)  |
 * |       выяснить       |
 * |______________________|
 * <p>
 * TextView занял треть экрана, как и полагается, но из-за этого по бокам образуются пустоты. Нам нужно,
 * чтобы TextView занял ширину текста, чтобы этих пустот не было. Именно эту задачу выполняет данная вьюшка.
 */
public class TightTextView extends AppCompatTextView {
    private boolean hasMaxWidth;

    public TightTextView(Context context) {
        this(context, null, 0);
    }

    public TightTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (hasMaxWidth) {
            int specModeW = MeasureSpec.getMode(widthMeasureSpec);
            if (specModeW != MeasureSpec.EXACTLY) {
                Layout layout = getLayout();
                int linesCount = layout.getLineCount();
                if (linesCount > 1) {
                    float textRealMaxWidth = 0;
                    for (int n = 0; n < linesCount; ++n) {
                        textRealMaxWidth = Math.max(textRealMaxWidth, layout.getLineWidth(n));
                    }
                    int w = Math.round(textRealMaxWidth);
                    if (w < getMeasuredWidth()) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.AT_MOST),
                                heightMeasureSpec);
                    }
                }
            }
        }
    }


    @Override
    public void setMaxWidth(int maxpixels) {
        super.setMaxWidth(maxpixels);
        hasMaxWidth = true;
    }

    @Override
    public void setMaxEms(int maxems) {
        super.setMaxEms(maxems);
        hasMaxWidth = true;
    }
}