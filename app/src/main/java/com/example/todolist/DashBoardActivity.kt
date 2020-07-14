package com.example.todolist

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItem
import kotlinx.android.synthetic.main.activity_dash_board.*
import java.util.*

class DashBoardActivity : AppCompatActivity() {
    lateinit var alarmCalendar: Calendar
    lateinit var dbHandler: DBHandler

    @RequiresApi(Build.VERSION_CODES.N)
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
        val completedTextView = findViewById<TextView>(R.id.tv_completed_task)
        val listItems = arrayOfNulls<String>(users.size)
        for (i in 0 until users.size) {
            val listData = users[i]
            listItems[i] = listData.itemName
        }
        if (users.size != 0) {
            arrayAdapter = ArrayAdapter(this, R.layout.mylist, listItems)
            mlist.adapter = arrayAdapter
        } else {
            completedTextView.visibility = View.GONE
        }

        //Del-list
        // ListViewを作成
        val deletedarrayAdapter: ArrayAdapter<*>
        val getDeletedList = DeletedList()
        val deletedListView = findViewById<ListView>(R.id.lv_deleted_task)
        val deletedTextView = findViewById<TextView>(R.id.tv_deleted_task)
        val deletedItems = arrayOfNulls<String>(getDeletedList.size)
        for (j in 0 until getDeletedList.size) {
            val delitem = getDeletedList[j]
            deletedItems[j] = delitem.name
        }
        if (getDeletedList.size != 0) {
            deletedarrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, deletedItems)
            deletedListView.adapter = deletedarrayAdapter
        } else {
            deletedTextView.visibility = View.GONE
        }

        //Del-list complete
        fab_dashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle(resources.getString(R.string.add_toDo))
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
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.invalid_datetime),
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    setAlarm(cal)
                }
            }
            dialog.setView(view)
            dialog.setPositiveButton(resources.getString(R.string.add)) { _: DialogInterface, _: Int ->

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
                toDo.isDeleted = false
                Toast.makeText(
                    applicationContext,
                    "alarm Hour $alarmHour + alarm Minutes $alarmMinute",
                    Toast.LENGTH_LONG
                ).show()
                dbHandler.addToDo(toDo)
                refreshList()


            }
            dialog.setNegativeButton(resources.getString(R.string.cancel)) { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }
    }

    fun updateToDo(toDo: ToDo) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(resources.getString(R.string.update_toDo))
        val view: View = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.ev_todo)
        val pickerDate = view.findViewById<DatePicker>(R.id.pickerdate)
        val pickerTime = view.findViewById<TimePicker>(R.id.pickertime)
        toDoName.setText(toDo.name)
        pickerTime.currentHour(7)
        pickerTime.currentMinute(3)
        if (toDo.toDoCalendarYear.isNotEmpty()) {
            pickerDate.init(
                toDo.toDoCalendarYear.toInt(),
                toDo.toDoCalendarMonth.toInt(),
                toDo.toDoCalendarDay.toInt(),
                null
            )
        } else {
            val now = Calendar.getInstance()
            pickerDate.init(
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH],
                null
            )

        }
        val malarmHour = toDo.toDoAlarmHour
        val malarmMinute = toDo.toDoAlarmMinutes
        Toast.makeText(
            applicationContext,
            "Hour is $malarmHour,Minute is $malarmMinute ",
            Toast.LENGTH_LONG
        ).show()
        dialog.setView(view)

        dialog.setPositiveButton(resources.getString(R.string.update)) { _: DialogInterface, _: Int ->
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
                toDo.isDeleted = false
                dbHandler.updateToDo(toDo)
                Toast.makeText(this, resources.getString(R.string.update_toDo), Toast.LENGTH_SHORT)
                    .show()
                refreshList()
            }
        }
        dialog.setNegativeButton(resources.getString(R.string.cancel)) { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    fun deleteToDo(toDo: ToDo) {
        toDo.isDeleted = true
        dbHandler.deleteToDo(toDo)
        Toast.makeText(this, resources.getString(R.string.delete_todo), Toast.LENGTH_SHORT)
            .show()
        refreshList()
    }

    fun setAlarm(targetCal: Calendar) {
        val intent = Intent(baseContext, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, intent, 0)
        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, targetCal.timeInMillis] = pendingIntent
        alarmCalendar = targetCal
        Toast.makeText(this, resources.getString(R.string.completed_alarm), Toast.LENGTH_SHORT)
            .show()
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

    private fun setLocate(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Setting", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun loadLocate() {
        val sharePreferences = getSharedPreferences("Setting", Activity.MODE_PRIVATE)
        val language = sharePreferences.getString("MY_Lang", "")
        if (language != null) {
            setLocate(language)
        }

    }

    fun CompletedList(): MutableList<ToDoItem> {
        var listA = dbHandler.getToDoItemsCompleted()
        return listA
    }


    fun DeletedList(): MutableList<ToDo> {
        var listB = dbHandler.getToDoItemDeleted()
        return listB
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
                setAlarmTime =
                    list[p1].toDoAlarmHour + ":" + alarmMinutes + " " + activity.resources.getString(
                        R.string.time_PM
                    )
            } else {

                setAlarmTime =
                    list[p1].toDoAlarmHour + ":" + alarmMinutes + " " + activity.resources.getString(
                        R.string.time_AM
                    )
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
                            dialog.setTitle(activity.resources.getString(R.string.confirm))
                            dialog.setMessage(activity.resources.getString(R.string.do_you_delete))
                            dialog.setPositiveButton(activity.resources.getString(R.string.continue_task)) { _: DialogInterface?, _: Int ->
                                activity.deleteToDo(list[p1])
                                activity.refreshList()
                            }
                            dialog.setNegativeButton(activity.resources.getString(R.string.cancel)) { _: DialogInterface?, _: Int ->
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

private operator fun CharSequence?.invoke(string: String) {

}

operator fun Int.invoke(alarmHour: Int) {

}


