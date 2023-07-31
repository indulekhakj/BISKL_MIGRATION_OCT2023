package com.example.bskl_kotlin.fragment.timetable

import ApiClient
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bskl_kotlin.R
import com.example.bskl_kotlin.common.PreferenceManager
import com.example.bskl_kotlin.common.model.StudentListResponseModel
import com.example.bskl_kotlin.fragment.absence.model.StudentModel
import com.example.bskl_kotlin.fragment.report.StudentSpinnerAdapter
import com.example.bskl_kotlin.fragment.timetable.adapter.TimeTableAllWeekSelectionAdapterNew
import com.example.bskl_kotlin.fragment.timetable.adapter.TimeTableSingleWeekSelectionAdapter
import com.example.bskl_kotlin.fragment.timetable.adapter.TimeTableWeekListAdapter
import com.example.bskl_kotlin.fragment.timetable.model.*
import com.example.bskl_kotlin.manager.AppUtils
import com.example.bskl_kotlin.recyclerviewmanager.DividerItemDecoration
import com.example.bskl_kotlin.recyclerviewmanager.RecyclerItemListener
import com.ryanharter.android.tooltips.ToolTipLayout
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TimeTableFragment:Fragment() {

    var alertTxtRelative: RelativeLayout? = null
    var CCAFRegisterRel: RelativeLayout? = null
   lateinit var studentsModelArrayList: ArrayList<StudentModel>
    var studentsModelArrayListCopy: ArrayList<StudentModel>? = null
  lateinit  var studentDetail: ListView
   lateinit var studentName: TextView
   lateinit var textViewYear: TextView
    var stud_id = ""
    var stud_class = ""
    var stud_name = ""
    var stud_img = ""
    var progressReport = ""
    var section = ""
    var alumini = ""
    var type = ""
   lateinit var noDataRelative: RelativeLayout
   lateinit var noDataTxt: TextView
   lateinit var noDataImg: ImageView
   lateinit var mStudentSpinner: LinearLayout
   lateinit var back: ImageView
   lateinit var home: ImageView
   lateinit  var studImg: ImageView
    var extras: Bundle? = null
    private var mRootView: View? = null
   lateinit var mContext: Context
    private val mTitle: String? = null
    private val mTabId: String? = null
   lateinit var relMain: RelativeLayout
    lateinit var mBannerImage: ImageView
    lateinit var timeTableSingleRecycler: RecyclerView
    lateinit var timeTableAllRecycler: RecyclerView
    lateinit var weekRecyclerList: RecyclerView
    lateinit var alertText: TextView
    private var weekListArray: ArrayList<WeekModel>? = null
    var mRangeModel: ArrayList<RangeModel>? = null
    var mFridayModelArraylist: ArrayList<DayModel>? = null
   lateinit var mPeriodModel: ArrayList<PeriodModel>
    var breakArrayList: ArrayList<String>? = null
    var mBreakArrayList: ArrayList<FieldModel>? = null
    var timeTableArrayList: ArrayList<StudentModel>? = null
    var primaryArrayList: ArrayList<StudentModel>? = null
    var secondaryArrayList: ArrayList<StudentModel>? = null
    var bothArrayList: ArrayList<StudentModel>? = null
    lateinit var weekListArrayString: ArrayList<String>
    var feildAPIArrayList = ArrayList<FieldModel>()

    var mTimetableApiArrayList = ArrayList<DayModel>()


    lateinit var mMondayArrayList: ArrayList<DayModel>
    lateinit var mTuesdayArrayList: ArrayList<DayModel>
    lateinit var mwednesdayArrayList: ArrayList<DayModel>
    lateinit var mThurdayArrayList: ArrayList<DayModel>
    lateinit var mFridayArrayList: ArrayList<DayModel>


    lateinit var card_viewAll: CardView
   lateinit var mFieldModel: ArrayList<FieldModel>
    var mTimeTableModelArrayList: ArrayList<TimeTableModel>? = null
    var weekPosition = 0
    var TimeTableWeekListAdapter: TimeTableWeekListAdapter? = null
    var dayOfTheWeek: String? = null
    lateinit var tipContainer: ToolTipLayout

    //var quickAction: QuickAction? = null
   // var quickIntent: QuickAction? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mRootView= inflater.inflate(R.layout.time_table_fragment, container, false)
        return mRootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        val headerTitle = actionBar!!.customView.findViewById<TextView>(R.id.headerTitle)
        val logoClickImgView = actionBar!!.customView.findViewById<ImageView>(R.id.logoClickImgView)
        val imageButton2 = actionBar!!.customView.findViewById<ImageView>(R.id.action_bar_forward)
        imageButton2.setImageResource(R.drawable.settings)
        headerTitle.setText("Safeguarding")
        headerTitle.visibility = View.VISIBLE
        logoClickImgView.visibility = View.INVISIBLE
        initialiseUI()

        weekListArray = ArrayList()
        for (i in weekListArrayString.indices) {
            val mWeekModel = WeekModel()
            //mWeekModel.setmId(String.valueOf(i));
            mWeekModel.weekName=(weekListArrayString.get(i))
            mWeekModel.positionSelected=(-1)
            weekListArray!!.add(mWeekModel)

        }
        TimeTableWeekListAdapter = TimeTableWeekListAdapter(mContext!!,
            weekListArray!!, weekPosition)
        weekRecyclerList!!.adapter = TimeTableWeekListAdapter
        mStudentSpinner!!.setOnClickListener { showSocialmediaList(studentsModelArrayList!!) }
        if (AppUtils().isNetworkConnected(mContext)) {
            getStudentsListAPI()
        } else {
            AppUtils().showDialogAlertDismiss(
                mContext as Activity?,
                "Network Error",
                getString(R.string.no_internet),
                R.drawable.nonetworkicon,
                R.drawable.roundred
            )
        }
}

     fun getStudentsListAPI() {
         studentsModelArrayList= ArrayList()
         val call: Call<StudentListResponseModel> = ApiClient.getClient.student_list( PreferenceManager().getaccesstoken(mContext).toString(),
                     PreferenceManager().getUserId(mContext).toString())

                 call.enqueue(object : Callback<StudentListResponseModel> {
                     override fun onFailure(call: Call<StudentListResponseModel>, t: Throwable) {
                         Log.e("Failed", t.localizedMessage)

                     }

                     override fun onResponse(
                         call: Call<StudentListResponseModel>,
                         response: Response<StudentListResponseModel>
                     ) {

                         val responsedata = response.body()

                         Log.e("Response Signup", responsedata.toString())

                         if (response.body()!!.responsecode.equals("200")) {
                             if (response.body()!!.response.statuscode.equals("303")) {

                                 if(response.body()!!.response.data.size>0)
                                 {
                                     for (i in response.body()!!.response.data.indices) {
                                         Log.e("alumi",
                                             response.body()!!.response.data.get(i).alumi.toString()
                                         )
                                         if(response.body()!!.response.data.get(i).alumi.equals("0"))
                                         {
                                            // studentsModelArrayListCopy!!.add(addStudentDetails(response.body()!!.response.data.get(i))!!)
                                             println("Student list size first" + response.body()!!.response.data.size)
                                             studentsModelArrayList!!.addAll(response.body()!!.response.data)

                                             println("Student list size first" + studentsModelArrayList!!.size)
                                         }
                                     }


                                     if (PreferenceManager().getStudIdForCCA(mContext)
                                             .equals("")
                                     ) {
                                         studentName.setText(studentsModelArrayList!![0].mName)
                                         stud_id = studentsModelArrayList!![0].mId.toString()
                                         stud_name = studentsModelArrayList!![0].mName.toString()
                                         stud_class = studentsModelArrayList!![0].mClass.toString()
                                         stud_img = studentsModelArrayList!![0].mPhoto.toString()
                                         progressReport =
                                             studentsModelArrayList!![0].progressreport.toString()
                                         section = studentsModelArrayList!![0].mSection.toString()
                                         alumini = studentsModelArrayList!![0].alumi.toString()
                                         type = studentsModelArrayList!![0].type.toString()
                                         if (stud_img != "") {
                                             Picasso.with(mContext).load(AppUtils().replace(stud_img))
                                                 .placeholder(R.drawable.boy).fit().into(studImg)
                                         } else {
                                             studImg.setImageResource(R.drawable.boy)
                                         }
                                         textViewYear.text =
                                             "Class : " + studentsModelArrayList!![0].mClass
                                         PreferenceManager().setCCAStudentIdPosition(mContext, "0")
                                     } else {
                                         val studentSelectPosition: Int = Integer.valueOf(
                                             PreferenceManager().getCCAStudentIdPosition(mContext)
                                         )
                                         studentName.setText(studentsModelArrayList!![studentSelectPosition].mName)
                                         stud_id =
                                             studentsModelArrayList!![studentSelectPosition].mId.toString()
                                         stud_name =
                                             studentsModelArrayList!![studentSelectPosition].mName.toString()
                                         stud_class =
                                             studentsModelArrayList!![studentSelectPosition].mClass.toString()
                                         progressReport =
                                             studentsModelArrayList!![studentSelectPosition].progressreport.toString()
                                         stud_img =
                                             studentsModelArrayList!![studentSelectPosition].mPhoto.toString()
                                         alumini =
                                             studentsModelArrayList!![studentSelectPosition].alumi.toString()
                                         type =
                                             studentsModelArrayList!![studentSelectPosition].type.toString()
                                         System.out.println("selected student image" + studentsModelArrayList!![studentSelectPosition].mPhoto)
                                         if (stud_img != "") {
                                             Picasso.with(mContext).load(AppUtils().replace(stud_img))
                                                 .placeholder(R.drawable.boy).fit().into(studImg)
                                         } else {
                                             studImg.setImageResource(R.drawable.boy)
                                         }
                                         textViewYear.text =
                                             "Class : " + studentsModelArrayList!![studentSelectPosition].mClass
                                     }

                                     Log.e("TYPE ", type)
                                     if (PreferenceManager().getTimeTableGroup(mContext)
                                             .equals("1")
                                     ) {
                                         if (type.equals("Primary", ignoreCase = true)) {
                                             timeTableSingleRecycler.visibility = View.VISIBLE
                                             timeTableAllRecycler.visibility = View.GONE
                                             if (AppUtils().isNetworkConnected(mContext)) {
                                                 println("test working")
                                                 getReportListAPI( 0)
                                             } else {
                                                 AppUtils().showDialogAlertDismiss(
                                                     mContext as Activity,
                                                     "Network Error",
                                                     getString(R.string.no_internet),
                                                     R.drawable.nonetworkicon,
                                                     R.drawable.roundred
                                                 )
                                             }
                                         } else {
                                             timeTableSingleRecycler.visibility = View.GONE
                                             timeTableAllRecycler.visibility = View.GONE
                                             AppUtils().showDialogAlertDismiss(
                                                 mContext as Activity,
                                                 getString(R.string.alert_heading),
                                                 getString(R.string.not_available_feature),
                                                 R.drawable.exclamationicon,
                                                 R.drawable.round
                                             )
                                         }
                                     } else if (PreferenceManager().getTimeTableGroup(mContext)
                                             .equals("2")
                                     ) {
                                         Log.e("TYPE1 ", type)
                                         if (type.equals("Secondary", ignoreCase = true)) {
                                             Log.e("TYPE2 ", type)
                                             timeTableSingleRecycler.visibility = View.VISIBLE
                                             timeTableAllRecycler.visibility = View.GONE
                                             if (AppUtils().isNetworkConnected(mContext)) {
                                                 println("test working")
                                                 getReportListAPI( 0)
                                             } else {
                                                 AppUtils().showDialogAlertDismiss(
                                                     mContext as Activity,
                                                     "Network Error",
                                                     getString(R.string.no_internet),
                                                     R.drawable.nonetworkicon,
                                                     R.drawable.roundred
                                                 )
                                             }
                                         } else {
                                             timeTableSingleRecycler.visibility = View.GONE
                                             timeTableAllRecycler.visibility = View.GONE
                                             AppUtils().showDialogAlertDismiss(
                                                 mContext as Activity,
                                                 getString(R.string.alert_heading),
                                                 getString(R.string.not_available_feature),
                                                 R.drawable.exclamationicon,
                                                 R.drawable.round
                                             )
                                         }
                                     } else if (PreferenceManager().getTimeTableGroup(mContext)
                                             .equals("3")
                                     ) {
                                         if (type.equals(
                                                 "Primary",
                                                 ignoreCase = true
                                             ) || type.equals(
                                                 "Secondary",
                                                 ignoreCase = true
                                             ) || type.equals("EYFS", ignoreCase = true)
                                         ) {
                                             timeTableSingleRecycler.visibility = View.VISIBLE
                                             timeTableAllRecycler.visibility = View.GONE
                                             if (AppUtils().isNetworkConnected(mContext)) {
                                                 println("test working")
                                                 getReportListAPI( 0)
                                             } else {
                                                 AppUtils().showDialogAlertDismiss(
                                                     mContext as Activity,
                                                     "Network Error",
                                                     getString(R.string.no_internet),
                                                     R.drawable.nonetworkicon,
                                                     R.drawable.roundred
                                                 )
                                             }
                                         } else {
                                             timeTableSingleRecycler.visibility = View.GONE
                                             timeTableAllRecycler.visibility = View.GONE
                                         }
                                     } else if (PreferenceManager().getTimeTableGroup(mContext)
                                             .equals("4")
                                     ) {
                                         if (type.equals("EYFS", ignoreCase = true)) {
                                             timeTableSingleRecycler.visibility = View.VISIBLE
                                             timeTableAllRecycler.visibility = View.GONE
                                             if (AppUtils().isNetworkConnected(mContext)) {
                                                 println("test working")
                                                 getReportListAPI( 0)
                                             } else {
                                                 AppUtils().showDialogAlertDismiss(
                                                     mContext as Activity,
                                                     "Network Error",
                                                     getString(R.string.no_internet),
                                                     R.drawable.nonetworkicon,
                                                     R.drawable.roundred
                                                 )
                                             }
                                         } else {
                                             timeTableSingleRecycler.visibility = View.GONE
                                             timeTableAllRecycler.visibility = View.GONE
                                             AppUtils().showDialogAlertDismiss(
                                                 mContext as Activity,
                                                 getString(R.string.alert_heading),
                                                 getString(R.string.not_available_feature),
                                                 R.drawable.exclamationicon,
                                                 R.drawable.round
                                             )
                                         }
                                     } else if (PreferenceManager().getTimeTableGroup(mContext)
                                             .equals("5")
                                     ) {
                                         if (type.equals(
                                                 "Primary",
                                                 ignoreCase = true
                                             ) || type.equals("Secondary", ignoreCase = true)
                                         ) {
                                             timeTableSingleRecycler.visibility = View.VISIBLE
                                             timeTableAllRecycler.visibility = View.GONE
                                             if (AppUtils().isNetworkConnected(mContext)) {
                                                 println("test working")
                                                 getReportListAPI( 0)
                                             } else {
                                                 AppUtils().showDialogAlertDismiss(
                                                     mContext as Activity,
                                                     "Network Error",
                                                     getString(R.string.no_internet),
                                                     R.drawable.nonetworkicon,
                                                     R.drawable.roundred
                                                 )
                                             }
                                         } else {
                                             timeTableSingleRecycler.visibility = View.GONE
                                             timeTableAllRecycler.visibility = View.GONE
                                             AppUtils().showDialogAlertDismiss(
                                                 mContext as Activity,
                                                 getString(R.string.alert_heading),
                                                 getString(R.string.not_available_feature),
                                                 R.drawable.exclamationicon,
                                                 R.drawable.round
                                             )
                                         }
                                     } else if (PreferenceManager().getTimeTableGroup(mContext)
                                             .equals("6")
                                     ) {
                                         if (type.equals(
                                                 "Primary",
                                                 ignoreCase = true
                                             ) || type.equals("EYFS", ignoreCase = true)
                                         ) {
                                             timeTableSingleRecycler.visibility = View.VISIBLE
                                             timeTableAllRecycler.visibility = View.GONE
                                             if (AppUtils().isNetworkConnected(mContext)) {
                                                 println("test working")
                                                 getReportListAPI( 0)
                                             } else {
                                                 AppUtils().showDialogAlertDismiss(
                                                     mContext as Activity,
                                                     "Network Error",
                                                     getString(R.string.no_internet),
                                                     R.drawable.nonetworkicon,
                                                     R.drawable.roundred
                                                 )
                                             }
                                         } else {
                                             timeTableSingleRecycler.visibility = View.GONE
                                             timeTableAllRecycler.visibility = View.GONE
                                         }
                                     } else if (PreferenceManager().getTimeTableGroup(mContext)
                                             .equals("7")
                                     ) {
                                         if (type.equals(
                                                 "EYFS",
                                                 ignoreCase = true
                                             ) || type.equals("Secondary", ignoreCase = true)
                                         ) {
                                             timeTableSingleRecycler.visibility = View.VISIBLE
                                             timeTableAllRecycler.visibility = View.GONE
                                             if (AppUtils().isNetworkConnected(mContext)) {
                                                 println("test working")
                                                 getReportListAPI( 0)
                                             } else {
                                                 AppUtils().showDialogAlertDismiss(
                                                     mContext as Activity,
                                                     "Network Error",
                                                     getString(R.string.no_internet),
                                                     R.drawable.nonetworkicon,
                                                     R.drawable.roundred
                                                 )
                                             }
                                         } else {
                                             timeTableSingleRecycler.visibility = View.GONE
                                             timeTableAllRecycler.visibility = View.GONE
                                         }
                                     }


                                 } else {
                                     AppUtils().showDialogAlertDismiss(
                                         mContext as Activity,
                                         "Alert",
                                         getString(R.string.student_not_available),
                                         R.drawable.exclamationicon,
                                         R.drawable.round
                                     )
                                 }
                             }
                             } else if (response.body()!!.responsecode.equals("500")) {
                             AppUtils().showDialogAlertDismiss(
                                 mContext as Activity,
                                 "Alert",
                                 getString(R.string.common_error),
                                 R.drawable.exclamationicon,
                                 R.drawable.round
                             )
                         } else {
                             AppUtils().showDialogAlertDismiss(
                                 mContext as Activity,
                                 "Alert",
                                 getString(R.string.common_error),
                                 R.drawable.exclamationicon,
                                 R.drawable.round
                             )
                         }
                     }

                 })
    }

    private fun getReportListAPI( i: Int) {
        mMondayArrayList= ArrayList()
        mTuesdayArrayList= ArrayList()
        mwednesdayArrayList= ArrayList()
        mThurdayArrayList= ArrayList()
        mFridayArrayList= ArrayList()
        mFridayArrayList = ArrayList()
        mFieldModel= ArrayList()
        val call: Call<TimetableResponseModel> = ApiClient.getClient.timtable_list(PreferenceManager().getaccesstoken(mContext).toString(),
           "3801")

        call.enqueue(object : Callback<TimetableResponseModel> {
            override fun onFailure(call: Call<TimetableResponseModel>, t: Throwable) {
                Log.e("Failed", t.localizedMessage)

            }

            override fun onResponse(
                call: Call<TimetableResponseModel>,
                response: Response<TimetableResponseModel>
            ) {

                val responsedata:String = response.body().toString()
                try {


                val obj = JSONObject(responsedata)
                val response_code = obj.getString("responsecode")
                Log.e("Response Signup", obj.toString())
                if (response.body()!!.responsecode.equals("200")) {
                    val secobj = obj.getJSONObject("response")

                    if (response.body()!!.response.statuscode.equals("303")) {


                       /* weekRecyclerList.visibility=View.VISIBLE

                        feildAPIArrayList.addAll(response.body()!!.response.field)
                        for (i in 0..feildAPIArrayList.size - 1) {
                            var model = FieldModel(
                                feildAPIArrayList.get(i).sortname,
                                feildAPIArrayList.get(i).starttime,
                                feildAPIArrayList.get(i).endtime
                            )
                            mFieldModel.add(model)
                        }

                        if (response.body()!!.response.range.Monday.isEmpty()&&response.body()!!.response.range.Tuesday.isEmpty()&&
                            response.body()!!.response.range.Wednesday.isEmpty()&&response.body()!!.response.range.Thursday.isEmpty()&&
                            response.body()!!.response.range.Friday.isEmpty())
                        {

                            timeTableSingleRecycler.visibility = View.GONE
                            timeTableAllRecycler.visibility = View.GONE
                            card_viewAll.visibility = View.GONE
                            weekRecyclerList.visibility = View.GONE
                           // Toast.makeText(ncontext, "No Data", Toast.LENGTH_SHORT).show()
                        }

                        else
                        {
                            mMondayArrayList.addAll(response.body()!!.response.range.Monday)
                            Log.e("monday", mMondayArrayList.get(0).day.toString())

                            mTuesdayArrayList.addAll(response.body()!!.response.range.Tuesday)
                            Log.e("tuesday", mTuesdayArrayList.get(0).day.toString())


                            mwednesdayArrayList.addAll(response.body()!!.response.range.Wednesday)
                            Log.e("wednesday", mwednesdayArrayList.get(0).day.toString())


                            mThurdayArrayList.addAll(response.body()!!.response.range.Thursday)
                            Log.e("thursday", mThurdayArrayList.get(0).day.toString())


                            mFridayArrayList.addAll(response.body()!!.response.range.Friday)
                            Log.e("friday", mFridayArrayList.get(0).day.toString())

                        }


                        timeTableSingleRecycler.visibility = View.GONE
                        timeTableAllRecycler.visibility = View.VISIBLE
                        card_viewAll.visibility = View.VISIBLE
                        mTimetableApiArrayList = ArrayList()


                        mTimetableApiArrayList.addAll(response.body()!!.response.Timetable)
                        mPeriodModel = ArrayList()
                        var mDataModelArrayList = ArrayList<DayModel>()

                        var m = 0
                        var tu = 0
                        var w = 0
                        var th = 0
                        var fr = 0
                        for (f in 0..feildAPIArrayList.size - 1) {
                            var mDayModel: DayModel = DayModel()
                            var mPeriod: PeriodModel = PeriodModel()

                            var timeTableListM = ArrayList<DayModel?>()
                            var timeTableListT = ArrayList<DayModel?>()
                            var timeTableListW = ArrayList<DayModel?>()
                            var timeTableListTh = ArrayList<DayModel?>()
                            var timeTableListF = ArrayList<DayModel?>()


                            for (t in 0..mTimetableApiArrayList.size - 1) {
                                if (feildAPIArrayList.get(f).sortname.equals(
                                        mTimetableApiArrayList.get(t).sortname
                                    )
                                ) {
                                    Log.e(
                                        "Sortname",
                                        mTimetableApiArrayList.get(t).sortname!!
                                    )

                                    mDayModel.id = mTimetableApiArrayList.get(t).id
                                    mDayModel.period_id =
                                        mTimetableApiArrayList.get(t).period_id
                                    mDayModel.day = mTimetableApiArrayList.get(t).day
                                    mDayModel.sortname =
                                        mTimetableApiArrayList.get(t).sortname
                                    mDayModel.starttime =
                                        mTimetableApiArrayList.get(t).starttime
                                    mDayModel.endtime =
                                        mTimetableApiArrayList.get(t).endtime
                                    mDayModel.subject_name =
                                        mTimetableApiArrayList.get(t).subject_name


                                    //    mDayModel.staff=mTimetableApiArrayList.get(t).staff

                                    if (mTimetableApiArrayList.get(t).day.equals("Monday")) {
                                        m = m + 1
                                        var dayModel = DayModel()
                                        dayModel.id = mTimetableApiArrayList.get(t).id
                                        dayModel.period_id =
                                            mTimetableApiArrayList.get(t).period_id
                                        dayModel.day = mTimetableApiArrayList.get(t).day
                                        dayModel.sortname =
                                            mTimetableApiArrayList.get(t).sortname
                                        dayModel.starttime =
                                            mTimetableApiArrayList.get(t).starttime
                                        dayModel.endtime =
                                            mTimetableApiArrayList.get(t).endtime
                                        dayModel.subject_name =
                                            mTimetableApiArrayList.get(t).subject_name

                                        dayModel.staff = mTimetableApiArrayList.get(t).staff
                                        timeTableListM.add(dayModel)
                                        mPeriod.monday =
                                            mTimetableApiArrayList.get(t).subject_name
                                       *//* Log.e(
                                            "staffname",
                                            mTimetableApiArrayList.get(t).staff!!
                                        )
                                        if (mTimetableApiArrayList.get(t).staff!!.equals("")) {
                                            mDayModel.isBreak=1
                                        } else {
                                            mDayModel.isBreak=0

                                        }*//*
                                    } else if (mTimetableApiArrayList.get(t).day.equals("Tuesday")) {
                                        tu = tu + 1
                                        var dayModel = DayModel()
                                        dayModel.id = mTimetableApiArrayList.get(t).id
                                        dayModel.period_id =
                                            mTimetableApiArrayList.get(t).period_id
                                        dayModel.subject_name =
                                            mTimetableApiArrayList.get(t).subject_name
                                        dayModel.staff = mTimetableApiArrayList.get(t).staff

                                        dayModel.day = mTimetableApiArrayList.get(t).day
                                        dayModel.sortname =
                                            mTimetableApiArrayList.get(t).sortname
                                        dayModel.starttime =
                                            mTimetableApiArrayList.get(t).starttime
                                        dayModel.endtime =
                                            mTimetableApiArrayList.get(t).endtime
                                        timeTableListT.add(dayModel)
                                        mPeriod.tuesday =
                                            mTimetableApiArrayList.get(t).subject_name
                                    } else if (mTimetableApiArrayList.get(t).day.equals("Wednesday")) {
                                        w = w + 1
                                        var dayModel = DayModel()
                                        dayModel.id = mTimetableApiArrayList.get(t).id
                                        dayModel.period_id =
                                            mTimetableApiArrayList.get(t).period_id
                                        dayModel.day = mTimetableApiArrayList.get(t).day
                                        dayModel.sortname =
                                            mTimetableApiArrayList.get(t).sortname
                                        dayModel.starttime =
                                            mTimetableApiArrayList.get(t).starttime
                                        dayModel.endtime =
                                            mTimetableApiArrayList.get(t).endtime
                                        dayModel.subject_name =
                                            mTimetableApiArrayList.get(t).subject_name

                                        dayModel.staff = mTimetableApiArrayList.get(t).staff
                                        timeTableListW.add(dayModel)
                                        mPeriod.wednesday =
                                            mTimetableApiArrayList.get(t).subject_name
                                    } else if (mTimetableApiArrayList.get(t).day.equals("Thursday")) {
                                        th = th + 1
                                        var dayModel = DayModel()
                                        dayModel.id = mTimetableApiArrayList.get(t).id
                                        dayModel.period_id =
                                            mTimetableApiArrayList.get(t).period_id
                                        dayModel.day = mTimetableApiArrayList.get(t).day
                                        dayModel.sortname =
                                            mTimetableApiArrayList.get(t).sortname
                                        dayModel.starttime =
                                            mTimetableApiArrayList.get(t).starttime
                                        dayModel.endtime =
                                            mTimetableApiArrayList.get(t).endtime
                                        dayModel.subject_name =
                                            mTimetableApiArrayList.get(t).subject_name

                                        dayModel.staff = mTimetableApiArrayList.get(t).staff
                                        timeTableListTh.add(dayModel)
                                        mPeriod.thursday =
                                            mTimetableApiArrayList.get(t).subject_name
                                    } else if (mTimetableApiArrayList.get(t).day.equals("Friday")) {
                                        fr = f + 1
                                        var dayModel = DayModel()
                                        dayModel.id = mTimetableApiArrayList.get(t).id
                                        dayModel.period_id =
                                            mTimetableApiArrayList.get(t).period_id
                                        dayModel.day = mTimetableApiArrayList.get(t).day
                                        dayModel.sortname =
                                            mTimetableApiArrayList.get(t).sortname
                                        dayModel.starttime =
                                            mTimetableApiArrayList.get(t).starttime
                                        dayModel.endtime =
                                            mTimetableApiArrayList.get(t).endtime
                                        dayModel.subject_name =
                                            mTimetableApiArrayList.get(t).subject_name
                                        dayModel.staff = mTimetableApiArrayList.get(t).staff
                                        timeTableListF.add(dayModel)
                                        mPeriod.friday =
                                            mTimetableApiArrayList.get(t).subject_name
                                    } else {

                                        mPeriod.monday = ""
                                        mPeriod.tuesday = ""
                                        mPeriod.wednesday = ""
                                        mPeriod.thursday = ""
                                        mPeriod.friday = ""
                                    }
                                    mPeriod.countM = m
                                    mPeriod.countT = tu
                                    mPeriod.countW = w
                                    mPeriod.countTh = th
                                    mPeriod.countF = fr


                                    mPeriod.timeTableListM = timeTableListM
                                    mPeriod.timeTableListT = timeTableListT
                                    mPeriod.timeTableListW = timeTableListW
                                    mPeriod.timeTableListTh = timeTableListTh
                                    mPeriod.timeTableListF = timeTableListF

                                }
                            }
                        }
                        if (weekPosition != 0) {
                            timeTableAllRecycler.visibility = View.GONE
                            card_viewAll.visibility = View.GONE
                            //  timeTableAllRecycler.visibility=View.GONE
                            tipContainer.visibility = View.GONE
                            timeTableSingleRecycler.visibility = View.VISIBLE
//                    if (mRangeModel.size>0)
//                    {
                            //card_viewAll.visibility = View.GONE
                            // timeTableAllRecycler.visibility = View.GONE
                            timeTableSingleRecycler.visibility = View.VISIBLE
                            // timeTableAllRecycler.visibility = View.VISIBLE
                            Log.e("weekposition", weekPosition.toString())
                            if (weekPosition == 1) {

                                var mRecyclerViewMainAdapter =
                                    TimeTableSingleWeekSelectionAdapter(mContext, mMondayArrayList)
                                timeTableSingleRecycler.adapter = mRecyclerViewMainAdapter
                            } else if (weekPosition == 2) {
                                Log.e("week","Successs")
                                var mRecyclerViewMainAdapter =
                                    TimeTableSingleWeekSelectionAdapter(mContext, mTuesdayArrayList)
                                timeTableSingleRecycler.adapter = mRecyclerViewMainAdapter

                            } else if (weekPosition == 3) {
                                Log.e("Success","Successs")
                                var mRecyclerViewMainAdapter =
                                    TimeTableSingleWeekSelectionAdapter(mContext, mwednesdayArrayList)
                                timeTableSingleRecycler.adapter = mRecyclerViewMainAdapter
                            } else if (weekPosition == 4) {
                                Log.e("Failed","Successs")
                                var mRecyclerViewMainAdapter =
                                    TimeTableSingleWeekSelectionAdapter(mContext, mThurdayArrayList)
                                timeTableSingleRecycler.adapter = mRecyclerViewMainAdapter
                            } else if (weekPosition == 5) {

                                var mRecyclerViewMainAdapter =
                                    TimeTableSingleWeekSelectionAdapter(mContext, mFridayArrayList)
                                timeTableSingleRecycler.adapter = mRecyclerViewMainAdapter
                            }
                        }
                        else {
                            timeTableSingleRecycler.visibility = View.GONE
                            // timeTableAllRecycler.visibility = View.VISIBLE
                            tipContainer.visibility = View.VISIBLE

                            card_viewAll.visibility = View.VISIBLE


                            timeTableAllRecycler.visibility = View.VISIBLE
                            // timeTableListS.shuffle()
                            var mRecyclerAllAdapter =
                                TimeTableAllWeekSelectionAdapterNew(mContext, mPeriodModel, feildAPIArrayList,tipContainer)
                            timeTableAllRecycler.adapter = mRecyclerAllAdapter

                        }*/
                        val feildArray = secobj.getJSONArray("field1")

                        mFieldModel = ArrayList()
                        if (response.body()!!.response.field1.size > 0) {
                            var lun = 0
                            for (i in response.body()!!.response.field1.indices) {
                                val listObject: JSONObject = feildArray.optJSONObject(i)
                                val xmodel = FieldModel()
                                xmodel.sortname=(response.body()!!.response.field1.get(i).sortname)
                                xmodel.starttime=(response.body()!!.response.field1.get(i).starttime)
                                xmodel.endtime=(response.body()!!.response.field1.get(i).endtime)
                                Log.e("jsonobjet",
                                    listObject.toString()
                                )
                                System.out.print("jsonobjet"+response.body()!!.response.field1.get(i))
                                if (listObject.has("period_id")) {
                                    xmodel.periodId=(response.body()!!.response.field1.get(i).periodId)
                                    lun = lun + 1
                                    println("lun::$lun")
                                    xmodel.countBreak=(lun)
                                } else {
                                    xmodel.periodId=""
                                }
                                mFieldModel.add(xmodel)
                            }
                        }
                    }

                        val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
                        mRangeModel = ArrayList()
                        mFridayModelArraylist = ArrayList()
                        mBreakArrayList = ArrayList()

                        for (i in 0..4) {
                            println("i value$i")

                          //  println("size of monday array" + monday.length())
                            var monday1: ArrayList<DayModel>
                            monday1= ArrayList()
                           // monday1= response.body()!!.response.range
                            val rangeModel = RangeModel()
                            val mDayModel = ArrayList<DayModel>()


                            if (response.body()!!.response.range.equals("")) {
                                timeTableSingleRecycler.visibility = View.VISIBLE
                                println("working success")
                                /*for (j in 0 until monday.length()) {
                                    val dataObject = monday.getJSONObject(j)
                                    val model = DayModel()
                                    model.setId(dataObject.optString("id"))
                                    model.setPeriod_id(dataObject.optString("period_id"))
                                    model.setEndtime(dataObject.optString("endtime"))
                                    model.setStarttime(dataObject.optString("starttime"))
                                    if (dataObject.has("staff")) {
                                        model.setIsBreak(0)
                                    } else {
                                        model.setIsBreak(1)
                                    }
                                    model.setSubject_name(dataObject.optString("subject_name"))
                                    model.setStaff(dataObject.optString("staff"))
                                    model.setSortname(dataObject.optString("sortname"))
                                    model.setStudent_id(dataObject.optString("student_id"))
                                    model.setDay(dataObject.optString("day"))
                                    if (i == 4) {
                                        mFridayModelArraylist!!.add(model)
                                    }
                                    if (!dataObject.optString("sortname")
                                            .equals("BUS", ignoreCase = true)
                                    ) mDayModel.add(model)
                                }*/
                            }
                            println("table range model size" + mDayModel.size)
                          //  rangeModel.setTimeTableDayModel(mDayModel)
                            mRangeModel!!.add(rangeModel)
                        }
                    }
                }catch (e: JSONException) {
            }
            }
        })
    }

    private fun initialiseUI() {
        weekListArrayString = ArrayList()
        weekListArrayString.add("ALL")
        weekListArrayString.add("MONDAY")
        weekListArrayString.add("TUESDAY")
        weekListArrayString.add("WEDNESDAY")
        weekListArrayString.add("THURSDAY")
        weekListArrayString.add("FRIDAY")
        if (extras != null) {
        }
//
        //
        breakArrayList = java.util.ArrayList()
        mRangeModel = java.util.ArrayList()
        mFridayModelArraylist = java.util.ArrayList()
        mPeriodModel = java.util.ArrayList()
        timeTableArrayList = java.util.ArrayList()
        primaryArrayList = java.util.ArrayList()
        secondaryArrayList = java.util.ArrayList()
        bothArrayList = java.util.ArrayList()
        alertText = mRootView!!.findViewById(R.id.noDataTxt)
        mStudentSpinner = mRootView!!.findViewById(R.id.studentSpinner)
        studentName = mRootView!!.findViewById(R.id.studentName)
        studImg = mRootView!!.findViewById(R.id.imagicon)
        noDataImg = mRootView!!.findViewById(R.id.noDataImg)
        timeTableSingleRecycler = mRootView!!.findViewById(R.id.timeTableSingleRecycler)
        weekRecyclerList = mRootView!!.findViewById(R.id.weekRecyclerList)
        timeTableAllRecycler = mRootView!!.findViewById(R.id.timeTableAllRecycler)
        noDataRelative = mRootView!!.findViewById(R.id.noDataRelative)
        relMain = mRootView!!.findViewById(R.id.relMain)
        alertTxtRelative = mRootView!!.findViewById(R.id.noDataRelative)
        textViewYear = mRootView!!.findViewById(R.id.textViewYear)
        noDataTxt = mRootView!!.findViewById(R.id.noDataTxt)
        card_viewAll = mRootView!!.findViewById(R.id.card_viewAll)
        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        weekRecyclerList.layoutManager = llm
        tipContainer = mRootView!!.findViewById<View>(R.id.tooltip_container) as ToolTipLayout
        val llmAll = LinearLayoutManager(activity)
        llmAll.orientation = LinearLayoutManager.VERTICAL
        timeTableAllRecycler.layoutManager = llmAll
        /*LayoutInflater.from(mContext).inflate(R.layout.layout_timetable_popup, null);*/

//        timeTableAllRecycler.addOnItemTouchListener(new RecyclerItemListener(mContext, timeTableAllRecycler, new RecyclerItemListener.RecyclerTouchListener() {
//            @Override
//            public void onClickItem(View v, int position) {
//
//
//            }
//
//            @Override
//            public void onLongClickItem(View v, int position) {
//
//            }
//        }));
        val llmm = LinearLayoutManager(activity)
        llmm.orientation = LinearLayoutManager.VERTICAL
        timeTableSingleRecycler.layoutManager = llmm
        val sdf = SimpleDateFormat("EEEE")
        val d = Date()
        dayOfTheWeek = sdf.format(d)
        println("Current day$dayOfTheWeek")
        weekPosition = when (dayOfTheWeek) {
            "Monday" -> 1
            "Tuesday" -> 2
            "Wednesday" -> 3
            "Thursday" -> 4
            "Friday" -> 5
            else -> 0
        }
        relMain.setOnClickListener { }
        weekRecyclerList.addOnItemTouchListener(
            RecyclerItemListener(mContext, weekRecyclerList,
                object : RecyclerItemListener.RecyclerTouchListener {
                    override fun onClickItem(v: View?, position: Int) {
                        println("clicked position$position")
                        weekPosition = position
                        if (weekPosition < 3) {
                            weekRecyclerList.scrollToPosition(0)
                        } else {
                            weekRecyclerList.scrollToPosition(5)
                        }
                        for (i in weekListArray!!.indices) {
                            if (i == position) {
                                weekListArray!![i].positionSelected=(i)
                            } else {
                                weekListArray!![i].positionSelected=(-1)
                            }
                        }
                        TimeTableWeekListAdapter!!.notifyDataSetChanged()
                        if (position != 0) {
                            card_viewAll.setVisibility(View.GONE)
                            timeTableAllRecycler.visibility = View.GONE
                            tipContainer!!.visibility = View.GONE
                            timeTableSingleRecycler.visibility = View.VISIBLE
                            if (mRangeModel!!.size > 0) {
                                val mRecyclerViewMainAdapter = TimeTableSingleWeekSelectionAdapter(
                                    mContext,
                                    mRangeModel!![position - 1].timeTableDayModel!!
                                )
                                timeTableSingleRecycler.adapter = mRecyclerViewMainAdapter
                            }
                        } else {
                            timeTableSingleRecycler.visibility = View.GONE
                            timeTableAllRecycler.visibility = View.VISIBLE
                            tipContainer!!.visibility = View.VISIBLE
                            card_viewAll.setVisibility(View.VISIBLE)
                            if (mPeriodModel!!.size > 0) {
                                val mRecyclerAllAdapter = TimeTableAllWeekSelectionAdapterNew(
                                    mContext, mPeriodModel, feildAPIArrayList,tipContainer
                                )
                                timeTableAllRecycler.adapter = mRecyclerAllAdapter
                            }
                        }
                    }

                    override fun onLongClickItem(v: View?, position: Int) {
                        println("On Long Click Item interface")
                    }
                })
        )
    }
    fun showSocialmediaList(mStudentArray: java.util.ArrayList<StudentModel>) {
        val dialog = Dialog(mContext!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_student_list)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogDismiss = dialog.findViewById<Button>(R.id.btn_dismiss)
        val iconImageView = dialog.findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setImageResource(R.drawable.boy)
        val socialMediaList = dialog.findViewById<RecyclerView>(R.id.recycler_view_social_media)
        //if(mSocialMediaArray.get())
        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            dialogDismiss.setBackgroundDrawable(mContext!!.resources.getDrawable(R.drawable.button_new))
        } else {
            dialogDismiss.background = mContext!!.resources.getDrawable(R.drawable.button_new)
        }
        socialMediaList.addItemDecoration(DividerItemDecoration(mContext!!.resources.getDrawable(R.drawable.list_divider)))
        socialMediaList.setHasFixedSize(true)
        val llm = LinearLayoutManager(mContext)
        llm.orientation = LinearLayoutManager.VERTICAL
        socialMediaList.layoutManager = llm
        val studentAdapter = StudentSpinnerAdapter(mContext!!, mStudentArray)
        socialMediaList.adapter = studentAdapter
        dialogDismiss.setOnClickListener { dialog.dismiss() }
        socialMediaList.addOnItemTouchListener(
            RecyclerItemListener(mContext, socialMediaList,
                object : RecyclerItemListener.RecyclerTouchListener {
                    override fun onClickItem(v: View?, position: Int) {
                        dialog.dismiss()
                        studentName.setText(mStudentArray[position].mName)
                        stud_id = mStudentArray[position].mId.toString()
                        stud_name = mStudentArray[position].mName.toString()
                        stud_class = mStudentArray[position].mClass.toString()
                        stud_img = mStudentArray[position].mPhoto.toString()
                        section = mStudentArray[position].mSection.toString()
                        progressReport = mStudentArray[position].progressreport.toString()
                        alumini = mStudentArray[position].alumi.toString()
                        type = mStudentArray[position].type.toString()
                        textViewYear!!.text = "Class : " + mStudentArray[position].mClass
                        if (stud_img != "") {
                            Picasso.with(mContext).load(AppUtils().replace(stud_img))
                                .placeholder(R.drawable.boy).fit().into(studImg)
                        } else {
                            studImg!!.setImageResource(R.drawable.boy)
                        }
                        if (PreferenceManager().getTimeTableGroup(mContext).equals("1")) {
                            if (type.equals("Primary", ignoreCase = true)) {
                                Log.e("PRIMARY", "WORKS")
                                timeTableSingleRecycler!!.visibility = View.VISIBLE
                                timeTableAllRecycler!!.visibility = View.GONE
                                if (AppUtils().isNetworkConnected(mContext)) {
                                    println("test working")
                                    getReportListAPI( 1)
                                } else {
                                    AppUtils().showDialogAlertDismiss(
                                        mContext as Activity?,
                                        "Network Error",
                                        getString(R.string.no_internet),
                                        R.drawable.nonetworkicon,
                                        R.drawable.roundred
                                    )
                                }
                            } else {
                                timeTableSingleRecycler!!.visibility = View.GONE
                                timeTableAllRecycler!!.visibility = View.GONE
                                AppUtils().showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.alert_heading),
                                    getString(R.string.not_available_feature),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                                )
                            }
                        } else if (PreferenceManager().getTimeTableGroup(mContext)
                                .equals("2")
                        ) {
                            if (type.equals("Secondary", ignoreCase = true)) {
                                Log.e("SECONDARY", "WORKS")
                                timeTableSingleRecycler!!.visibility = View.VISIBLE
                                timeTableAllRecycler!!.visibility = View.GONE
                                if (AppUtils().isNetworkConnected(mContext)) {
                                    println("test working")
                                    getReportListAPI( 1)
                                } else {
                                    AppUtils().showDialogAlertDismiss(
                                        mContext as Activity?,
                                        "Network Error",
                                        getString(R.string.no_internet),
                                        R.drawable.nonetworkicon,
                                        R.drawable.roundred
                                    )
                                }
                            } else {
                                timeTableSingleRecycler!!.visibility = View.GONE
                                timeTableAllRecycler!!.visibility = View.GONE
                                AppUtils().showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.alert_heading),
                                    getString(R.string.not_available_feature),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                                )
                            }
                        } else if (PreferenceManager().getTimeTableGroup(mContext)
                                .equals("3")
                        ) {
                            if (type.equals(
                                    "Primary",
                                    ignoreCase = true
                                ) || type.equals(
                                    "Secondary",
                                    ignoreCase = true
                                ) || type.equals("EYFS", ignoreCase = true)
                            ) {
                                timeTableSingleRecycler!!.visibility = View.VISIBLE
                                timeTableAllRecycler!!.visibility = View.GONE
                                if (AppUtils().isNetworkConnected(mContext)) {
                                    println("test working")
                                    getReportListAPI( 1)
                                } else {
                                    AppUtils().showDialogAlertDismiss(
                                        mContext as Activity?,
                                        "Network Error",
                                        getString(R.string.no_internet),
                                        R.drawable.nonetworkicon,
                                        R.drawable.roundred
                                    )
                                }
                            } else {
                                timeTableSingleRecycler!!.visibility = View.GONE
                                timeTableAllRecycler!!.visibility = View.GONE
                            }
                        } else if (PreferenceManager().getTimeTableGroup(mContext)
                                .equals("4")
                        ) {
                            if (type.equals("EYFS", ignoreCase = true)) {
                                timeTableSingleRecycler!!.visibility = View.VISIBLE
                                timeTableAllRecycler!!.visibility = View.GONE
                                if (AppUtils().isNetworkConnected(mContext)) {
                                    println("test working")
                                    getReportListAPI(1)
                                } else {
                                    AppUtils().showDialogAlertDismiss(
                                        mContext as Activity?,
                                        "Network Error",
                                        getString(R.string.no_internet),
                                        R.drawable.nonetworkicon,
                                        R.drawable.roundred
                                    )
                                }
                            } else {
                                timeTableSingleRecycler!!.visibility = View.GONE
                                timeTableAllRecycler!!.visibility = View.GONE
                                AppUtils().showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.alert_heading),
                                    getString(R.string.not_available_feature),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                                )
                            }
                        } else if (PreferenceManager().getTimeTableGroup(mContext)
                                .equals("5")
                        ) {
                            if (type.equals(
                                    "Primary",
                                    ignoreCase = true
                                ) || type.equals("Secondary", ignoreCase = true)
                            ) {
                                timeTableSingleRecycler!!.visibility = View.VISIBLE
                                timeTableAllRecycler!!.visibility = View.GONE
                                if (AppUtils().isNetworkConnected(mContext)) {
                                    println("test working")
                                    getReportListAPI( 1)
                                } else {
                                    AppUtils().showDialogAlertDismiss(
                                        mContext as Activity?,
                                        "Network Error",
                                        getString(R.string.no_internet),
                                        R.drawable.nonetworkicon,
                                        R.drawable.roundred
                                    )
                                }
                            } else {
                                timeTableSingleRecycler!!.visibility = View.GONE
                                timeTableAllRecycler!!.visibility = View.GONE
                                AppUtils().showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.alert_heading),
                                    getString(R.string.not_available_feature),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                                )
                            }
                        } else if (PreferenceManager().getTimeTableGroup(mContext)
                                .equals("6")
                        ) {
                            if (type.equals("Primary", ignoreCase = true) || type.equals(
                                    "EYFS",
                                    ignoreCase = true
                                )
                            ) {
                                timeTableSingleRecycler!!.visibility = View.VISIBLE
                                timeTableAllRecycler!!.visibility = View.GONE
                                if (AppUtils().isNetworkConnected(mContext)) {
                                    println("test working")
                                    getReportListAPI( 1)
                                } else {
                                    AppUtils().showDialogAlertDismiss(
                                        mContext as Activity?,
                                        "Network Error",
                                        getString(R.string.no_internet),
                                        R.drawable.nonetworkicon,
                                        R.drawable.roundred
                                    )
                                }
                            } else {
                                timeTableSingleRecycler!!.visibility = View.GONE
                                timeTableAllRecycler!!.visibility = View.GONE
                                AppUtils().showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.alert_heading),
                                    getString(R.string.not_available_feature),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                                )
                            }
                        } else if (PreferenceManager().getTimeTableGroup(mContext)
                                .equals("7")
                        ) {
                            if (type.equals("EYFS", ignoreCase = true) || type.equals(
                                    "Secondary",
                                    ignoreCase = true
                                )
                            ) {
                                timeTableSingleRecycler!!.visibility = View.VISIBLE
                                timeTableAllRecycler!!.visibility = View.GONE
                                if (AppUtils().isNetworkConnected(mContext)) {
                                    println("test working")
                                    getReportListAPI( 1)
                                } else {
                                    AppUtils().showDialogAlertDismiss(
                                        mContext as Activity?,
                                        "Network Error",
                                        getString(R.string.no_internet),
                                        R.drawable.nonetworkicon,
                                        R.drawable.roundred
                                    )
                                }
                            } else {
                                timeTableSingleRecycler!!.visibility = View.GONE
                                timeTableAllRecycler!!.visibility = View.GONE
                                AppUtils().showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.alert_heading),
                                    getString(R.string.not_available_feature),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                                )
                            }
                        }
                    }

                    override fun onLongClickItem(v: View?, position: Int) {
                        println("On Long Click Item interface")
                    }
                })
        )
        dialog.show()
    }
    private fun addStudentDetails(dataObject: StudentModel): StudentModel? {
        val studentModel = StudentModel()
        studentModel.mId=(dataObject.mId)
        studentModel.mName=(dataObject.mName)
        studentModel.mClass=(dataObject.mClass)
        studentModel.mSection=(dataObject.mSection)
        studentModel.mHouse=(dataObject.mHouse)
        studentModel.mPhoto=(dataObject.mPhoto)
        studentModel.progressreport=(dataObject.progressreport)
        studentModel.alumi=(dataObject.alumi)
        studentModel.type=(dataObject.type)
        return studentModel
    }
}