package com.soagent.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PluginResultsService extends IntentService {
    public static final String EXTRA_EXECUTION_ID = "execution_id";
    private static final String PREFS = "so_agent";
    private static final String RESULT_BUNDLE = "result";

    public PluginResultsService() {
        super("SOAgentResultsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        Bundle result = intent.getBundleExtra(RESULT_BUNDLE);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (result == null) {
            save("No se recibió el paquete de resultado. Comprueba el permiso RUN_COMMAND.",
                 now + "\nNo se recibió el paquete de resultado.");
            return;
        }

        int exitCode = result.getInt("exitCode", -999);
        int errCode = result.getInt("err", 0);
        String stdout = result.getString("stdout", "");
        String stderr = result.getString("stderr", "");
        String errMsg = result.getString("errmsg", "");

        StringBuilder text = new StringBuilder();
        text.append("Fecha: ").append(now).append("\n");
        text.append("Código de salida: ").append(exitCode).append("\n");
        text.append("Error interno Termux: ").append(errCode).append("\n");
        if (!stdout.isEmpty()) text.append("\nSalida:\n").append(stdout);
        if (!stderr.isEmpty()) text.append("\nErrores:\n").append(stderr);
        if (!errMsg.isEmpty()) text.append("\nDetalle:\n").append(errMsg);

        String status = (exitCode == 0 && errCode == -1)
            ? "Respaldo finalizado correctamente (código 0)."
            : "La ejecución terminó; revisa el detalle del resultado.";
        save(status, text.toString());
    }

    private void save(String status, String result) {
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        p.edit().putString("status", status).putString("last_result", result).apply();
    }
}
