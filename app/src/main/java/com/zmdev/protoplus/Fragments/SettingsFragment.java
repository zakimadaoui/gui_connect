package com.zmdev.protoplus.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.zmdev.protoplus.MainActivity;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.TutorialFlow;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_prefereces, rootKey);

        getPreferenceManager().findPreference("dark_mode").setOnPreferenceChangeListener((preference, newValue) -> {
            Toast.makeText(getContext(), "Applying theme...", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Runtime.getRuntime().exit(0);
            }, 400);
            return true;
        });

        getPreferenceManager().findPreference("interactive_tuto_pref").setOnPreferenceChangeListener((preference, newValue) -> {
            TutorialFlow.setTutorialsEnabled((Boolean) newValue);
            return true;
        });

        getPreferenceManager().findPreference("licenses_pref").setOnPreferenceClickListener((preference) -> {
            startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
            return true;
        });

        getPreferenceManager().findPreference("tutos_pref").setOnPreferenceClickListener((preference) -> {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/zakimadaoui/GuiConnectHelper")));
            return true;
        });

        getPreferenceManager().findPreference("tip_pref").setOnPreferenceClickListener((preference) -> {
            String donationUrl = "https://www.paypal.com/paypalme/zmdev77";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(donationUrl));
            startActivity(intent);
            return true;
        });
    }
}
