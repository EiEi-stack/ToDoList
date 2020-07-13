package com.example.todolist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItem
import kotlinx.android.synthetic.main.activity_item.*
import java.util.*

private val TAG = "ItemActivity"

class ItemActivity : AppCompatActivity() {
    lateinit var dbHandler: DBHandler
    lateinit var alarmItemCalendar: Calendar
    var todoId: Long = -1
    var list: MutableList<ToDoItem>? = null
    var adapter: ItemAdapter? = null
    var touchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_TODO_NAME)
        todoId = intent.getLongExtra(INTENT_TODO_ID, -1)

        dbHandler = DBHandler(this)
        alarmItemCalendar = Calendar.getInstance()
        rv_item.layoutManager = LinearLayoutManager(this)

        fab_item.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add ToDoItem")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
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
                if (toDoName.text.isNotEmpty()) {
                    val alarmHour = alarmItemCalendar.get(Calendar.HOUR_OF_DAY).toString()
                    val alarmMinute = alarmItemCalendar.get(Calendar.MINUTE).toString()
                    val alarmYear = alarmItemCalendar.get(Calendar.YEAR).toString()
                    val alarmMonth = alarmItemCalendar.get(Calendar.MONTH).toString()
                    val alarmDay = alarmItemCalendar.get(Calendar.DAY_OF_MONTH).toString()
                    val item = ToDoItem()
                    item.itemName = toDoName.text.toString()
                    item.toDoId = todoId
                    item.isCompleted = false
                    item.toDoItemAlarmHour = alarmHour
                    item.toDoItemAlarmMinutes = alarmMinute
                    item.toDoItemCalendarYear = alarmYear
                    item.toDoItemCalendarMonth = alarmMonth
                    item.toDoItemCalendarDay = alarmDay
                    Toast.makeText(
                        applicationContext,
                        "Add item $alarmHour + $alarmMinute",
                        Toast.LENGTH_SHORT
                    ).show()
                    dbHandler.addToDoItem(item)
                    refreshList()
                }
                Log.d(TAG, "add item")
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

        touchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                Collections.swap(list, sourcePosition, targetPosition)
                adapter?.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }
        })
        touchHelper?.attachToRecyclerView(rv_item)
    }

    fun setAlarm(targetCal: Calendar) {
        val intent = Intent(baseContext, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, intent, 0)
        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, targetCal.timeInMillis] = pendingIntent
        alarmItemCalendar = targetCal
        Toast.makeText(this, "Alaram setting completed", Toast.LENGTH_SHORT).show()
    }

    fun updateItem(item: ToDoItem) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update ToDoItem")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.ev_todo)
        val pickerDate = view.findViewById<DatePicker>(R.id.pickerdate)
        val pickerTime = view.findViewById<TimePicker>(R.id.pickertime)
        toDoName.setText(item.itemName)
        pickerTime.hour(item.toDoItemAlarmHour)
        pickerTime.minute(item.toDoItemAlarmMinutes)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                val alarmHour = pickerTime.currentHour.toString()
                val alarmMinute = pickerTime.currentMinute.toString()
                val alarmYear = pickerDate.year.toString()
                val alarmMonth = pickerDate.month.toString()
                val alarmDay = pickerDate.dayOfMonth.toString()
                item.itemName = toDoName.text.toString()
                item.toDoId = todoId
                item.isCompleted = false
                item.toDoItemAlarmHour = alarmHour
                item.toDoItemAlarmMinutes = alarmMinute
                item.toDoItemCalendarYear = alarmYear
                item.toDoItemCalendarMonth = alarmMonth
                item.toDoItemCalendarDay = alarmDay
                dbHandler.updateToDoItem(item)
                Toast.makeText(this, "Updated Task Item", Toast.LENGTH_SHORT).show()
                refreshList()
            }
            Log.d(TAG, "Update item")
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    override fun onResume() {
        refreshList()
        super.onResume()

        Log.d(TAG, "onResume")
    }

    private fun refreshList() {
        list = dbHandler.getToDoItems(todoId)
        adapter = ItemAdapter(this, list!!)
        rv_item.adapter = adapter
    }

    class ItemAdapter(
        val activity: ItemActivity,
        val list: MutableList<ToDoItem>
    ) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.rv_child_item, p0, false)
            )

            Log.d(TAG, "onResume")
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            var setAlarmTime = ""
            var alarmMinutes = "00"
            if (list[p1].toDoItemAlarmMinutes.length == 1) {
                alarmMinutes = "0" + list[p1].toDoItemAlarmMinutes
            } else {
                alarmMinutes = list[p1].toDoItemAlarmMinutes.toString()
            }
            if (list[p1].toDoItemAlarmHour != null && list[p1].toDoItemAlarmHour.toInt() > 12) {
                setAlarmTime = list[p1].toDoItemAlarmHour + ":" + alarmMinutes + " PM"
            } else {
                setAlarmTime = list[p1].toDoItemAlarmHour + ":" + alarmMinutes + " AM"
            }
            holder.toDoItemAlarm.text = setAlarmTime
            holder.itemName.text = list[p1].itemName
            holder.itemName.isChecked = list[p1].isCompleted
            holder.itemName.setOnClickListener {
                list[p1].isCompleted = !list[p1].isCompleted
                activity.dbHandler.updateToDoItem(list[p1])

                Log.d(TAG, "Bind ViewHolder")
            }
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Confirm")
                dialog.setMessage("Do you want to delete this Item?")
                dialog.setPositiveButton("Continue") { _: DialogInterface?, _: Int ->
                    activity.dbHandler.deleteToDoItem(list[p1].id)
                    activity.refreshList()
                }
                dialog.setNegativeButton("Cancel") { _: DialogInterface?, _: Int ->
                }
                dialog.show()
            }
            holder.edit.setOnClickListener {
                activity.updateItem(list[p1])
            }
            holder.move.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.touchHelper?.startDrag(holder)
                }
                false
            }

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemName: CheckBox = v.findViewById(R.id.cb_item)
            val edit: ImageView = v.findViewById(R.id.iv_edit)
            val delete: ImageView = v.findViewById(R.id.iv_delete)
            val move: ImageView = v.findViewById(R.id.iv_move)
            val toDoItemAlarm: TextView = v.findViewById(R.id.tv_todo_item_alarm)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }

}

