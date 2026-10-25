package com.wrench.remote;

import android.app.Activity;
import android.graphics.Color;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.HashSet;

/** UI + local-network discovery prototype. No remote commands are sent yet. */
public class MainActivity extends Activity {
    private TextView status;
    private NsdManager nsd;
    private NsdManager.DiscoveryListener discovery;
    private final HashSet<String> found = new HashSet<>();
    private final int bg = Color.rgb(17, 21, 29);
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        nsd = (NsdManager) getSystemService(NSD_SERVICE);
        ScrollView scroll = new ScrollView(this); scroll.setBackgroundColor(bg);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(26, 34, 26, 28); scroll.addView(root);
        TextView title = new TextView(this); title.setText("WRENCH REMOTE"); title.setTextSize(27);
        title.setTextColor(Color.WHITE); title.setGravity(Gravity.CENTER); root.addView(title);
        status = new TextView(this); status.setText("Not connected · prototype");
        status.setTextColor(0xffaab6c9); status.setGravity(Gravity.CENTER); status.setPadding(0,20,0,20); root.addView(status);
        addRow(root, new String[]{"FIND GOOGLE TV"});
        addRow(root, new String[]{"POWER", "SETTINGS"});
        addRow(root, new String[]{"", "▲", ""});
        addRow(root, new String[]{"◀", "OK", "▶"});
        addRow(root, new String[]{"", "▼", ""});
        addRow(root, new String[]{"BACK", "HOME", "MENU"});
        addRow(root, new String[]{"VOL −", "MUTE", "VOL +"});
        addRow(root, new String[]{"REW", "PLAY/PAUSE", "FF"});
        addRow(root, new String[]{"KEYBOARD"});
        TextView note = new TextView(this);
        note.setText("Prototype: buttons show the intended layout. Discovery scans for Google Cast devices; pairing and command transport still need implementation and device testing.");
        note.setTextColor(0xffaab6c9); note.setPadding(0,24,0,0); root.addView(note);
        setContentView(scroll);
    }
    private void addRow(LinearLayout root, String[] names) {
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        for (String name : names) {
            Button button = new Button(this); button.setText(name); button.setAllCaps(false);
            button.setTextColor(Color.WHITE); button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xff33445c));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 68, 1); lp.setMargins(3,5,3,5);
            row.addView(button, lp);
            if (name.isEmpty()) { button.setVisibility(View.INVISIBLE); continue; }
            button.setOnClickListener(v -> { if (name.equals("FIND GOOGLE TV")) discover();
                else status.setText(name + " · command not connected yet"); });
        }
        root.addView(row);
    }
    private void discover() {
        if (discovery != null) { status.setText("Discovery already running"); return; }
        found.clear(); status.setText("Searching local Wi-Fi for Cast devices…");
        discovery = new NsdManager.DiscoveryListener() {
            @Override public void onDiscoveryStarted(String type) { }
            @Override public void onServiceFound(NsdServiceInfo info) {
                if (found.add(info.getServiceName())) runOnUiThread(() -> status.setText("Found: " + String.join(", ", found) + " · pairing pending"));
            }
            @Override public void onServiceLost(NsdServiceInfo info) { found.remove(info.getServiceName()); }
            @Override public void onDiscoveryStopped(String type) { discovery = null; }
            @Override public void onStartDiscoveryFailed(String type, int code) { error(code); }
            @Override public void onStopDiscoveryFailed(String type, int code) { error(code); }
            private void error(int code) { runOnUiThread(() -> status.setText("Discovery error: " + code)); discovery = null; }
        };
        try { nsd.discoverServices("_googlecast._tcp.", NsdManager.PROTOCOL_DNS_SD, discovery); }
        catch (Exception ex) { discovery = null; status.setText("Discovery unavailable: " + ex.getMessage()); }
    }
    @Override protected void onDestroy() {
        if (discovery != null) { try { nsd.stopServiceDiscovery(discovery); } catch (Exception ignored) {} }
        super.onDestroy();
    }
}
