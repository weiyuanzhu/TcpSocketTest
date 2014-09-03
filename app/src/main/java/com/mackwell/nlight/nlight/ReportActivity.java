package com.mackwell.nlight.nlight;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Report;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.Constants;
import com.mackwell.nlight.util.DataParser;
import com.mackwell.nlight.util.GetCmdEnum;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends BaseActivity implements ReportFragment.OnListItemClickedListener {

    private static final String TAG = "ReportActivity";
    private static final String TAG_RECEIVE = "ReportActivity_Receive";

    private String ip;
    private List<Integer> reportRawData;
    private List<Report> reportList;
    private Handler mHandler;
    private ReportFragment fragment;


    @Override
    public void receive(List<Integer> rx, String ip) {
        Log.d(TAG_RECEIVE,ip);
       System.out.println(rx);
        if (rx.get(1) == Constants.MASTER_GET && rx.get(2) == Constants.GET_REPORT) {
            reportRawData.addAll(rx.subList(3, rx.size() - 6));
        } else {
            if (rx.get(1) == Constants.FINISH) {
                Log.i(TAG,Integer.toString(reportRawData.size()));
                System.out.println(reportRawData);
                reportList = DataParser.getReportList(reportRawData);
                mHandler.post(test);
            }
        }
    }

    @Override
    public void onCLick(int position) {
        Intent intent = new Intent(this,ReportFaultsActivity.class);
        intent.putExtra("report",reportList.get(position-1));
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ip = getIntent().getStringExtra("ip");
        isDemo = getIntent().getBooleanExtra("demo",true);
        reportRawData = new ArrayList<Integer>();
        reportList = new ArrayList<Report>();

        fragment = ReportFragment.newInstance("arg1","arg2");

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.report_container,fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if(isConnected && !isDemo) connection = new TCPConnection(this,ip);

        mHandler = new Handler();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(connection == null){
            connection = new TCPConnection(this,ip);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_fetch_report:
                Log.i(TAG,"fetch_report");
                fetchReport();
                return true;
            case R.id.action_save_report:
                saveReport();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void fetchReport()
    {
        reportRawData.clear();

        fragment.showLoading();

        Log.i(TAG,ip);

        if (isConnected && !isDemo) {
//            connection = new TCPConnection(this,ip);
            connection.fetchData(GetCmdEnum.GET_REPORT.get());
        }
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_report, container, false);
            return rootView;
        }
    }

    Runnable test = new Runnable() {
        @Override
        public void run() {
            fragment.updateList(reportList);
        }
    };

    private void saveReport(){


    }

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


    @TargetApi(Build.VERSION_CODES.KITKAT)
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

}
