package com.example.todolist

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItem
import kotlinx.android.synthetic.main.activity_dash_board.*
import java.util.*

class DashBoardActivity : AppCompatActivity() {
    lateinit var alarmCalendar: Calendar
    lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        loadLocate() //Call LoadLocale

        setSupportActionBar(dashboard_toolbar)
        alarmCalendar = Calendar.getInstance()
        dbHandler = DBHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)
        // ListViewを作成
        val arrayAdapter: ArrayAdapter<*>
        val users = CompletedList()
        val mlist = findViewById<ListView>(R.id.lv_completedTask)
        val listItems = arrayOfNulls<String>(users.size)
        for (i in 0 until users.size) {
            val listData = users[i]
            listItems[i] = listData.itemName
        }
        arrayAdapter = ArrayAdapter(this, R.layout.mylist, listItems)
        mlist.adapter = arrayAdapter

        fab_dashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add ToDo")
            val view: View = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.ev_todo)
            val pickerDate = view.findViewById<DatePicker>(R.id.pickerdate)
            val pickerTime = view.findViewById<TimePicker>(R.id.pickertime)
            val buttonSetAlarm = view.findViewById<Button>(R.id.setalarm)
            val now = Calendar.getInstance()
            pickerDate.init(
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH],
                null
            )
            buttonSetAlarm.setOnClickListener {
                val current = Calendar.getInstance()
                val cal = Calendar.getInstance()
                val toDo = ToDo()
                if (toDo.toDoAlarmHour.isEmpty()) {
                    cal[pickerDate.year, pickerDate.month, pickerDate.dayOfMonth, pickerTime.currentHour, pickerTime.currentMinute] =
                        0
                } else {
                    cal[toDo.toDoCalendarYear.toInt(), toDo.toDoCalendarMonth.toInt(), toDo.toDoCalendarDay.toInt(), toDo.toDoAlarmHour.toInt(), toDo.toDoAlarmMinutes.toInt()] =
                        0
                }
                if (cal.compareTo(current) <= 0) {
                    //The set Date/Time is already passed
                    Toast.makeText(applicationContext, "Invalid DateTime", Toast.LENGTH_LONG)
                        .show()
                } else {
                    setAlarm(cal)
                }
            }
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (toDoName.text.isEmpty()) {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Warning")
                    dialog.setMessage("Please enter the Task Title")
                    dialog.show()
                } else {
                    val toDo = ToDo()
                    toDo.name = toDoName.text.toString()
                    val alarmHour = alarmCalendar.get(Calendar.HOUR_OF_DAY).toString()
                    val alarmMinute = alarmCalendar.get(Calendar.MINUTE).toString()
                    val alarmYear = alarmCalendar.get(Calendar.YEAR).toString()
                    val alarmMonth = alarmCalendar.get(Calendar.MONTH).toString()
                    val alarmDay = alarmCalendar.get(Calendar.DAY_OF_MONTH).toString()
                    toDo.toDoAlarmHour = alarmHour
                    toDo.toDoAlarmMinutes = alarmMinute
                    toDo.toDoCalendarYear = alarmYear
                    toDo.toDoCalendarMonth = alarmMonth
                    toDo.toDoCalendarDay = alarmDay
                    Toast.makeText(
                        applicationContext,
                        "alarm Hour $alarmHour + alarm Minutes $alarmMinute",
                        Toast.LENGTH_LONG
                    ).show()
                    dbHandler.addToDo(toDo)
                    refreshList()
                }

            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }
    }

    fun updateToDo(toDo: ToDo) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update ToDo")
        val view: View = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.ev_todo)
        val pickerDate = view.findViewById<DatePicker>(R.id.pickerdate)
        val pickerTime = view.findViewById<TimePicker>(R.id.pickertime)
        toDoName.setText(toDo.name)
        pickerTime.hour(toDo.toDoAlarmHour)
        pickerTime.minute(toDo.toDoAlarmMinutes)
        val malarmHour = toDo.toDoAlarmHour
        val malarmMinute = toDo.toDoAlarmMinutes
        Toast.makeText(
            applicationContext,
            "Hour is $malarmHour,Minute is $malarmMinute ",
            Toast.LENGTH_LONG
        ).show()
        dialog.setView(view)

        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                toDo.name = toDoName.text.toString()
                val alarmHour = pickerTime.currentHour.toString()
                val alarmMinute = pickerTime.currentMinute.toString()
                val alarmYear = pickerDate.year.toString()
                val alarmMonth = pickerDate.month.toString()
                val alarmDay = pickerDate.dayOfMonth.toString()
                toDo.toDoAlarmHour = alarmHour
                toDo.toDoAlarmMinutes = alarmMinute
                toDo.toDoCalendarYear = alarmYear
                toDo.toDoCalendarMonth = alarmMonth
                toDo.toDoCalendarDay = alarmDay
                dbHandler.updateToDo(toDo)
                Toast.makeText(this, "Updated Task", Toast.LENGTH_SHORT).show()
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    fun setAlarm(targetCal: Calendar) {
        val intent = Intent(baseContext, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, intent, 0)
        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, targetCal.timeInMillis] = pendingIntent
        alarmCalendar = targetCal
        Toast.makeText(this, "Alarm setting completed", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        refreshList()
        super.onResume()
        dbHandler.getToDo()
    }

    private fun refreshList() {
        rv_dashboard.adapter = DashboardAdapter(this, dbHandler.getToDo())
    }


    private fun refreshListView() {
        dbHandler.getToDo()
    }

    private fun setLocate(Lang: String){
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Setting",Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang",Lang)
        editor.apply()
    }

    private fun loadLocate(){
        val sharePreferences = getSharedPreferences("Setting",Activity.MODE_PRIVATE)
        val language = sharePreferences.getString("MY_Lang","")
        if (language != null) {
            setLocate(language)
        }

    }

    fun CompletedList(): MutableList<ToDoItem> {
        var listA = dbHandler.getToDoItemsCompleted()
        return listA
    }


    class DashboardAdapter(val activity: DashBoardActivity, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, p0, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            var setAlarmTime = ""
            var alarmMinutes = "00"
            holder.toDoName.text = list[p1].name
            if (list[p1].toDoAlarmMinutes.length == 1) {
                alarmMinutes = "0" + list[p1].toDoAlarmMinutes
            } else {
                alarmMinutes = list[p1].toDoAlarmMinutes.toString()
            }
            if (list[p1].toDoAlarmHour != null && list[p1].toDoAlarmHour.toInt() > 12) {
                setAlarmTime = list[p1].toDoAlarmHour + ":" + alarmMinutes + " PM"
            } else {

                setAlarmTime = list[p1].toDoAlarmHour + ":" + alarmMinutes + " AM"
            }
            Log.d("--Show on View", list[p1].toDoAlarmHour)
            holder.todoAlarm.text = setAlarmTime


            holder.toDoName.setOnClickListener {
                val intent = Intent(activity, ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID, list[p1].id)
                intent.putExtra(INTENT_TODO_NAME, list[p1].name)
                activity.startActivity(intent)
            }
            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity, holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_edit -> {
                            activity.updateToDo(list[p1])
                        }
                        R.id.menu_delete -> {
                            val dialog = AlertDialog.Builder(activity)
                            dialog.setTitle("Confirm")
                            dialog.setMessage("Do you want to delete this task?")
                            dialog.setPositiveButton("Continue") { _: DialogInterface?, _: Int ->
                                activity.dbHandler.deleteToDo(list[p1].id)
                                activity.refreshList()
                            }
                            dialog.setNegativeButton("Cancel") { _: DialogInterface?, _: Int ->
                            }
                            dialog.show()
                        }
                        R.id.menu_mark_as_completed -> {
                            activity.dbHandler.updateToDOItemCompletedStatus(list[p1].id, true)
                            activity.refreshList()
                            activity.refreshListView()
                        }
                        R.id.menu_reset -> {
                            activity.dbHandler.updateToDOItemCompletedStatus(list[p1].id, false)

                        }
                        R.id.menu_eng -> {
                            activity.setLocate("en")
                            //recreate()
                        }
                        R.id.menu_jp -> {
                            activity.setLocate("ja")
                            //recreate()
                        }
                    }
                    true
                }
                popup.show()
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val toDoName: TextView = v.findViewById(R.id.tv_todo_name)
            val todoAlarm: TextView = v.findViewById(R.id.tv_todo_alarm)
            val menu: ImageView = v.findViewById(R.id.iv_menu)
        }
    }
}

operator fun Int.invoke(alarmHour: String) {

}


