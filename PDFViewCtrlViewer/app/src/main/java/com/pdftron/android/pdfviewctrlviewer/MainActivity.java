package com.pdftron.android.pdfviewctrlviewer;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private final SparseArray<PDFViewCtrl> mViewHolders = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup pager
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new CustomPagerAdapter());
    }

    /**
     * Helper method to view a PDF document from resource
     *
     * @param resourceId of the sample PDF file
     * @param fileName   of the temporary PDF file copy
     * @throws PDFNetException if invalid document path is supplied to PDFDoc
     */
    public void viewFromResource(PDFViewCtrl pdfViewCtrl, int resourceId, String fileName) throws PDFNetException {
        File file = Utils.copyResourceToLocal(this, resourceId, fileName, ".pdf");
        PDFDoc pdfDoc = new PDFDoc(file.getAbsolutePath());
        pdfViewCtrl.setDoc(pdfDoc);
        // Alternatively, you can open the document using Uri:
        // Uri fileUri = Uri.fromFile(file);
        // PDFDoc pdfDoc = pdfViewCtrl.openPDFUri(fileUri, null);
    }

    class CustomPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View layout = inflater.inflate(R.layout.viewer_page, container, false);
            PDFViewCtrl pdfViewCtrl = layout.findViewById(R.id.pdfviewctrl);

            // load doc
            try {
                if (position == 0) {
                    viewFromResource(pdfViewCtrl, R.raw.floorplan, "floor_plan");
                } else if (position == 1) {
                    viewFromResource(pdfViewCtrl, R.raw.sample, "sample");
                } else {
                    viewFromResource(pdfViewCtrl, R.raw.words, "words");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mViewHolders.put(position, pdfViewCtrl);
            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            PDFViewCtrl pdfViewCtrl = mViewHolders.get(position);
            if (pdfViewCtrl != null) {
                pdfViewCtrl.destroy();
                pdfViewCtrl = null;
            }
            container.removeView((View) object);
        }
    }
}
