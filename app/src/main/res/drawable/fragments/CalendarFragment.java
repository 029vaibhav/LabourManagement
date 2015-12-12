package drawable.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.Decorators;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.AttendanceDates;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CalendarFragment extends Fragment {

    public static String TAG = "CalendarFragment";

    private CalendarPickerView calendar;
    ArrayList<String> dates;
    String username;


    public static CalendarFragment newInstance(String param1) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString("username", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments() != null ? getArguments().getString("username") : "";


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        calendar = (CalendarPickerView) viewGroup.findViewById(R.id.calendar_view);
        calendar.init(DateTime.now().minusYears(1).toDate(), DateTime.now().plusDays(2).toDate())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        new GetAttendanceList().execute();


        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);


//        dates = new ArrayList<String>();
//
//        for(int i=1;i<12;i++)
//        {
//
//            DateTime dateTime = new DateTime(DateTime.now().getYear(),i,
//                    i,0,0);
//            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = null;
//            try {
//                date = format.parse(dateTime.toString("yyyy-MM-dd"));
//                Log.e("hi",date.toString());
//                dates.add(dateTime.toString("yyyy-MM-dd"));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//
//
//        }


//        calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
//        calendar.init(new Date(), nextYear.getTime()) //
//                .inMode(CalendarPickerView.SelectionMode.MULTIPLE) //
//                .withSelectedDates(dates);

        return viewGroup;
    }


    private class GetAttendanceList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            Client.INSTANCE.getUserAttendance(MkShop.AUTH,username, new Callback<List<AttendanceDates>>() {
                @Override
                public void success(List<AttendanceDates> attendanceDates, Response response) {

                    List<String> dates = new ArrayList<String>();
                    for (int i = attendanceDates.size() - 1; i >= 0; i--) {
                        dates.add(attendanceDates.get(i).getDate());
                    }


                    calendar.setDecorators(Arrays.<CalendarCellDecorator>asList(new Decorators(dates)));
                    calendar.init(DateTime.now().minusMonths(6).toDate(), DateTime.now().plusDays(2).toDate())
                            .inMode(CalendarPickerView.SelectionMode.MULTIPLE);


                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

            return null;
        }
    }
}
