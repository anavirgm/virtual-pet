package principal

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReduccionWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val prefs: SharedPreferences = applicationContext.getSharedPreferences("MascotaPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // 🔽 Reducimos progresivamente los valores
        val hambre = (prefs.getInt("hambre", 100) - 1).coerceAtLeast(0)
        val animo = (prefs.getInt("animo", 100) - 1).coerceAtLeast(0)
        val energia = (prefs.getInt("energia", 100) - 1).coerceAtLeast(0)

        editor.putInt("hambre", hambre)
        editor.putInt("animo", animo)
        editor.putInt("energia", energia)
        editor.apply()

        return Result.success()
    }
}
