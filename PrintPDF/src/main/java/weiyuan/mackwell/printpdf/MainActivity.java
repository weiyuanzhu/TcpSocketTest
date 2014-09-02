package weiyuan.mackwell.printpdf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends Activity {


    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 1;

        public MyPrintDocumentAdapter(Context context) {
            this.context = context;

        }


        @Override

        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight = newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled()) {
                layoutResultCallback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                layoutResultCallback.onLayoutFinished(info, true);

            } else {
                layoutResultCallback.onLayoutFailed("Page count is zero");
            }
        }

        @Override
        public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {

            for (int i=0; i<totalpages; i++)
            {
                if (pagesInRange(pageRanges,i)) {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,pageHeight,i).create();

                    PdfDocument.Page page = myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        writeResultCallback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }

                    drawPage(page,i);
                    myPdfDocument.finishPage(page);
                }
            }

            try{
                myPdfDocument.writeTo(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
            } catch (IOException e){
                writeResultCallback.onWriteFailed(e.toString());
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            writeResultCallback.onWriteFinished(pageRanges);




        }
    }

    private boolean pagesInRange(PageRange[] pageRanges,int page) {
        for (int i=0; i<pageRanges.length;i++) {

            if ((page >= pageRanges[i].getStart()) && (page <= pageRanges[i].getEnd())) {
                return true;
            }
        }
        return false;

    }


    private void drawPage(PdfDocument.Page page, int pagenumber)
    {
        Canvas canvas = page.getCanvas();

        pagenumber ++ ;

        int titleBaseLine = 72;
        int leftMargin = 54;



        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);

        canvas.drawText("N-Light panel report" , leftMargin, titleBaseLine, paint);

        paint.setTextSize(20);

        canvas.drawText("Report type: Panel status report", leftMargin,titleBaseLine + 50,paint);

        canvas.drawText("Panel location: Test Unit", leftMargin,titleBaseLine + 80,paint);

        canvas.drawText("Date/Time", leftMargin,titleBaseLine + 150,paint);
        canvas.drawText("Fault(s) found", leftMargin + 200,titleBaseLine + 150,paint);
        canvas.drawText("Status", leftMargin + 400,titleBaseLine  + 150,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 160,leftMargin+500,titleBaseLine+160,paint);

        canvas.drawText("01 Aug 2014 00:00:22", leftMargin,titleBaseLine + 180,paint);
        canvas.drawText("2", leftMargin + 250,titleBaseLine + 180,paint);
        canvas.drawText("Fault(s) found", leftMargin + 400,titleBaseLine  + 180,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 190,leftMargin+500,titleBaseLine+190,paint);

        canvas.drawText("27 Jul 2014 13:47:22", leftMargin,titleBaseLine + 210,paint);
        canvas.drawText("2", leftMargin + 250,titleBaseLine + 210,paint);
        canvas.drawText("Fault(s) found", leftMargin + 400,titleBaseLine  + 210,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 220,leftMargin+500,titleBaseLine+220,paint);

        canvas.drawText("27 Jul 2014 13:47:22", leftMargin,titleBaseLine + 240,paint);
        canvas.drawText("2", leftMargin + 250,titleBaseLine + 240,paint);
        canvas.drawText("Fault(s) found", leftMargin + 400,titleBaseLine  + 240,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 250,leftMargin+500,titleBaseLine+250,paint);

        canvas.drawText("02 Jul 2014 10:02:22", leftMargin,titleBaseLine + 270,paint);
        canvas.drawText("1", leftMargin + 250,titleBaseLine + 270,paint);
        canvas.drawText("Fault(s) found", leftMargin + 400,titleBaseLine  + 270,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 280,leftMargin+500,titleBaseLine+280,paint);

        canvas.drawText("01 Jul 2014 00:00:22", leftMargin,titleBaseLine + 300,paint);
        canvas.drawText("1", leftMargin + 250,titleBaseLine + 300,paint);
        canvas.drawText("Fault(s) found", leftMargin + 400,titleBaseLine  + 300,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 310,leftMargin+500,titleBaseLine+310,paint);

        canvas.drawText("26 Jun 2014 17:17:22", leftMargin,titleBaseLine + 330,paint);
        canvas.drawText("0", leftMargin + 250,titleBaseLine + 330,paint);
        canvas.drawText("OK", leftMargin + 400,titleBaseLine  + 330,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 340,leftMargin+500,titleBaseLine+340,paint);

        /*if (pagenumber % 2 == 0) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.GREEN);
        }

        PdfDocument.PageInfo pageInfo = page.getInfo();

        canvas.drawCircle(pageInfo.getPageWidth()/2,pageInfo.getPageHeight()/2,150, paint);
        */

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void printDocument(View view) {

        Log.i("printPDF","print clicked");

        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) + " Document";

        printManager.print(jobName,new MyPrintDocumentAdapter(this),null);
    }
}
