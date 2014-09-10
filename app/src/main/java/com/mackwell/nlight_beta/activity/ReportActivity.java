package com.mackwell.nlight_beta.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.models.Report;
import com.mackwell.nlight_beta.socket.TCPConnection;
import com.mackwell.nlight_beta.util.Constants;
import com.mackwell.nlight_beta.util.DataParser;
import com.mackwell.nlight_beta.util.GetCmdEnum;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ReportActivity extends BaseActivity implements ReportFragment.OnListItemClickedListener {

    private static final String TAG = "ReportActivity";
    private static final String TAG_RECEIVE = "ReportActivity_Receive";
    private static final int ROW_PER_PAGE = 20;

    private String ip;
    private String location;
    private List<Integer> reportRawData;
    private List<Report> reportList;
    private Handler mHandler;
    private ReportFragment fragment;

    //properties for pdf print

    private int pageHeight;
    private int pageWidth;
    public int totalpages;


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
        location = getIntent().getStringExtra("location");
        isDemo = getIntent().getBooleanExtra("demo",true);
        reportRawData = new ArrayList<Integer>();
        reportList = new ArrayList<Report>();

        //init reportList in demo mode
        if(isDemo) reportList = initReportList();

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
        if(connection == null && isConnected && !isDemo){
            connection = new TCPConnection(this,ip);

        }

        if(isDemo) fragment.updateList(reportList);

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
            return inflater.inflate(R.layout.fragment_report, container, false);
        }
    }

    Runnable test = new Runnable() {
        @Override
        public void run() {
            fragment.updateList(reportList);
        }
    };

    private void saveReport(){
        Log.i("printPDF","print clicked");

        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) + " Document";

        printManager.print(jobName,new MyPrintDocumentAdapter(this),null);

    }


    private List<Report> initReportList(){

        List<Report> list = new ArrayList<Report>();
        Report report;
        List<List<Integer>> list2;
        List<Integer> list3;

        for(int i=0; i<37; i++){

            report = new Report(1, Calendar.getInstance(),true);

            list2 = new ArrayList<List<Integer>>();
            list3 =  Arrays.asList(1,130,255,255,255,255);
            list2.add(list3);
            report.setFaultyDeviceList(list2);
            list.add(report);
        }







        return list;
    }

    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        Context context;

        private String outputName;

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

            outputName = getResources().getString(R.string.app_name) + ".pdf";

            pageHeight = newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            ReportActivity.this.pageHeight = pageHeight;

            pageWidth = newAttributes.getMediaSize().getWidthMils()/1000 * 72;
            ReportActivity.this.pageWidth = pageWidth;

            if (cancellationSignal.isCanceled()) {
                layoutResultCallback.onLayoutCancelled();
                return;
            }

            //calculate total page number



            totalpages = ((reportList.size() * 33) / (pageHeight-150)) + 1;
            ReportActivity.this.totalpages = totalpages;


            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder(outputName)
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
        for (PageRange pageRange : pageRanges) {

            if ((page >= pageRange.getStart()) && (page <= pageRange.getEnd())) {
                return true;
            }
        }
        return false;

    }


    private void drawPage(PdfDocument.Page page, int pagenumber)
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Canvas canvas = page.getCanvas();


        int imageBaseLine = 10;
        int imageLeftMargin = 100;
        int titleBaseLine = 130;
        int leftMargin = 54;
        int rightMargin = 104;
        int bottomMargin = 20;



        Paint paint = new Paint();
        paint.setColor(Color.BLACK);


        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.nlight_connect);

        Matrix matrix=new Matrix();
        matrix.postScale(0.15f, 0.15f);
        Bitmap dstbmp=Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
        canvas.drawBitmap(dstbmp, imageLeftMargin, imageBaseLine, null);

        paint.setTextSize(40);
        canvas.drawText("Panel report" , leftMargin, titleBaseLine, paint);

        paint.setTextSize(15);

        canvas.drawText("Report type: Panel status report", leftMargin,titleBaseLine + 40,paint);

        canvas.drawText("Panel location: " + location, leftMargin,titleBaseLine + 60,paint);

        paint.setTextSize(12);

        canvas.drawText("Date/Time", leftMargin,titleBaseLine + 100,paint);
        canvas.drawText("Fault(s) found", leftMargin + 170,titleBaseLine + 100,paint);
        canvas.drawText("Status", leftMargin + 320,titleBaseLine  + 100,paint);
        canvas.drawLine(leftMargin, titleBaseLine  + 108,leftMargin+450,titleBaseLine+108,paint);

        int remain = reportList.size() - pagenumber* ROW_PER_PAGE;
        int n = remain > ROW_PER_PAGE? (pagenumber+1) * ROW_PER_PAGE: reportList.size();

        Report report;
        for(int i=(pagenumber * ROW_PER_PAGE),j=0; i<n ; i++,j++) {
            report = reportList.get(i);
            canvas.drawText(dateFormat.format(report.getDate().getTime()), leftMargin, titleBaseLine + 125 + j*25, paint);
            canvas.drawText(Integer.toString(report.getFaults()), leftMargin + 200, titleBaseLine + 125 + j*25, paint);
            canvas.drawText(report.isFaulty() ? "Fault(s) found" : "OK", leftMargin + 330, titleBaseLine + 125 + j*25, paint);
            canvas.drawLine(leftMargin, titleBaseLine + 133 + j*25, leftMargin + 450, titleBaseLine + 133 + j*25, paint);
        }

        pagenumber ++ ;

        canvas.drawText("Page " + Integer.toString(pagenumber),pageWidth-rightMargin,pageHeight-bottomMargin,paint);


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
