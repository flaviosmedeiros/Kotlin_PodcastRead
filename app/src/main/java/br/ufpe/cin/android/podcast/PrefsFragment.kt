package br.ufpe.cin.android.podcast

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class PrefsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferenciasapp)
    }

}