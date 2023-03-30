package itan.com.bluetoothle

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import java.io.*
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


object Utils{

    val TAG = Utils::class.java.simpleName

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    fun validatePassword(password: String): Boolean {
        if (password.length < 8) {
            return false
        }

        var hasUpperCase = false;
        var hasDigitCharacter = false;
        var hasLowerCase = false;

        var count = 0
        for (i in password.indices) {
            val character = password[i]
            when {
                Character.isDigit(character) -> {
                    hasDigitCharacter = true;
                    count++;
                }
                Character.isUpperCase(character) -> {
                    hasUpperCase = true;
                    count++;
                }
                Character.isLowerCase(character) -> {
                    hasLowerCase = true;
                    count++;
                }
            }
        }

        val hasSpecialCharacter = count != password.length

        if (!hasUpperCase || !hasDigitCharacter || !hasLowerCase || !hasSpecialCharacter) {
            return false
        }

        return true
    }

    fun dpToPixel(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun hideSoftKeyboard(view: View,context: Context) {
        val imm: InputMethodManager? =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun floatToBytes(f: Float): ByteArray {
        val bits = f.toBits()
        return byteArrayOf(
            (bits shr 24).toByte(),
            (bits shr 16).toByte(),
            (bits shr 8).toByte(),
            bits.toByte()
        )
    }

    fun bytesToFloat(byteArray: ByteArray): Float{
        val buffer = ByteBuffer.wrap(byteArray)
        return buffer.float
    }

    fun intTo4Bytes(data: Int, size: Int = 4): ByteArray = ByteArray(size) { i ->
        (data shr (i*8)).toByte()
    }.reversedArray()

    fun bytesToInt(buffers: ByteArray, offset: Int = 0): Int {
        val buffer = buffers.reversedArray()
        return (buffer[offset + 3].toUByte().toInt() shl 24) or
                (buffer[offset + 2].toUByte().toInt() and 0xff shl 16) or
                (buffer[offset + 1].toUByte().toInt() and 0xff shl 8) or
                (buffer[offset + 0].toUByte().toInt() and 0xff)
    }

    fun writeLogOnInternalStorage(mcoContext: Context, bytes: ByteArray) {
        val dir = File(mcoContext.filesDir, "log")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val fileName = SimpleDateFormat("yyMMdd").format(Date()) + "_log.txt"
            val gpxfile = File(dir, fileName)
            val writer = FileOutputStream(gpxfile, true)
            val out = OutputStreamWriter(writer)
            val line = bytes.toDataString()
            Log.e("Trung", "Line: $line")
            out.append(line)
            out.appendLine()
            out.close()
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Trung","writeLogOnInternalStorage got error: " + e.message)
        }
    }

    fun getLastIndexLogOnInternalStorage(mcoContext: Context): Pair<Int, Int>? {
        val dir = File(mcoContext.filesDir, "log")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val fileName = SimpleDateFormat("yyMMdd").format(Date()) + "_log.txt"
            val gpxfile = File(dir, fileName)
            var lastLine: String? = null
            if (gpxfile.exists()) {
                val reader = FileReader(gpxfile)
                val buff = BufferedReader(reader)
                var nextLine: String?
                nextLine = buff.readLine()
                while (nextLine != null) {
                    lastLine = nextLine
                    nextLine = buff.readLine()
                }
            }

            lastLine?.let {
                val s = it.split(",")
                return Pair(s[0].toInt(), s[1].toInt())
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Trung","writeLogOnInternalStorage got error: "+ e.message + "")
        }

        return null
    }

    fun getCurrentTime(): String {
        val timestamp = System.currentTimeMillis()

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp
        return DateFormat.format("HH-mm-ss-MM-dd-yyyy", calendar).toString()
    }

    fun genTreatmentId(suffix: Int): String {
        val timestamp = System.currentTimeMillis()

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp
        val date = DateFormat.format("MM-dd-yyyy", calendar).toString()
        val list = date.split("-")
        return "${list[0]}${list[1]}${list[2].substring(2)}_$suffix"
    }

    fun checkExistsAndAddSuffix(file: File, name : String, path : String): File {
        Log.d(TAG,"checkExistsAndAddSuffix name $name")
        Log.d(TAG,"checkExistsAndAddSuffix path $path")

        return if (file.exists()){
            val extension = file.extension
            var newFileName = name
            for (i in 1..Int.MAX_VALUE) {
                newFileName = file.nameWithoutExtension.plus("($i).").plus(extension)
                if (!File(path, newFileName).exists())
                    break
            }
            Log.d(TAG, "newFileName = $newFileName")
            File(path, newFileName)
        } else {
            file
        }
    }

    fun createFile(data: ByteArray, name : String, path : String) {
        Log.d(TAG,"createFile name ${name}")
        Log.d(TAG,"createFile path ${path}")
        val file = checkExistsAndAddSuffix(File(path, name), name, path)
        if (file.parentFile?.exists() == false) {
            file.parentFile?.mkdirs()
        }

        try {
            file.writeBytes(data)
            Log.d(TAG, "createTxtFile: --> parentFolderDir = $path")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "createTxtFile --> File write failed: $e")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "createTxtFile --> File write failed: $e")
        }

    }

    fun createLoadingDialog(context: Context): Dialog {
        return  Dialog(context).apply {
            setCancelable(false)
            //setContentView(R.layout.loading_view)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    fun getLSMFile(context: Context) : File {
        val dirLSM = File(getExternalDownloadPath(context) + "/LSM/")
        if (!dirLSM.exists()) {
            dirLSM.mkdir()
        }
        return dirLSM
    }

    fun getExternalDownloadPath(context: Context) : String {
        val root = context.getExternalFilesDir(null)?.absolutePath?.substringBefore("0")
        return "${root}0/download"
    }

    /*fun refreshFile(context: Context) {
        val path: String = context.getExternalFilesDir(null)?.absolutePath?.substringBefore("0") + "0/" + Environment.DIRECTORY_DOWNLOADS
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val mediaScanner = MediaScannerConnection(
                context, null
            )
            mediaScanner.connect()
            mediaScanner.scanFile(File(path).absolutePath, null)
            mediaScanner.disconnect()
        }
    }*/

    /**
     * Create the notification channel. Need to use channels only if your targetSdkVersion is 26 or higher
     * @param context Context in the activity
     * @param title The user visible name of the channel
     * @param description Set the user visible description of this channel
     */
    fun createNotificationChannel(
        context: Context,
        title: String = "submission",
        description: String = "Coming up submission"
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT)
                    .apply {
                        this.description = description
                        setShowBadge(true)
                        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    const val CHANNEL_ID = "my_channel"
    private val NOTIFICATION_CHANNEL_ID = "example.permanence"
    private const val GROUP_KEY_NOTIFY = "group_key_notify"
    private const val NOTIFICATION_ID = 101

    /**
     * Post a notification to be shown in the status bar, stream, etc.
     * @param context Context in the activity
     * @param title The user visible name of the channel
     * @param description Set the user visible description of this channel
     * @param notifyIcon Drawable resource will be set to icon in notification
     */
    /*fun sendNotification(
        context: Context,
        title: String,
        description: String,
        @DrawableRes notifyIcon: Int
    ) {
        createNotificationChannel(context, title, description)

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, Intent(), 0)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(notifyIcon)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(GROUP_KEY_NOTIFY)

        NotificationManagerCompat.from(context).apply {
            notify(NOTIFICATION_ID, builder.build())
        }
    }*/

    /**
     * Post a notification to be shown in the status bar, stream, etc.
     * @param context Context in the activity
     * @param title The user visible name of the channel
     * @param description Set the user visible description of this channel
     * @param notifyIcon Drawable resource will be set to icon in notification
     */
    /*fun sendNotificationAndStartActivity(
        context: Context,
        description: String,
        @DrawableRes notifyIcon: Int
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("yourpackage.notifyId", NOTIFICATION_CHANNEL_ID)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(notifyIcon)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(GROUP_KEY_NOTIFY)

        NotificationManagerCompat.from(context).apply {
            notify(2, builder.build())
        }
    }*/

    fun ByteArray.toDataString()  = "${bytesToInt(this.copyOfRange(0, 4))}," +
            "${bytesToInt(this.copyOfRange(4, 8))}," + "${this[8].toInt()}," +
            "${bytesToInt(this.copyOfRange(9, 13))}," + "${this[13].toInt()}," +
            "${bytesToFloat(this.copyOfRange(14, 18))}," + "${bytesToFloat(this.copyOfRange(18, 22))}," +
            "${bytesToFloat(this.copyOfRange(22, 26))}," + "${bytesToFloat(this.copyOfRange(26, 30))}," +
            "${bytesToFloat(this.copyOfRange(30, 34))}," + "${bytesToFloat(this.copyOfRange(34, 38))}," +
            "${bytesToFloat(this.copyOfRange(38, 42))}," + "${bytesToFloat(this.copyOfRange(42, 46))}"

    fun ByteArray.toEventData()  = PcmEvent(bytesToInt(this.copyOfRange(0, 4)),
            bytesToInt(this.copyOfRange(4, 8)), this[8].toInt(),
            bytesToInt(this.copyOfRange(9, 13)),  this[13].toInt(),
            bytesToFloat(this.copyOfRange(14, 18)), bytesToFloat(this.copyOfRange(18, 22)),
            bytesToFloat(this.copyOfRange(22, 26)), bytesToFloat(this.copyOfRange(26, 30)),
            bytesToFloat(this.copyOfRange(30, 34)), bytesToFloat(this.copyOfRange(34, 38)),
            bytesToFloat(this.copyOfRange(38, 42)), bytesToFloat(this.copyOfRange(42, 46)))

    fun timestampToHours(t: Int): Triple<Int, Int, Int> {
        val hh = t / (60 * 60)
        val mm = (t % 60) / 60
        val ss = (t % 60) / 60 - mm * 60
        return Triple(hh, mm, ss)
    }

    //2022-12-16T10:34:23.645Z
    private val months  = listOf("January", "February", "march", "April", "May", "June", "July", "August",
        "September", "October", "November" , "December")
    fun convertTimelineToString(s: String): String {
        val d = s.split("T")
        val t = d.first().split("-")
        return "${months[t[1].toInt() - 1]} ${t[2]}, ${t[0]}"
    }

    //2022-12-16T10:34:23.645Z
    fun isTodayTimeline(timeline: String): Boolean {
        val d = timeline.split("T")
        val t = d.first().split("-")

        val date = Date()
        val calender = Calendar.getInstance()
        calender.time = date

        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        return year == t[0].toInt() && month == t[1].toInt() && day == t[2].toInt()
    }

    fun startOfDay(): Date {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.HOUR_OF_DAY] = 0 //set hours to zero
        cal[Calendar.MINUTE] = 0 // set minutes to zero
        cal[Calendar.SECOND] = 0 //set seconds to zero
        Log.i("Time", cal.time.toString())
        return cal.time
    }
}