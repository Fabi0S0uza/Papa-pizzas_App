package br.com.estudos.myapplication.ui.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import br.com.estudos.myapplication.Login;
import br.com.estudos.myapplication.R;

public class SettingsFragment extends Fragment {

    private FirebaseAuth auth;
    private Switch switchDarkMode;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        auth = FirebaseAuth.getInstance();
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Shared Preferences para salvar o estado do modo escuro
        sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("darkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        // Aplica o modo escuro
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Salva a preferência do usuário
            sharedPreferences.edit().putBoolean("darkMode", isChecked).apply();
        });

        // Logout do usuário
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(getContext(), "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();

            // Redireciona para a tela de login
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
