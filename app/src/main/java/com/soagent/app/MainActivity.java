package com.soagent.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView statusView;
    private TextView historyView;
    private int nextId = 1000;
    private static final String PREFS = "so_agent";
    private static final String SCRIPT = "/data/data/com.termux/files/home/so_androide_backup_job.sh";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        buildScreen();
        refreshHistory();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20);
        root.setPadding(pad, pad, pad, pad);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("SO-AGENT");
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        TextView description = new TextView(this);
        description.setText("Control del respaldo SO-ANDROIDE mediante Termux.");
        description.setTextSize(16);
        addSpace(root, 8);
        root.addView(description);

        Button backupButton = new Button(this);
        backupButton.setText("Ejecutar respaldo ahora");
        addSpace(root, 16);
        root.addView(backupButton, matchWrap());
        backupButton.setOnClickListener(v -> runBackup());

        Button refreshButton = new Button(this);
        refreshButton.setText("Actualizar historial");
        root.addView(refreshButton, matchWrap());
        refreshButton.setOnClickListener(v -> refreshHistory());

        statusView = new TextView(this);
        statusView.setTextSize(16);
        addSpace(root, 16);
        root.addView(statusView);

        TextView historyTitle = new TextView(this);
        historyTitle.setText("Último resultado recibido");
        historyTitle.setTextSize(18);
        historyTitle.setTypeface(null, Typeface.BOLD);
        addSpace(root, 18);
        root.addView(historyTitle);

        historyView = new TextView(this);
        historyView.setTextIsSelectable(true);
        historyView.setTextSize(14);
        addSpace(root, 8);
        root.addView(historyView);

        setContentView(scroll);
    }

    private void runBackup() {
        try {
            int id = nextId++;
            Intent command = new Intent();
            command.setClassName("com.termux", "com.termux.app.RunCommandService");
            command.setAction("com.termux.RUN_COMMAND");
            command.putExtra("com.termux.RUN_COMMAND_PATH", SCRIPT);
            command.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{});
            command.putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home");
            command.putExtra("com.termux.RUN_COMMAND_BACKGROUND", true);
            command.putExtra("com.termux.RUN_COMMAND_COMMAND_LABEL", "Respaldo SO-ANDROIDE");
            command.putExtra("com.termux.RUN_COMMAND_COMMAND_DESCRIPTION", "Ejecuta el script de respaldo existente.");

            Intent resultIntent = new Intent(this, PluginResultsService.class);
            resultIntent.putExtra(PluginResultsService.EXTRA_EXECUTION_ID, id);
            int flags = PendingIntent.FLAG_ONE_SHOT;
            if (Build.VERSION.SDK_INT >= 31) flags |= PendingIntent.FLAG_MUTABLE;
            PendingIntent resultPendingIntent = PendingIntent.getService(this, id, resultIntent, flags);
            command.putExtra("com.termux.RUN_COMMAND_PENDING_INTENT", resultPendingIntent);

            startService(command);
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putString("status", "Solicitud enviada: " + now + ". Esperando resultado.")
                .apply();
            refreshHistory();
            Toast.makeText(this, "Solicitud enviada a Termux", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            String msg = "No se pudo iniciar el respaldo: " + e.getClass().getSimpleName() + ": " + e.getMessage();
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString("status", msg).apply();
            refreshHistory();
        }
    }

    private void refreshHistory() {
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (statusView != null) statusView.setText(p.getString("status", "Listo. Pulsa el botón para ejecutar un respaldo."));
        if (historyView != null) historyView.setText(p.getString("last_result", "Todavía no se ha recibido ningún resultado."));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (statusView != null) refreshHistory();
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void addSpace(LinearLayout layout, int dp) {
        TextView spacer = new TextView(this);
        spacer.setHeight(dp(dp));
        layout.addView(spacer);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
